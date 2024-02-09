package part1;


import io.swagger.client.ApiClient;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Runner {

  public static final int NUM_THREADS = 32;
  public static final int REQ_PER_THREAD = 1000;
  public static final int TOTAL_REQS = 200000;
  static AtomicInteger successfulReqs = new AtomicInteger(0);
  static AtomicInteger failedReqs = new AtomicInteger(0);
  static CountDownLatch latch = new CountDownLatch(1);
  //static String BASE_URL = "http://localhost:8080/hw1_war_exploded";
  static String BASE_URL = "http://34.216.243.255:8080/hw1_war";

  public static void main(String[] args) throws InterruptedException {
    BlockingQueue<LiftRideEvent> buffer = new LinkedBlockingQueue<>();
    int moreThanCore = TOTAL_REQS / REQ_PER_THREAD - NUM_THREADS; // 200000 / 1000 - 32 = 168

    //start a single threaded producer
    (new Thread(new LiftRideEventProducer(buffer))).start();

    ExecutorService executor = new ThreadPoolExecutor(
        NUM_THREADS,
        moreThanCore + NUM_THREADS,
        0L,
        TimeUnit.MILLISECONDS,
        new SynchronousQueue<>(),
        new CallerRunsPolicy());
    //((ThreadPoolExecutor) executor).prestartAllCoreThreads();

    long st = System.currentTimeMillis();

    for (int i = 0; i < NUM_THREADS; i++) {
      executor.execute(
          new ClientThread(buffer, successfulReqs, failedReqs, latch,
              new ApiClient().setBasePath(BASE_URL)));
    }
    //http://localhost:8080/hw1_war_exploded
    //http://35.91.233.132:8080/hw1_war

    try {
      latch.await();
      for (int i = 0; i < TOTAL_REQS / REQ_PER_THREAD - NUM_THREADS; i++) {
        executor.execute(
            new ClientThread(buffer, successfulReqs, failedReqs, latch,
                new ApiClient().setBasePath(BASE_URL)));
      }
    } catch (InterruptedException e) {
      System.out.println("Failed to start more than core threads.");
      e.printStackTrace();
    }

    executor.shutdown();
    executor.awaitTermination(300, TimeUnit.SECONDS);
    long ed = System.currentTimeMillis();

    System.out.println(successfulReqs + " requests are successful");
    System.out.println(failedReqs + " requests are failed");
    System.out.println("Wall time: " + (ed - st) + " ms");
    System.out.println(
        "Throughput: " + Math.round((double) TOTAL_REQS / (ed - st) * 1000) + " reqs/s");

  }

}
