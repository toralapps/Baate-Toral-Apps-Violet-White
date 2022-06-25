package com.photography.hindishayari.Interface;

import androidx.annotation.Keep;

@Keep

public class Ads {
    private int MonetizationId;

    private String MonetizerName;

    private String AdAppKey;

    private String BannerAdKey;

    private String InterstitialAdKey;

    private String RewardedAdKey;

    private String NativeAdKey;

    public void setMonetizationId(int MonetizationId){
        this.MonetizationId = MonetizationId;
    }
    public int getMonetizationId(){
        return this.MonetizationId;
    }
    public void setMonetizerName(String MonetizerName){
        this.MonetizerName = MonetizerName;
    }
    public String getMonetizerName(){
        return this.MonetizerName;
    }
    public void setAdAppKey(String AdAppKey){
        this.AdAppKey = AdAppKey;
    }
    public String getAdAppKey(){
        return this.AdAppKey;
    }
    public void setBannerAdKey(String BannerAdKey){
        this.BannerAdKey = BannerAdKey;
    }
    public String getBannerAdKey(){
        return this.BannerAdKey;
    }
    public void setInterstitialAdKey(String InterstitialAdKey){
        this.InterstitialAdKey = InterstitialAdKey;
    }
    public String getInterstitialAdKey(){
        return this.InterstitialAdKey;
    }
    public void setRewardedAdKey(String RewardedAdKey){
        this.RewardedAdKey = RewardedAdKey;
    }
    public String getRewardedAdKey(){
        return this.RewardedAdKey;
    }
    public void setNativeAdKey(String NativeAdKey){
        this.NativeAdKey = NativeAdKey;
    }
    public String getNativeAdKey(){
        return this.NativeAdKey;
    }
}
