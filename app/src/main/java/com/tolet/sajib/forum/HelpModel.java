package com.tolet.sajib.forum;

public class HelpModel {
    String name;
    String address;
    String pro_pic;
    String post_image;
    String title;
    String userId;
    String details;
    String date;

    public HelpModel(String name, String address, String pro_pic, String post_image, String title, String userId, String details, String date) {
        this.name = name;
        this.address = address;
        this.pro_pic = pro_pic;
        this.post_image = post_image;
        this.title = title;
        this.userId = userId;
        this.details = details;
        this.date = date;

    }

    public HelpModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPro_pic() {
        return pro_pic;
    }

    public void setPro_pic(String pro_pic) {
        this.pro_pic = pro_pic;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
