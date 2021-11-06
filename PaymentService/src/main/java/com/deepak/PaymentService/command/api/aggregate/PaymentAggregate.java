package com.deepak.PaymentService.command.api.aggregate;

import com.deepak.CommonService.commands.CancelPaymentCommand;
import com.deepak.CommonService.commands.ValidatePaymentCommand;
import com.deepak.CommonService.events.PaymentCancelledEvent;
import com.deepak.CommonService.events.PaymentProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Slf4j
@Aggregate
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private String paymentStatus;

    public PaymentAggregate() {
    }

    @CommandHandler
    public PaymentAggregate(ValidatePaymentCommand validatePaymentCommand) {
        //Validate payment details
        //publish payment processed event
        log.info("Executing validate payment command for order Id: {} and payment Id: {}",validatePaymentCommand.getOrderId(), validatePaymentCommand.getPaymentId());

        PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(validatePaymentCommand.getPaymentId(), validatePaymentCommand.getOrderId());
        AggregateLifecycle.apply(paymentProcessedEvent);
        log.info("PaymentProcessedEvent applied");

    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event){
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
    }

    @CommandHandler
    public void handle(CancelPaymentCommand cancelPaymentCommand){
        PaymentCancelledEvent event = new PaymentCancelledEvent();
        BeanUtils.copyProperties(cancelPaymentCommand,event);

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PaymentCancelledEvent event){
        this.paymentStatus = event.getPaymentStatus();
    }
}
