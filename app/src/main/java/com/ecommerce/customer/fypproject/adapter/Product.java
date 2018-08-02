package com.ecommerce.customer.fypproject.adapter;

/**
 * Created by leeyipfung on 3/5/2018.
 */

public class Product {

    private String prodID;
    private String prodCode;
    private String prodName;
    private String prodDate;
    private String prodCategory;
    private String prodDesc;
    private String prodSize;
    private String prodStatus;
    private String RID;
    private String productURL;
    private String shopName;
    private String productVariant;
    private String discountedPrice;
    private double prodPrice;
    private double prodVariantPrice;
    private int prodDiscount;
    private int prodVariantQty;


    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public double getProdVariantPrice() {
        return prodVariantPrice;
    }

    public void setProdVariantPrice(double prodVariantPrice) {
        this.prodVariantPrice = prodVariantPrice;
    }

    public int getProdVariantQty() {
        return prodVariantQty;
    }

    public void setProdVariantQty(int prodVariantQty) {
        this.prodVariantQty = prodVariantQty;
    }

    public String getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(String productVariant) {
        this.productVariant = productVariant;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getProductURL() {
        return productURL;
    }

    public void setProductURL(String productURL) {
        this.productURL = productURL;
    }

    public String getProdID() {
        return prodID;
    }

    public void setProdID(String prodID) {
        this.prodID = prodID;
    }

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdDate() {
        return prodDate;
    }

    public void setProdDate(String prodDate) {
        this.prodDate = prodDate;
    }

    public String getProdCategory() {
        return prodCategory;
    }

    public void setProdCategory(String prodCategory) {
        this.prodCategory = prodCategory;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public String getProdSize() {
        return prodSize;
    }

    public void setProdSize(String prodSize) {
        this.prodSize = prodSize;
    }

    public String getProdStatus() {
        return prodStatus;
    }

    public void setProdStatus(String prodStatus) {
        this.prodStatus = prodStatus;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public double getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(double prodPrice) {
        this.prodPrice = prodPrice;
    }

    public int getProdDiscount() {
        return prodDiscount;
    }

    public void setProdDiscount(int prodDiscount) {
        this.prodDiscount = prodDiscount;
    }
}
