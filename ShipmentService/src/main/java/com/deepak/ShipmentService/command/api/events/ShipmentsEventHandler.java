package com.deepak.ShipmentService.command.api.events;

import com.deepak.CommonService.events.OrderShippedEvent;
import com.deepak.CommonService.events.ShipmentCancelledEvent;
import com.deepak.ShipmentService.command.api.data.Shipment;
import com.deepak.ShipmentService.command.api.data.ShipmentRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ShipmentsEventHandler {

    private ShipmentRepository shipmentRepository;

    public ShipmentsEventHandler(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @EventHandler
    public void on(OrderShippedEvent event){
        Shipment shipment = new Shipment();
        BeanUtils.copyProperties(event,shipment);
        shipmentRepository.save(shipment);
    }

    @EventHandler
    public void on(ShipmentCancelledEvent event){
        Shipment shipment = shipmentRepository.findById(event.getShipmentId()).get();
        shipment.setShipmentStatus(event.getShipmentStatus());
        shipmentRepository.save(shipment);
    }
}
