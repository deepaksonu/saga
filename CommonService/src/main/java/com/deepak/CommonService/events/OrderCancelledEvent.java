package com.deepak.CommonService.events;

import lombok.Builder;
import lombok.Data;

@Data
public class OrderCancelledEvent {
    private String orderId;
    private String orderStatus;
}
