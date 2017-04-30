package com.example.ece420final.businesscard;

/**
 * Created by hanfei on 4/27/17.
 */

class ContactInfo {
    private String myTitle;
    private String myContent;

    protected ContactInfo(String title,String content){
        myContent = content;
        myTitle = title;
    }

    protected String getMyTitle(){
        return myTitle;
    }

    protected String getMyContent(){
        return myContent;
    }

    public String toString(){
        return myTitle + " " + myContent;
    }

}
