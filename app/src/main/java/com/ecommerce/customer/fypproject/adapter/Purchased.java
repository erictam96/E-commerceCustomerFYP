package com.ecommerce.customer.fypproject.adapter;

public class Purchased {
    private String purchasedDate;
    private String purchasedItemName;
    private String imgURL;

    public String getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(String purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public String getPurchasedItemName() {
        return purchasedItemName;
    }

    public void setPurchasedItemName(String purchasedItemName) {
        this.purchasedItemName = purchasedItemName;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
