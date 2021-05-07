package com.example.rssreader;

public class RssSource {
    private String intro;
    private int imgId;
    private boolean selected;

    public RssSource(String intro, int imgId){
        this.intro = intro;
        this.imgId = imgId;
        selected = false;
    }
    public String getIntro(){
        return intro;
    }
    public int getImgId(){
        return imgId;
    }
    public boolean getSelected() {return selected;}
    public void setSelected(boolean selected){this.selected = selected;}
}
