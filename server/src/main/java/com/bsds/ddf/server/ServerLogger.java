package com.bsds.ddf.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Class to log any event on the server side to ServerLogs file. If the file is not present then the
 * file gets created first before logs are appended to it.
 */
public class ServerLogger {
  private static File logFile = null;
  private static String currUID;

  static {
    logFile = new File("LogsFromServer.txt");
  }

  public static void init(String UID) {
    currUID = UID;
  }

  public static void log(String message) {
    FileOutputStream fos = null;
    try {
      Date date = new Date(System.currentTimeMillis());
      fos = new FileOutputStream(logFile, true);
      message = String.format("%s --> Server %s: %s\n", date, currUID, message);
      System.out.println(message);
      fos.write(message.getBytes());
      fos.close();
    } catch (FileNotFoundException ex) {
      logFile = new File("LogsFromServer.txt");
      log(message);
    } catch (IOException e) {
      System.out.println("Failed due to IOException" + e.getMessage());
    }
  }
}
