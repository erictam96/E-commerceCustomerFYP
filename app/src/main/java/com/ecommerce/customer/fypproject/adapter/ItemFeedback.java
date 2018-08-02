package com.ecommerce.customer.fypproject.adapter;

public class ItemFeedback {
    private String retailerProfilePicURL;
    private String itemURL;
    private String shopname;
    private String itemName;
    private String itemVariant;
    private String quantity;
    private String deliveredDate;

    public String getRetailerProfilePicURL() {
        return retailerProfilePicURL;
    }

    public void setRetailerProfilePicURL(String retailerProfilePicURL) {
        this.retailerProfilePicURL = retailerProfilePicURL;
    }

    public String getItemURL() {
        return itemURL;
    }

    public void setItemURL(String itemURL) {
        this.itemURL = itemURL;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemVariant() {
        return itemVariant;
    }

    public void setItemVariant(String itemVariant) {
        this.itemVariant = itemVariant;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(String deliveredDate) {
        this.deliveredDate = deliveredDate;
    }
}
