package com.testexample.ads.utils

sealed class AdsEvent{

    object onAdClose : AdsEvent()
    object onAdReady : AdsEvent()
    object onAdOpened : AdsEvent()

}
