package com.isaaco.campusdrive;

public class MarketItems {

    private String item_id,email, item_name, price, phonenumber, description, timestamp, url;

    public MarketItems(){

    }

    public MarketItems(String item_id,String email, String item_name, String price, String phonenumber, String description, String timestamp, String url ){
        this.item_id=item_id;
        this.email = email;
        this.item_name = item_name;
        this.price = price;
        this.phonenumber = phonenumber;
        this.description = description;
        this.timestamp = timestamp;
        this.url=url;
}
    public String getItem_id() {return item_id;}
    public void setItem_id() {this.item_id=item_id;}

    public String getEmail(){return email;}
    public void setEmail(String email) {this.email=email;}

    public String getItem_name(){return item_name;}
    public void setItem_name(String item_name){this.item_name=item_name;}

    public String getPrice(){return price;}
    public void setPrice(String price){this.price = price;}

    public String getPhonenumber(){return phonenumber;}
    public void setPhonenumber(String phonenumber){this.item_name=item_name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description=description;}

    public String getTimestamp(){return timestamp;}
    public void setTimestamp(String timestamp){this.timestamp=timestamp;}

    public String getUrl(){return url;}
    public void setUrl (String url) {this.url = url;}
}
