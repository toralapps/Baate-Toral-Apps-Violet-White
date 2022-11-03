package com.rugbychat.redpink.video.call.extenstionfunctions

import android.app.Activity
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun Activity.snackBar(view: View){
    Snackbar.make(view,"No Internate Connection",Snackbar.LENGTH_LONG).show()
}

fun Activity.snackBar(view: View , msg:String){
    Snackbar.make(view,msg,Snackbar.LENGTH_LONG).show()
}