package com.stocks.omservice.service;

import com.stocks.omservice.client.InventoryClient;
import com.stocks.omservice.controller.InventoryController;
import com.stocks.omservice.entity.Order;
import com.stocks.omservice.enums.OrderStatus;
import com.stocks.omservice.repository.OrderRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final InventoryClient inventoryClient = null;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate = null;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order placeOrder(Order order) {
        boolean available = inventoryClient.checkAvailabilty(order.getProductId(), order.getQuantity());
        order.setStatus(!available || order.getType().equalsIgnoreCase("SELL") ? OrderStatus.PENDING : OrderStatus.CANCELLED);

        Order saved = orderRepository.save(order);
        kafkaTemplate.send("order-events", "Order placed with id" + order.getId() + "status" + order.getStatus());
        return saved;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Id not found" + id));
    }

    public Object updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        Order updated = orderRepository.save(order);
        kafkaTemplate.send("order-events", "Order Id" + id + "order status" + status);
        return updated;
    }

    public Object cancelOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() == OrderStatus.SUCCESS || order.getStatus()==OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already"+order.getStatus()+", can not cancel");
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order updated = orderRepository.save(order);
        kafkaTemplate.send("order-events","Order id"+id+"has been CANCELLED");
        return updated;
    }
}
