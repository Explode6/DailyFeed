package com.example.rssreader;

public class RssSource {
    private String title;
    private String intro;
    private byte[] image;
    private boolean selected;

    public RssSource(String title, String intro, byte[] image, boolean selected) {
        this.title = title;
        this.intro = intro;
        this.image = image;
        this.selected = selected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
