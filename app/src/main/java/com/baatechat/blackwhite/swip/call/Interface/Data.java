package com.baatechat.blackwhite.swip.call.Interface;

import androidx.annotation.Keep;

import java.util.List;

@Keep
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