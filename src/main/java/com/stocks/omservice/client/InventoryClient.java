package com.stocks.omservice.client;

import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class InventoryClient {
    public boolean checkAvailabilty(@NonNull Long productId, int quantity) {
        return true;
    }
}
