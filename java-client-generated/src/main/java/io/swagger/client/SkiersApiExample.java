package io.swagger.client;

import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

public class SkiersApiExample {

  public static void main(String[] args) {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath("http://localhost:8080/hw1_war_exploded");
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
      //System.out.println(postRes.getStatusCode());
      //apiInstance.writeNewLiftRide(testRide, resortID, seasonID, dayID, skierID);

    } catch (ApiException e) {
      System.err.println("Exception when calling SkierApi#getSkierDayVertical");
      e.printStackTrace();
    }
  }

}
