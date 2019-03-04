package com.example.drhal.honsproject;

public class Upload {
    private String myName;
    private String myImageUrl;

    public Upload(){
        //empty constructor needed
    }

    public Upload(String name, String imageUrl){

        if(name.trim().equals("")){
            name = "No Name";
        }

        myName = name;
        myImageUrl = imageUrl;

    }

    public  String getName(){ return myName; }
    public void setName(String name){
        myName = name;
    }

    public  String getImageUrl(){
        return myImageUrl;
    }

    public void setImageUrl(String imageUrl){
        myImageUrl = imageUrl;
    }



}
