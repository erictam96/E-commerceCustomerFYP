package com.ecommerce.customer.fypproject.adapter;

public class Refund {
    private String shortSellDate;
    private String refundPrice;
    private String refundItemName;
    private String imgURL;

    public String getShortSellDate() {
        return shortSellDate;
    }

    public void setShortSellDate(String shortSellDate) {
        this.shortSellDate = shortSellDate;
    }

    public String getRefundPrice() {
        return refundPrice;
    }

    public void setRefundPrice(String refundPrice) {
        this.refundPrice = refundPrice;
    }

    public String getRefundItemName() {
        return refundItemName;
    }

    public void setRefundItemName(String refundItemName) {
        this.refundItemName = refundItemName;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
