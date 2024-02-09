package part1;

import static part1.Runner.REQ_PER_THREAD;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientThread implements Runnable {

  private BlockingQueue<LiftRideEvent> buffer;
  private AtomicInteger successfulReqs;
  private AtomicInteger failedReqs;
  private SkiersApi apiInstance;
  private CountDownLatch latch;

  public ClientThread(BlockingQueue<LiftRideEvent> q, AtomicInteger successfulReqs, AtomicInteger failedReqs, CountDownLatch latch, ApiClient apiClient) {
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
        LiftRideEvent body = (LiftRideEvent) buffer.take();
        System.out.println("LiftRide: " + body.toString() + " is taken from buffer.");

        int retries = 0;
        while (retries < 5) {
          try {
            ApiResponse<Void> res = apiInstance.writeNewLiftRideWithHttpInfo(
                body.getLiftRide(), body.getResortID(), "2024", "1", body.getSkierID());
            System.out.println("LiftRide: " + body.getLiftRide().toString() + " POST req is sent.");

            if (res.getStatusCode() >= 400 && res.getStatusCode() < 600) {
              System.out.println("Network or server failed with status code: " + res.getStatusCode());
              retries++;
            } else if (res.getStatusCode() == 201) {
              successfulReqs.incrementAndGet();
              break;
            } else {
              System.out.println("Unexpected status code: " + res.getStatusCode());
              retries++;
            }
          } catch (ApiException e) {
            retries++;
            System.out.println("Exception thrown sending POST req. Retrying " + retries + " time(s).");
            e.printStackTrace();
          }
        }
        if (retries == 5) {
          failedReqs.incrementAndGet();
          System.out.println("Failed to send POST req after 5 retries.");
        }
      } catch (InterruptedException e) {
        System.out.println("Failed to take LiftRide from buffer.");
        throw new RuntimeException(e);
      }

    }
    latch.countDown();
  }

}
