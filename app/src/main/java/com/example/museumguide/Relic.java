package com.example.museumguide;

/**
 * Created by LQ on 2018/4/15.
 */

public class Relic {
    private String name;
    private int imageId;
    public Relic(String name,int imageId){
        this.name=name;
        this.imageId=imageId;
    }
    public String getName(){
        return name;
    }
    public int getImageId(){
        return imageId;
    }
}
