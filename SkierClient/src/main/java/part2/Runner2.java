package part2;


import io.swagger.client.ApiClient;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Runner2 {

  public static final int NUM_THREADS = 32;
  public static final int REQ_PER_THREAD = 1000;
  public static final int TOTAL_REQS = 200000;
  static AtomicInteger successfulReqs = new AtomicInteger(0);
  static AtomicInteger failedReqs = new AtomicInteger(0);
  static CountDownLatch latch = new CountDownLatch(1);
  //static String BASE_URL = "http://localhost:8080/hw1_war_exploded";
  static String BASE_URL = "http://34.217.57.105:8080/hw1_war";

  public static void main(String[] args) throws InterruptedException {
    BlockingQueue<LiftRideEvent2> buffer = new LinkedBlockingQueue<>();
    int moreThanCore = TOTAL_REQS / REQ_PER_THREAD - NUM_THREADS; // 200000 / 1000 - 32 = 168

    //start a single threaded producer
    (new Thread(new LiftRideEventProducer2(buffer))).start();

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
          new ClientThread2(buffer, successfulReqs, failedReqs, latch,
              new ApiClient().setBasePath(BASE_URL)));
    }

    try {
      latch.await();
      for (int i = 0; i < TOTAL_REQS / REQ_PER_THREAD - NUM_THREADS; i++) {
        executor.execute(
            new ClientThread2(buffer, successfulReqs, failedReqs, latch,
                new ApiClient().setBasePath(BASE_URL)));
      }
    } catch (InterruptedException e) {
      System.out.println("Failed to start more than core threads.");
      e.printStackTrace();
    }

    executor.shutdown();
    executor.awaitTermination(300, TimeUnit.SECONDS);
    long ed = System.currentTimeMillis();

    System.out.println("----------Part 1 starts----------");
    System.out.println(successfulReqs + " requests are successful");
    System.out.println(failedReqs + " requests are failed");
    System.out.println("Number of threads initially: " + NUM_THREADS + ", max number of threads: "
        + (NUM_THREADS + moreThanCore));
    System.out.println("Wall time: " + (ed - st) + " ms");
    System.out.println(
        "Throughput: " + Math.round((double) TOTAL_REQS / (ed - st) * 1000) + " reqs/s");
    System.out.println("----------Part 2 starts----------");
    System.out.println("Mean response time: " + Util.getMeanLatency() + " ms");
    System.out.println("Median response time: " + Util.getMedianLatency() + " ms");
    System.out.println("Throughput: " + Util.getThroughput() + " reqs/s");
    System.out.println("99th percentile response time: " + Util.getP99Latency() + " ms");
    System.out.println("Min response time: " + Util.getMinLatency() + " ms");
    System.out.println("Max response time: " + Util.getMaxLatency() + " ms");
    Util.writeToCSV(Util.records, "records-200k-32.csv");
    Map<Integer, Integer> tpsMap = Util.getThroughputsPerSecondMapping();
    Util.plotTps(tpsMap, "tps-200k-32.csv");

  }

}

