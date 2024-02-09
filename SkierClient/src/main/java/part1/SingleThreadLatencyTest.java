package part1;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SingleThreadLatencyTest {

  //static String BASE_URL = "http://localhost:8080/hw1_war_exploded";
  static String BASE_URL = "http://34.216.243.255:8080/hw1_war";

  public static void main(String[] args) {

    SkiersApi apiInstance = new SkiersApi(
        new ApiClient().setBasePath(BASE_URL));
    LiftRide testBody = new LiftRide().liftID(111).time(222);

    long st = System.currentTimeMillis();

    for (int i = 0; i < 10000; i++) {
      try {
        ApiResponse<Void> res = apiInstance.writeNewLiftRideWithHttpInfo(testBody, 56, "2024", "1",
            56);
        System.out.println(
            testBody + " POST req is sent." + "response code: " + res.getStatusCode());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    long ed = System.currentTimeMillis();
    System.out.println("Total time to send 10k reqs: " + (ed - st) + "ms");
    System.out.println("Latency per req: " + (double) (ed - st) / 10000 + " ms");
    System.out.println("Throughput: " + Math.round((double) 10000 / (ed - st) * 1000) + " reqs/s");


  }

}
