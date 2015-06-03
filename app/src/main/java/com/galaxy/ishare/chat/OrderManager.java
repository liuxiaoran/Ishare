package com.galaxy.ishare.chat;

import com.galaxy.ishare.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhan on 2015/6/2.
 */
public class OrderManager {
    private static OrderManager instance;
    public List<Order> orderList;
    public Order order;

    private OrderManager() {
        orderList = new ArrayList<>();
    }

    public static OrderManager getInstance() {
        if(instance == null) {
            instance = new OrderManager();
        }

        return instance;
    }
}
