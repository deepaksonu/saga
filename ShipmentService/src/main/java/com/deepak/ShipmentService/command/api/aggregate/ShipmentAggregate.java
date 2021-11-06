package com.deepak.ShipmentService.command.api.aggregate;

import com.deepak.CommonService.commands.CancelShipmentCommand;
import com.deepak.CommonService.commands.ShipOrderCommand;
import com.deepak.CommonService.events.OrderShippedEvent;
import com.deepak.CommonService.events.ShipmentCancelledEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;

@Aggregate
public class ShipmentAggregate {

    @AggregateIdentifier
    private String shipmentId;
    private String orderId;
    private String shipmentStatus;

    public ShipmentAggregate() {
    }

    @CommandHandler
    public ShipmentAggregate(ShipOrderCommand shipOrderCommand) {
        //Validate Ship order commmand
        //publish OrderShipevent

        OrderShippedEvent orderShippedEvent = OrderShippedEvent.builder()
                .shipmentId(shipOrderCommand.getShipmentId())
                .orderId(shipOrderCommand.getOrderId())
                .shipmentStatus("COMPLETED")
                .build();

        AggregateLifecycle.apply(orderShippedEvent);

    }

    @EventSourcingHandler
    public void on(OrderShippedEvent event){
        this.orderId = event.getOrderId();
        this.shipmentId = event.getShipmentId();
        this.shipmentStatus = event.getShipmentStatus();
    }

    @CommandHandler
    public void handle(CancelShipmentCommand cancelShipmentCommand){
        ShipmentCancelledEvent shipmentCancelledEvent = new ShipmentCancelledEvent();
        BeanUtils.copyProperties(cancelShipmentCommand,shipmentCancelledEvent);
        AggregateLifecycle.apply(shipmentCancelledEvent);
    }

    @EventSourcingHandler
    public void on(ShipmentCancelledEvent event){
        this.shipmentStatus = event.getShipmentStatus();
    }
}
