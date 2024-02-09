package part2;

import static part2.Runner2.TOTAL_REQS;

import java.util.concurrent.BlockingQueue;

public class LiftRideEventProducer2 implements Runnable {

  private final BlockingQueue<LiftRideEvent2> buffer;

  public LiftRideEventProducer2(BlockingQueue<LiftRideEvent2> q) {
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

  private LiftRideEvent2 produce() {
    return new LiftRideEvent2().genRandom();
  }


}
