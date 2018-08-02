package com.ecommerce.customer.fypproject.adapter;

public class Comment {
    private String commentUserid,commentUsername,commentPostid,commentDesc,profilePicUrl,commentPicUrl;
    private String commentDate;

    public String getCommentUserid() {
        return commentUserid;
    }

    public void setCommentUserid(String commentUserid) {
        this.commentUserid = commentUserid;
    }

    public String getCommentPostid() {
        return commentPostid;
    }

    public void setCommentPostid(String commentPostid) {
        this.commentPostid = commentPostid;
    }

    public String getCommentDesc() {
        return commentDesc;
    }

    public void setCommentDesc(String commentDesc) {
        this.commentDesc = commentDesc;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentUsername() {
        return commentUsername;
    }

    public void setCommentUsername(String commentUsername) {
        this.commentUsername = commentUsername;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getCommentPicUrl() {
        return commentPicUrl;
    }

    public void setCommentPicUrl(String commentPicUrl) {
        this.commentPicUrl = commentPicUrl;
    }
}
