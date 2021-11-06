package com.deepak.CommonService.events;

import lombok.Builder;
import lombok.Data;

@Data
public class ShipmentCancelledEvent {

    private String shipmentId;
    private String orderId;
    private String shipmentStatus;
}
