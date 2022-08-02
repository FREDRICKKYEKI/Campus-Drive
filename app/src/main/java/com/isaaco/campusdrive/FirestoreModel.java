package com.isaaco.campusdrive;

import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.HashMap;

public class FirestoreModel {
    private String name, user_id,username, url, school, timestamp, post_url, caption, item_id, FolderCode ;



    public FirestoreModel() {

    }
    public String getFolderCode() {
        return FolderCode;
    }
    public void setFolderCode(String folderCode) {
        this.FolderCode = FolderCode;
    }


    public String getItem_id(){return item_id;}
    public void setItem_id(String item_id){this.item_id=item_id;}

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getSchool () {
        return school;
    }
    public void setSchool (String school) { this.school  = school; }


    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getPost_url () {
        return post_url ;
    }
    public void setPost_url (String post_url) { this.post_url  = post_url; }

    public String getCaption () {
        return caption;
    }
    public void setCaption (String caption) { this.caption  = caption; }

    public String getTimestamp () {
        return timestamp;
    }
    public void setTimestamp (String timestamp) { this.timestamp  = timestamp; }

}