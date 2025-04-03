package com.footballstore.scheduler;

import com.footballstore.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CartCleanupScheduler {

    private final CartService cartService;

    @Autowired
    public CartCleanupScheduler(CartService cartService) {
        this.cartService = cartService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    //@Scheduled(cron = "0 * * * * ?") // Runs every minute for testing
    public void cleanUpAbandonedCarts() {

        log.info("Running cart cleanup task...");
        cartService.cleanUpOldCarts();
    }
}
