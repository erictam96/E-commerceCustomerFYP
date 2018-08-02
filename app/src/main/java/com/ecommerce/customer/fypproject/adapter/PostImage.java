package com.ecommerce.customer.fypproject.adapter;

class PostImage {
    private String mImageUrl;
    private String mpostID;

    public PostImage(){

    }

    public PostImage(String ImageUrl, String postID) {
        mImageUrl = ImageUrl;
        mpostID = postID;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        mImageUrl = ImageUrl;
    }

    public String getpostID() {
        return mpostID;
    }

    public void setpostID(String postID) {
        mpostID = postID;
    }
}