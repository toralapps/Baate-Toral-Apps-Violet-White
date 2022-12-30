package com.wisechat.violetwhite.video.call.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisechat.violetwhite.video.call.repository.RepositoryImp
import com.wisechat.violetwhite.video.call.repository.Response
import com.wisechat.violetwhite.video.call.singletons.ListOfVideos
import com.wisechat.violetwhite.video.call.videolistmodel.ReportModel
import com.wisechat.violetwhite.video.call.videolistmodel.VideoList
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