package com.app.ads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.ads.domain.models.AdsRoot
import com.app.ads.domain.repository.AdsRepository
import com.app.ads.utils.AdsEvent
import com.app.ads.utils.AdsState
import com.app.ads.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdsJavaViewModel @Inject constructor(
   private val adsRepository: AdsRepository
) :ViewModel() {

    private val _adsFlow = MutableStateFlow<Response<AdsRoot>?>(null)
    val adsFlow:StateFlow<Response<AdsRoot>?> = _adsFlow


    private val _AdsChannel = Channel<AdsState>()
    val adsState = _AdsChannel.receiveAsFlow().asLiveData(Dispatchers.Main)

    init {
        getAdsFromApi()
    }

    private fun getAdsFromApi(){
        viewModelScope.launch {
            adsRepository.getAddInfo().collect{
                when(it){
                    is Response.Success ->{
                        _adsFlow.value = it
                    }

                    is Response.error ->{
                        _adsFlow.value = it
                    }

                    is Response.Loading ->{
                        _adsFlow.value = it
                    }
                }
            }
        }
    }

    fun onAdEvent(event: AdsEvent){
        when(event){

            is AdsEvent.onAdClose ->{
                viewModelScope.launch {
                    _AdsChannel.send(AdsState.AdClosed)
                }
            }

            is AdsEvent.onAdOpened ->{
                viewModelScope.launch {
                    _AdsChannel.send(AdsState.AdOpened)
                }
            }

            is AdsEvent.onAdReady ->{
                viewModelScope.launch {
                    _AdsChannel.send(AdsState.AdReady)
                }
            }
        }
    }
}