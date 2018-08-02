package com.ecommerce.customer.fypproject.adapter;

class Order {
    private String orderNumber;
    private String numberPackingItem;
    private String numberReadyDeliverItem;
    private String totalItem;
    private String date;
    private String numberDeliveredItem;
    private String numberReadyCollectItem;

    public String getNumberReadyCollectItem() {
        return numberReadyCollectItem;
    }

    public void setNumberReadyCollectItem(String numberReadyCollectItem) {
        this.numberReadyCollectItem = numberReadyCollectItem;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getNumberPackingItem() {
        return numberPackingItem;
    }

    public void setNumberPackingItem(String numberPackingItem) {
        this.numberPackingItem = numberPackingItem;
    }

    public String getNumberReadyDeliverItem() {
        return numberReadyDeliverItem;
    }

    public String getNumberDeliveredItem() {
        return numberDeliveredItem;
    }

    public void setNumberDeliveredItem(String numberDeliveredItem) {
        this.numberDeliveredItem = numberDeliveredItem;
    }

    public void setNumberReadyDeliverItem(String numberReadyDeliverItem) {
        this.numberReadyDeliverItem = numberReadyDeliverItem;

    }

    public String getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(String totalItem) {
        this.totalItem = totalItem;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
