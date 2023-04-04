package com.example.hdwallpapersadmin;

public class Users {
    String wallpaper,caption;

    public Users() {
    }

    public Users(String wallpaper, String caption) {
        this.wallpaper = wallpaper;
        this.caption = caption;
    }

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
