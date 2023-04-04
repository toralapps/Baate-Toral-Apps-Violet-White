package com.toralapps.viowh.livecall.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toralapps.viowh.livecall.repository.RepositoryImp
import com.toralapps.viowh.livecall.repository.Response
import com.toralapps.viowh.livecall.videolistmodel.ReportModel
import com.toralapps.viowh.livecall.videolistmodel.VideoList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val respository: RepositoryImp):ViewModel()  {

    val VideoListLiveData: LiveData<Response<VideoList>> = respository.liveDataVideo


    init {
        viewModelScope.launch {
            respository.getVideos()
        }
    }

    fun reCallingApi(){
        viewModelScope.launch {
            respository.getVideos()
        }
    }

    suspend fun report(): Response<ReportModel> {
        return respository.getReport()
    }


}