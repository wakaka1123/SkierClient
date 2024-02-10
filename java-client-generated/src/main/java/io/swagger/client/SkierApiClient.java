package io.swagger.client;

import static java.lang.Thread.sleep;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SkierApiClient {

  public static final int NUM_THREADS = 32;
  public static final int REQ_PER_THREAD = 1000;
  public static final int TOTAL_REQS = 200000;
  static AtomicInteger successfulReqs = new AtomicInteger(0);
  static AtomicInteger failedReqs = new AtomicInteger(0);
  static CountDownLatch latch = new CountDownLatch(1);

  public static void main(String[] args) throws InterruptedException {
    BlockingQueue<LiftRideEvent> buffer = new LinkedBlockingQueue<>();
    int moreThanCore = TOTAL_REQS / REQ_PER_THREAD - NUM_THREADS;

    //start a single threaded producer
    (new Thread(new LiftRideEventProducer(buffer))).start();

    ExecutorService executor = new ThreadPoolExecutor(
        NUM_THREADS,
        moreThanCore + NUM_THREADS,
        0L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(),
        new ThreadPoolExecutor.CallerRunsPolicy());
    //((ThreadPoolExecutor) executor).prestartAllCoreThreads();

    long st = System.currentTimeMillis();

    for (int i = 0; i < NUM_THREADS; i++) {
      executor.execute(
          new ClientThread(buffer, successfulReqs, failedReqs, latch,
              new ApiClient().setBasePath("http://35.93.45.113:8080/hw1_war")));
    }
    //http://localhost:8080/hw1_war_exploded

    try {
      latch.await();
      //latch = new CountDownLatch(moreThanCore + NUM_THREADS - 1);
      for (int i = 0; i < moreThanCore; i++) {
        executor.execute(
            new ClientThread(buffer, successfulReqs, failedReqs, latch,
                new ApiClient().setBasePath("http://35.93.45.113:8080/hw1_war")));
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
    System.out.println("Throughput: " + Math.round((double) TOTAL_REQS / (ed - st) * 1000) + " req/s");

  }

}
