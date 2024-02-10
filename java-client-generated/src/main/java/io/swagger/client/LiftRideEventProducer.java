package io.swagger.client;

import java.util.concurrent.BlockingQueue;

public class LiftRideEventProducer implements Runnable {

  private final BlockingQueue<LiftRideEvent> buffer;

  private final int NUM_ELE = 200000;

  public LiftRideEventProducer(BlockingQueue<LiftRideEvent> q) {
    buffer = q;
  }

  public void run() {
    for (int i = 0; i < NUM_ELE; i++) {
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
