package part1;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

public class SkiersApiExample {
  //static String BASE_URL = "http://localhost:8080/hw1_war_exploded";
  static String BASE_URL = "http://54.189.174.77:8080/hw1_war";

  public static void main(String[] args) {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(BASE_URL);
    SkiersApi apiInstance = new SkiersApi(apiClient);
    Integer resortID = 56;
    String seasonID = "56";
    String dayID = "56";
    Integer skierID = 56;

    LiftRide testRide = new LiftRide();
    testRide.setLiftID(111);
    testRide.setTime(222);

    try {
      //test get request
      Integer getRes = apiInstance.getSkierDayVertical(resortID, seasonID, dayID, skierID);
      System.out.println("Test GET request returns " + getRes);

      //test post request
      System.out.println(testRide);
      ApiResponse<Void> postRes = apiInstance.writeNewLiftRideWithHttpInfo(testRide, resortID, seasonID, dayID, skierID);
      System.out.println(postRes.getStatusCode());
      //apiInstance.writeNewLiftRide(testRide, resortID, seasonID, dayID, skierID);

    } catch (ApiException e) {
      System.err.println("Exception when calling SkierApi#getSkierDayVertical");
      e.printStackTrace();
    }
  }

}
