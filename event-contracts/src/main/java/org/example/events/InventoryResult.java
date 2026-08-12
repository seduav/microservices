package org.example.events;

public class InventoryResult {

    private String orderId;
    private InventoryStatus status;

    public InventoryResult() {
    }

    public InventoryResult(String orderId, InventoryStatus status) {
        this.orderId = orderId;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }
}