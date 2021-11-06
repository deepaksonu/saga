package com.deepak.OrderService.command.api.aggregate;

import com.deepak.CommonService.commands.CancelOrderCommand;
import com.deepak.CommonService.commands.CompleteOrderCommand;
import com.deepak.CommonService.events.OrderCancelledEvent;
import com.deepak.CommonService.events.OrderCompletedEvent;
import com.deepak.OrderService.command.api.command.CreateOrderCommand;
import com.deepak.OrderService.command.api.events.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantity;
    private String orderStatus;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        //Add validation here for command
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand,orderCreatedEvent);

        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event){
        this.orderId = event.getOrderId();
        this.orderStatus = event.getOrderStatus();
        this.addressId = event.getAddressId();
        this.productId = event.getProductId();
        this.quantity = event.getQuantity();
        this.userId = event.getUserId();
    }

    @CommandHandler
    public void handle(CompleteOrderCommand completeOrderCommand){
        //Add validation for command
        //publish order completed event
        OrderCompletedEvent orderCompletedEvent = OrderCompletedEvent.builder()
                .orderId(completeOrderCommand.getOrderId())
                .orderStatus(completeOrderCommand.getOrderStatus())
                .build();

        AggregateLifecycle.apply(orderCompletedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent event){
        this.orderStatus = event.getOrderStatus();
    }

    @CommandHandler
    public void handle(CancelOrderCommand cancelOrderCommand){
        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent();
        BeanUtils.copyProperties(cancelOrderCommand,orderCancelledEvent);

        AggregateLifecycle.apply(orderCancelledEvent);
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event){
        this.orderStatus = event.getOrderStatus();
    }

}
