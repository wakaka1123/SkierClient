package part2;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

  private static Long START_TIME = 0L;

  public static List<Record> records = new ArrayList<>();

  public static synchronized void addRecord(Record record) {
    records.add(record);
  }

  public static long getMeanLatency() {
    long sum = 0;
    for (Record record : records) {
      sum += record.getLatency();
    }
    return sum / records.size();
  }

  public static long getMedianLatency() {
    records.sort(Comparator.comparingLong(Record::getLatency));
    if (records.size() % 2 == 0) {
      return (records.get(records.size() / 2).getLatency() + records.get(records.size() / 2 - 1).getLatency()) / 2;
    } else {
      return records.get(records.size() / 2).getLatency();
    }
  }

  public static int getThroughput() {
    records.sort(Comparator.comparingLong(Record::getSt));
    long start = records.get(0).getSt();
    long end = records.get(records.size() - 1).getSt() + records.get(records.size() - 1).getLatency();
    return (int) (records.size() / ((end - start) / 1000));
  }

  public static double getP99Latency() {
    records.sort(Comparator.comparingLong(Record::getLatency));
    int index = (int) (records.size() * 0.99);
    return records.get(index).getLatency();
  }

  public static long getMinLatency() {
    records.sort(Comparator.comparingLong(Record::getLatency));
    return records.get(0).getLatency();
  }

  public static long getMaxLatency() {
    records.sort(Comparator.comparingLong(Record::getLatency));
    return records.get(records.size() - 1).getLatency();
  }

  public static void writeToCSV(List<Record> records, String filename) {
    SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try (FileWriter writer = new FileWriter(filename)) {
      //header row
      writer.append("Start Time (ms)").append(",")
          .append("Start Time (yyyy-MM-dd HH:mm:ss)").append(",")
          .append("Latency (ms)").append(",")
          .append("Status Code").append("\n");
      //data rows
      for (Record record : records) {
        writer.append(String.valueOf(record.getSt())).append(",")
            .append(sdf.format(new Date(record.getSt()))).append(",")
            .append(String.valueOf(record.getLatency())).append(",")
            .append(String.valueOf(record.getStatusCode())).append("\n");
      }
    } catch (IOException e) {
      System.out.println("Failed to write to CSV file.");
      e.printStackTrace();
    }
  }

  public static List<Long> getEndTimes() {
    List<Long> endTimes = new ArrayList<>();
    for (Record record : records) {
      endTimes.add(record.getSt() + record.getLatency());
    }
    records.sort(Comparator.comparingLong(Record::getSt));
    START_TIME = records.get(0).getSt();
    return endTimes;
  }

  public static Map<Integer, Integer> getThroughputsPerSecondMapping() {
    List<Long> endTimes = getEndTimes();
    Map<Integer, Integer> mp = new HashMap<>();
    List<Long> diff = new ArrayList<>();
    for (long eds : endTimes) {
      diff.add((eds - START_TIME) / 1000);
    }
    for (long dif : diff) {
      int key = (int) dif;
      mp.put(key, mp.getOrDefault(key, 0) + 1);
    }
    return mp;
  }

  public static void plotTps(Map<Integer, Integer> mp, String filename) {
    try (FileWriter writer = new FileWriter(filename)) {
      //header row
      writer.append("Time (s)").append(",")
          .append("Throughput (reqs)").append("\n");
      //data rows
      for (Map.Entry<Integer, Integer> entry : mp.entrySet()) {
        writer.append(String.valueOf(entry.getKey())).append(",")
            .append(String.valueOf(entry.getValue())).append("\n");
      }
    } catch (IOException e) {
      System.out.println("Failed to write to CSV file.");
      e.printStackTrace();
    }
  }

}
