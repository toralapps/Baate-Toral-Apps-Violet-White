package com.wisechat.violetwhthise.video.call.reposthisory


import com.app.ads.CachData
import com.app.ads.CachVideos
import com.wisechat.violetwhite.video.call.videolistmodel.Data
import com.wisechat.violetwhite.video.call.videolistmodel.VideoCallType
import com.wisechat.violetwhite.video.call.videolistmodel.VideoList

fun CachVideos.toVideo(): VideoList {
   return VideoList(
        this.dataList.map { it.toData()},
        Settings = VideoCallType(this.settings.videoCall),
        HttpStatus = this.httpStatus,
        Message = this.message,
        Status = this.status
    )
}

fun CachData.toData() : Data =
    Data(
        Comments = this.comments,
        FirstName = this.firstName,
        LastName = this.lastName,
        Likes = this.likes,
        Location = this.location,
        Loves = this.loves,
        Smiles = this.smiles,
        SrNo = this.srNo,
        ThumbnailUrl = this.thumbnailUrl,
        VideoName = this.videoName,
        VideoUrl = this.videoUrl,
        Views = this.views,
        VideoId = this.videoId
    )


fun Data.toCacheData():CachData{
    return CachData.newBuilder().apply {
        comments = this.comments
        firstName = this.firstName
        lastName = this.lastName
        likes = this.likes
        location = this.location
        loves = this.loves
        smiles = this.smiles
        srNo = this.srNo
        thumbnailUrl = this.thumbnailUrl
        videoName = this.videoName
        videoUrl = this.videoUrl
        views = this.views
        videoId = this.videoId
    }.build()
}