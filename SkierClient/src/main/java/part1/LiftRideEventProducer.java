package part1;

import static part1.Runner.TOTAL_REQS;

import java.util.concurrent.BlockingQueue;

public class LiftRideEventProducer implements Runnable {

  private final BlockingQueue<LiftRideEvent> buffer;

  public LiftRideEventProducer(BlockingQueue<LiftRideEvent> q) {
    buffer = q;
  }

  public void run() {
    for (int i = 0; i < TOTAL_REQS; i++) {
      try {
        buffer.put(produce());

      } catch (InterruptedException e) {
      }
    }
  }

  private LiftRideEvent produce() {
    return new LiftRideEvent().genRandom();
  }


}
