package part1;

import io.swagger.client.model.LiftRide;
import java.util.concurrent.ThreadLocalRandom;

public class LiftRideEvent {

  private LiftRide liftRide = new LiftRide().liftID(0).time(0);

  private int skierID = 0;
  private int resortID = 0;
  private int seasonID = 2024;
  private int dayID = 1;

  public LiftRideEvent() {}

  public LiftRideEvent(LiftRide liftRide, int skierID, int resortID, int seasonID, int dayID) {
    this.liftRide = liftRide;
    this.skierID = skierID;
    this.resortID = resortID;
    this.seasonID = seasonID;
    this.dayID = dayID;
  }

  public LiftRideEvent genRandom() {
    int ranTime = ThreadLocalRandom.current().nextInt(1, 101);
    int ranLiftID = ThreadLocalRandom.current().nextInt(1,41);
    LiftRide liftRide = new LiftRide().time(ranTime).liftID(ranLiftID);

    int ranSkierID = ThreadLocalRandom.current().nextInt(1, 100001);
    int ranResortID = ThreadLocalRandom.current().nextInt(1, 11);

    return new LiftRideEvent(liftRide, ranSkierID, ranResortID, 2024, 1);
  }

  public LiftRide getLiftRide() {
    return liftRide;
  }

  public int getSkierID() {
    return skierID;
  }

  public int getResortID() {
    return resortID;
  }

  public int getSeasonID() {
    return seasonID;
  }

  public int getDayID() {
    return dayID;
  }
}