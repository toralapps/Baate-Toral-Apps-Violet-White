package com.app.ads.domain.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
@kotlinx.serialization.Serializable
data class AdsRoot(

	@field:SerializedName("Status")
	val status: Boolean? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("HttpStatus")
	val httpStatus: Int? = null,

	@field:SerializedName("Data")
	val data: Data? = null
)
@Keep
@kotlinx.serialization.Serializable
data class Data(

	@field:SerializedName("Ads")
	val ads: List<AdsItem?>? = null,

	@field:SerializedName("ShowAds")
	val showAds: Boolean? = null
)

@Keep
@kotlinx.serialization.Serializable
data class AdsItem(

	@field:SerializedName("RewardedAdKey")
	val rewardedAdKey: String? = null,

	@field:SerializedName("MonetizationId")
	val monetizationId: Int? = null,

	@field:SerializedName("BannerAdKey")
	val bannerAdKey: String? = null,

	@field:SerializedName("AdAppKey")
	val adAppKey: String? = null,

	@field:SerializedName("InterstitialAdKey")
	val interstitialAdKey: String? = null,

	@field:SerializedName("MonetizerName")
	val monetizerName: String? = null,

	@field:SerializedName("NativeAdKey")
	val nativeAdKey: String? = null
)
