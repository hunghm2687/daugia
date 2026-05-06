package com.example.auction.server.service;

import com.example.auction.dao.AuctionDAO;
import com.example.auction.dao.ProductDAO;
import com.example.auction.server.ClientManager;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.util.LoggerUtil;

import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * AuctionScheduler - Automatic auction status transitions
 * - Checks every minute for auctions that need status changes
 * - PENDING -> ACTIVE (when start time reached)
 * - ACTIVE -> CLOSED (when end time reached)
 */
public class AuctionScheduler {
  private static AuctionScheduler instance;
  private Timer timer;
  private static final long CHECK_INTERVAL = 60000; // Check every 60 seconds

  private AuctionScheduler() {}

  public static AuctionScheduler getInstance() {
    if (instance == null) {
      synchronized (AuctionScheduler.class) {
        if (instance == null) {
          instance = new AuctionScheduler();
        }
      }
    }
    return instance;
  }

  /**
   * Start the scheduler
   */
  public void start() {
    if (timer != null) {
      LoggerUtil.warning("Scheduler already running");
      return;
    }

    timer = new Timer("AuctionScheduler", true);
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        checkAndUpdateAuctions();
      }
    }, 0, CHECK_INTERVAL);

    LoggerUtil.info("AuctionScheduler started - checking every 60 seconds");
  }

  /**
   * Stop the scheduler
   */
  public void stop() {
    if (timer != null) {
      timer.cancel();
      timer = null;
      LoggerUtil.info("AuctionScheduler stopped");
    }
  }

  /**
   * Check and update auction statuses
   */
  private void checkAndUpdateAuctions() {
    try {
      Instant now = Instant.now();

      // ============ PENDING -> ACTIVE ============
      List<AuctionDTO> pendingAuctions = ProductDAO.getInstance().getAuctionsByStatus("PENDING");

      for (AuctionDTO auction : pendingAuctions) {
        if (now.isAfter(auction.startTime())) {
          AuctionDAO.getInstance().updateStatus(auction.id(), "ACTIVE");

          // Broadcast to all clients
          MessageProtocol msg = new MessageProtocol(
            "AUCTION_STARTED",
            auction.id(),
            "SUCCESS",
            "Phiên " + auction.id() + " đã bắt đầu"
          );
          ClientManager.getInstance().broadcast(msg);

          LoggerUtil.info("Auction " + auction.id() + " transitioned from PENDING to ACTIVE");
        }
      }

      // ============ ACTIVE -> CLOSED ============
      List<AuctionDTO> activeAuctions = ProductDAO.getInstance().getAuctionsByStatus("ACTIVE");

      for (AuctionDTO auction : activeAuctions) {
        if (now.isAfter(auction.endTime())) {
          AuctionDAO.getInstance().updateStatus(auction.id(), "CLOSED");

          // Broadcast to all clients
          MessageProtocol msg = new MessageProtocol(
            "AUCTION_ENDED",
            auction.id(),
            "SUCCESS",
            "Phiên " + auction.id() + " đã kết thúc"
          );
          ClientManager.getInstance().broadcast(msg);

          LoggerUtil.info("Auction " + auction.id() + " transitioned from ACTIVE to CLOSED");
        }
      }

    } catch (Exception e) {
      LoggerUtil.error("Error in AuctionScheduler", e);
    }
  }
}