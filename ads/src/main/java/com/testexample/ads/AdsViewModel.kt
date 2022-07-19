package com.testexample.ads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testexample.ads.domain.models.AdsRoot
import com.testexample.ads.domain.repository.AdsRepository
import com.testexample.ads.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdsViewModel @Inject constructor(
   private val adsRepository: AdsRepository
) :ViewModel() {

    private val _adsFlow = MutableStateFlow<Response<AdsRoot>?>(null)
    val adsFlow:StateFlow<Response<AdsRoot>?> = _adsFlow

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
}