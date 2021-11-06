package com.deepak.OrderService.command.api.saga;

import com.deepak.CommonService.commands.*;
import com.deepak.CommonService.events.*;
import com.deepak.CommonService.model.User;
import com.deepak.CommonService.queries.GetUserPaymentDetailsQuery;
import com.deepak.OrderService.command.api.events.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
@Slf4j
public class OrderProcessingSaga {

    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient QueryGateway queryGateway;

    public OrderProcessingSaga() {
    }


    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    private void handle(OrderCreatedEvent event){
        log.info("OrderCreatedEvent in Saga for Order Id : {}", event.getOrderId());
        GetUserPaymentDetailsQuery getUserPaymentDetailsQuery = new GetUserPaymentDetailsQuery(event.getUserId());

        User user = null;

        try {

            user = queryGateway.query(getUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class))
                    .join();
            log.info("user : {}" ,user);
        } catch (Exception e) {
            log.error(e.getMessage());
            //Start compensating transaction.
            cancelOrderCommand(event.getOrderId());
        }


        ValidatePaymentCommand validatePaymentCommand = ValidatePaymentCommand.builder()
                .paymentId(UUID.randomUUID().toString())
                .orderId(event.getOrderId())
                .cardDetails(user.getCardDetails())
                .build();
        commandGateway.sendAndWait(validatePaymentCommand);
    }

    private void cancelOrderCommand(String orderId) {

        CancelOrderCommand cancelOrderCommand = new CancelOrderCommand(orderId);
        commandGateway.send(cancelOrderCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    private void handle(PaymentProcessedEvent event){
        log.info("PaymentProcessedEvent in Saga for Order Id : {}", event.getOrderId());
        try {
            //if(true) throw new Exception("Exception occured");
            ShipOrderCommand shipOrderCommand = ShipOrderCommand.builder()
                    .shipmentId(UUID.randomUUID().toString())
                    .orderId(event.getOrderId())
                    .build();

            commandGateway.send(shipOrderCommand);
        } catch (Exception e) {
            log.error(e.getMessage());
            cancelPaymentCommand(event.getPaymentId(),event.getOrderId());
        }
    }

    private void cancelPaymentCommand(String paymentId,String orderId) {
        CancelPaymentCommand cancelPaymentCommand = new CancelPaymentCommand(paymentId, orderId );
        commandGateway.send(cancelPaymentCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderShippedEvent event){
        log.info("OrderShippedEvent in Saga for Order Id : {}", event.getOrderId());
        try {
            CompleteOrderCommand completeOrderCommand = CompleteOrderCommand.builder()
                    .orderId(event.getOrderId())
                    .orderStatus("APPROVED")
                    .build();

            commandGateway.send(completeOrderCommand);
        } catch (Exception e) {
            log.error(e.getMessage());
            cancelShipmentCommand( event);
        }
    }

    private void cancelShipmentCommand(OrderShippedEvent event) {
        CancelShipmentCommand cancelShipmentCommand = new CancelShipmentCommand(event.getShipmentId(),event.getOrderId());
        commandGateway.send(cancelShipmentCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderCompletedEvent event){
        log.info("OrderCompletedEvent in Saga for Order Id : {}", event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderCancelledEvent event){
        log.info("OrderCancelledEvent in Saga for Order Id : {}", event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentCancelledEvent event){
        log.info("PaymentCancelledEvent in Saga for Order Id : {}", event.getOrderId());
        cancelOrderCommand(event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ShipmentCancelledEvent event){
        log.info("ShipmentCancelledEvent in Saga for Order Id : {}", event.getOrderId());
        //cancelPaymentCommand(event.getOrderId(), );
    }

}
