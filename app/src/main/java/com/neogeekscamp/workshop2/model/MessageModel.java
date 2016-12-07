package com.neogeekscamp.workshop2.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by M. Asrof Bayhaqqi on 11/26/2016.
 */

public class MessageModel extends RealmObject {

    @PrimaryKey
    private String id;
    private String username;
    private String message;
    private String image;
    private String time;

    private int type;

    public static final int active = 0;
    public static final int inactive = 1;

    public MessageModel() {
    }

    public MessageModel(String id, String username, String message, String image, String time, int type) {
        this.id = id;
        this.username = username;
        this.message = message;
        this.image = image;
        this.time = time;
        this.type = type;
    }

    public MessageModel(MessageModel message) {
        this.id = message.getId();
        this.username = message.getUsername();
        this.message = message.getMessage();
        this.image = message.getImage();
        this.time = message.getTime();
        this.type = message.getType();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.equals("null") ? "" : id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.equals("null") ? "" : username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message.equals("null") ? "" : message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time.equals("null") ? "" : time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image.equals("null") ? "" : image;
    }

}
