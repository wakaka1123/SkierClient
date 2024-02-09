package part2;

public class Record {

  long st;
  long latency;
  int statusCode;

  public Record(long st, long latency, int statusCode) {
    this.st = st;
    this.latency = latency;
    this.statusCode = statusCode;
  }

  public synchronized long getSt() {
    return st;
  }

  public synchronized long getLatency() {
    return latency;
  }

  public synchronized int getStatusCode() {
    return statusCode;
  }

  public synchronized void setSt(long st) {
    this.st = st;
  }

  public synchronized void setLatency(long latency) {
    this.latency = latency;
  }

  public synchronized void setStatus(int statusCode) {
    this.statusCode = statusCode;
  }

}
