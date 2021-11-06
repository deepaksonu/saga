package com.deepak.CommonService.commands;

import lombok.Data;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class CancelShipmentCommand {

    @TargetAggregateIdentifier
    private String shipmentId;
    private String orderId;
    private String shipmentStatus = "CANCELLED";
}
