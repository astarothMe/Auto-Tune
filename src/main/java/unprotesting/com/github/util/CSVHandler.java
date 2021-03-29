package unprotesting.com.github.util;

import unprotesting.com.github.Main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CSVHandler {

    public static void writeCSV() throws
            IOException {
    FileWriter csvWriter = new FileWriter("plugins/Auto-Tune/web/trade.csv");

    Set < String > strSet = Main.map.keySet();
      List<String> strList = new ArrayList<>(strSet);
    Collections.sort(strList);
    for (String str : strList) {
      ConcurrentHashMap < Integer,
      Double[] > item = Main.map.get(str);

      csvWriter.append("\n");
      csvWriter.append("%").append(str);
      csvWriter.append(",");
      csvWriter.append("\n");

      for (int i = 0; i > -1; i++) {
        String k = String.valueOf(i);
        csvWriter.append(k);
        Double[] l = (item.get(i));
        if (l == null) {
          break;
        }
        double SP = l[0];
        String parsedSP = String.valueOf(SP);
        csvWriter.append(",");
        csvWriter.append(parsedSP);
        double Buy = l[1];
        String parsedBuy = String.valueOf(Buy);
        csvWriter.append(",");
        csvWriter.append(parsedBuy);
        double Sell = l[2];
        String parsedSell = String.valueOf(Sell);
        csvWriter.append(",");
        csvWriter.append(parsedSell);
        csvWriter.append("\n");
      }
      csvWriter.append("\n");
    }
    // for (List<String> rowData : rows) {
    //     csvWriter.append(String.join(",", rowData));
    //     csvWriter.append("\n");
    // }
    csvWriter.flush();
    csvWriter.close();

  }

  public static void writeShortCSV() throws
          IOException {
    FileWriter csvWriter = new FileWriter("plugins/Auto-Tune/web/trade-short.csv");

    Set < String > strSet = Main.map.keySet();
    List<String> strList = new ArrayList<>(strSet);
    Collections.sort(strList);
    for (String str : strList) {
      ConcurrentHashMap < Integer,
      Double[] > item = Main.map.get(str);

      csvWriter.append("\n");
      csvWriter.append("%").append(str);
      csvWriter.append(",");
      csvWriter.append("\n");

      int size = item.size();

      for (int i = size-Config.getMaximumShortTradeLength(); i < size; i++) {
        if (i < 0){
          continue;
        }
        String k = String.valueOf(i);
        csvWriter.append(k);
        Double[] l = (item.get(i));
        if (l == null) {
          break;
        }
        double SP = l[0];
        String parsedSP = String.valueOf(SP);
        csvWriter.append(",");
        csvWriter.append(parsedSP);
        double Buy = l[1];
        String parsedBuy = String.valueOf(Buy);
        csvWriter.append(",");
        csvWriter.append(parsedBuy);
        double Sell = l[2];
        String parsedSell = String.valueOf(Sell);
        csvWriter.append(",");
        csvWriter.append(parsedSell);
        csvWriter.append("\n");
      }
      csvWriter.append("\n");
    }
    // for (List<String> rowData : rows) {
    //     csvWriter.append(String.join(",", rowData));
    //     csvWriter.append("\n");
    // }
    csvWriter.flush();
    csvWriter.close();

  }
    
}