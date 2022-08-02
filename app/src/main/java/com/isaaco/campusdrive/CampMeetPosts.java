package com.isaaco.campusdrive;

public class CampMeetPosts {

    private String user_id, item_id, username, school, post_url, caption, timestamp;

    public CampMeetPosts(){

    }

    public CampMeetPosts (String user_id, String item_id,String username, String school, String post_url, String caption, String timestamp) {
        this.user_id = user_id;
        this.username=username;
        this.item_id=item_id;
        this.school = school;
        this.post_url = post_url;
        this.caption= caption;
        this.timestamp=timestamp;
    }

    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public  String getUsername(){return username;}
    public void setUsername(String username){this.username=username;}

    public String getSchool () {
        return school;
    }
    public void setSchool (String school) { this.school  = school; }

    public String getPost_url () {
        return post_url ;
    }
    public void setPost_url (String UserId) { this.post_url  = post_url; }

    public String getCaption () {
        return caption;
    }
    public void setCaption (String UserId) { this.caption  = caption; }

    public String getTimestamp () {
        return timestamp;
    }
    public void setTimestamp (String UserId) { this.timestamp  = timestamp; }


}
