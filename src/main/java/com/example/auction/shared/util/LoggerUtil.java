package com.example.auction.shared.util;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * LoggerUtil - Centralized logging for the entire system
 */
public class LoggerUtil {
  private static final Logger logger = Logger.getLogger("AuctionSystem");

  public static void info(String message) {
    logger.info(message);
  }

  public static void warning(String message) {
    logger.warning(message);
  }

  public static void error(String message, Exception e) {
    logger.log(Level.SEVERE, message, e);
  }

  public static void debug(String message) {
    logger.fine(message);
  }
}