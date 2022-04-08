package com.trendingchat.love.pink.video.chat.Interface;

import java.util.List;

public class Data {
    private boolean ShowAds;

    private List<Ads> Ads;

    public void setShowAds(boolean ShowAds){
        this.ShowAds = ShowAds;
    }
    public boolean getShowAds(){
        return this.ShowAds;
    }
    public void setAds(List<Ads> Ads){
        this.Ads = Ads;
    }
    public List<Ads> getAds(){
        return this.Ads;
    }
}
