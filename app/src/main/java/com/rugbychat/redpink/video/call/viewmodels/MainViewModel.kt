package com.rugbychat.redpink.video.call.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatguru.live.meet.video.chat.repository.Repository
import com.rugbychat.redpink.video.call.repository.Response
import com.rugbychat.redpink.video.call.videolistmodel.ReportModel
import com.rugbychat.redpink.video.call.videolistmodel.VideoList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val respository: Repository):ViewModel()  {

    val VideoListLiveData: LiveData<Response<VideoList>> = respository.liveDataVideo


    init {
        viewModelScope.launch {
            respository.getVideos()
        }
    }

    suspend fun report(): Response<ReportModel> {
        return respository.getReport()
    }


}