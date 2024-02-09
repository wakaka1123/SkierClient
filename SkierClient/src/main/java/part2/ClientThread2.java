package part2;

import static part2.Runner2.REQ_PER_THREAD;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientThread2 implements Runnable {

  private BlockingQueue<LiftRideEvent2> buffer;
  private AtomicInteger successfulReqs;
  private AtomicInteger failedReqs;
  private SkiersApi apiInstance;
  private CountDownLatch latch;

  public ClientThread2(BlockingQueue<LiftRideEvent2> q, AtomicInteger successfulReqs,
      AtomicInteger failedReqs, CountDownLatch latch, ApiClient apiClient) {
    this.buffer = q;
    this.successfulReqs = successfulReqs;
    this.failedReqs = failedReqs;
    this.apiInstance = new SkiersApi(apiClient);
    this.latch = latch;
  }

  @Override
  public void run() {
    for (int i = 0; i < REQ_PER_THREAD; i++) {
      try {
        LiftRideEvent2 body = (LiftRideEvent2) buffer.take();
        System.out.println("LiftRide: " + body.toString() + " is taken from buffer.");

        long st = System.currentTimeMillis();
        try {
          ApiResponse<Void> res = apiInstance.writeNewLiftRideWithHttpInfo(body.getLiftRide(),
              body.getResortID(), "2024", "1", body.getSkierID());
          long ed = System.currentTimeMillis();
          Record record = new Record(st, (ed - st), res.getStatusCode());
          Util.addRecord(record);
          System.out.println("LiftRide: " + body.getLiftRide().toString() + " POST req is sent.");
          if (res.getStatusCode() == 201) {
            successfulReqs.incrementAndGet();
          }
        } catch (ApiException e) {
          st = System.currentTimeMillis();
          int statusCode = retry(body);
          long ed = System.currentTimeMillis();
          Record record = new Record(st, (ed - st), statusCode);
          Util.addRecord(record);
          e.printStackTrace();
        }
      } catch (InterruptedException e) {
        System.out.println("Failed to take LiftRide from buffer.");
        throw new RuntimeException(e);
      }

    }
    latch.countDown();
  }

  private int retry(LiftRideEvent2 body) {
    int statusCode = 404;
    for (int i = 0; i < 5; i++) {
      System.out.println("Retrying " + (i + 1) + " time(s). to post LiftRide: " + body.getLiftRide()
          + " to server.");
      try {
        ApiResponse<Void> res = apiInstance.writeNewLiftRideWithHttpInfo(body.getLiftRide(),
            body.getResortID(), "2024", "1", body.getSkierID());
        statusCode = res.getStatusCode();
        if (statusCode == 201) {
          successfulReqs.incrementAndGet();
          return statusCode;
        }
      } catch (ApiException e) {
        e.printStackTrace();
      }
    }
    failedReqs.incrementAndGet();
    System.out.println("Failed to send POST req after 5 retries.");
    return statusCode;
  }

}
