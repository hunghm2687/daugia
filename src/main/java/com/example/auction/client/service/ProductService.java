package com.example.auction.client.service;

import com.example.auction.client.AppContext;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.MessageProtocol;

import java.util.Collections;
import java.util.List;

/**
 * ProductService - Client-side product/auction operations.
 */
public class ProductService {

    private static ProductService instance;

    private ProductService() {}

    public static ProductService getInstance() {
        if (instance == null) {
            synchronized (ProductService.class) {
                if (instance == null) {
                    instance = new ProductService();
                }
            }
        }
        return instance;
    }

    /**
     * Fetch active auction list from server.
     * Returns an empty list on error.
     */
    @SuppressWarnings("unchecked")
    public List<AuctionDTO> getProducts() throws Exception {
        MessageProtocol request = new MessageProtocol("GET_PRODUCTS_LIST", null, null, null);
        MessageProtocol response = AppContext.getInstance().sendAndReceive(request);

        if ("SUCCESS".equals(response.status())) {
            return (List<AuctionDTO>) response.data();
        }
        return Collections.emptyList();
    }
}
