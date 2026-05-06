package com.example.auction.client.service;

import com.example.auction.client.AppContext;
import com.example.auction.shared.dto.BidDTO;
import com.example.auction.shared.dto.MessageProtocol;

import java.time.Instant;

/**
 * BidService - Client-side bid operations.
 */
public class BidService {

    private static BidService instance;

    private BidService() {}

    public static BidService getInstance() {
        if (instance == null) {
            synchronized (BidService.class) {
                if (instance == null) {
                    instance = new BidService();
                }
            }
        }
        return instance;
    }

    /**
     * Send a BID request to the server.
     * Returns the server response (check response.status() for "SUCCESS"/"ERROR").
     */
    public MessageProtocol placeBid(Long auctionId, double amount) throws Exception {
        BidDTO bidDTO = new BidDTO(
            auctionId,
            AppContext.getInstance().getCurrentUsername(),
            amount,
            Instant.now(),
            "PENDING"
        );
        MessageProtocol request = new MessageProtocol("BID", bidDTO, null, null);
        return AppContext.getInstance().sendAndReceive(request);
    }
}
