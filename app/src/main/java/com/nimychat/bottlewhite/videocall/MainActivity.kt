package com.nimychat.bottlewhite.videocall

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.ads.AdsViewModel
import com.app.ads.NewAddsActivty
import com.app.ads.utils.AdsState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.myech.video.bluepink.chat.blockuser.BlockList
import com.nimychat.bottlewhite.videocall.extenstionfunctions.snackBar
import com.nimychat.bottlewhite.videocall.repository.Response
import com.nimychat.bottlewhite.videocall.singletons.ListOfVideos
import com.nimychat.bottlewhite.videocall.videolistmodel.Data
import com.nimychat.bottlewhite.videocall.viewmodels.MainViewModel
import com.nimychat.bottlewhite.videocall.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : NewAddsActivty(){
    val adsViewModel : AdsViewModel by viewModels()
    var audioflag = true
    var videoflag = true
    var cameraflag = true
    lateinit var bottomNavigation:BottomNavigationView
    lateinit var callend:FloatingActionButton
    lateinit var textview: TextView
    private val viewmodel: MainViewModel by viewModels()
    lateinit var reportBtn:ImageView
//    var adDataObject: VideoList_Model? = null
//    var reportObject: Report_Model? = null
//    private lateinit var mainViewModel: MainViewModel
//    private var connectingDialog: ConnectingDialog? = null

    lateinit var playerView: PlayerView
    lateinit var textureView: TextureView
    lateinit var blockBtn:ImageView


    val CAMERA_FRONT = "1"
    val CAMERA_BACK = "0"
    private var cameraId = CAMERA_FRONT

    var simpleExoPlayer: SimpleExoPlayer? = null

    protected var cameraDevice: CameraDevice? = null
    private var imageDimension: Size? = null
    private val REQUEST_CAMERA_PERMISSION = 200
    protected var captureRequestBuilder: CaptureRequest.Builder? = null
    protected var cameraCaptureSessions: CameraCaptureSession? = null
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    private var isPreviewCamera = true
    private var isSpeakerCamera = true
    private var isAudioMute = false
    private var permisttion = false
    override val adContainer: LinearLayout?
        get() = null



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                adsViewModel.adsState.collect{adsState ->
                    when(adsState){
                        is AdsState.AdOpened ->{

                        }

                        is AdsState.AdClosed ->{
                            finish()
                        }

                        is AdsState.AdReady ->{

                        }
                    }
                }
            }
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        playerView = findViewById(R.id.playerView)
        textureView = findViewById(R.id.textureView)
        bottomNavigation = findViewById(R.id.bottomnavigationview)
        bottomNavigation.background = null
        callend = findViewById(R.id.callend)
        blockBtn = findViewById(R.id.blockBtn)
        reportBtn = findViewById(R.id.report)

        Permisstion()

        if(permisttion){
            val url = ListOfVideos.videos!!.VideoUrl
            setupPlayer(url)
        }

        reportBtn.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                when(viewmodel.report()){
                    is Response.Success ->{
                        snackBar(reportBtn,viewmodel.report().videoList!!.Message)
                    }
                    is Response.error ->{
                            snackBar(reportBtn,"Please try after sometimes")
                    }
                    else -> {}
                }
            }
        }

        blockBtn.setOnClickListener {
            blockuser(ListOfVideos.videos!!)
        }

        playerView!!.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView!!.requestFocus()

        textureView.surfaceTextureListener = textureListener

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.audio ->{
                   audioSet(it)
                    true
                }
                R.id.video ->{
                    videoSet(it)
                        true
                }
                R.id.camera ->{
                    cameraSet()
                    true
                }else->{
                    false
                }
            }
        }

        bottomNavigation.setOnNavigationItemReselectedListener {
            when(it.itemId){
                R.id.audio ->{
                    audioSet(it)
                    true
                }
                R.id.video ->{
                    videoSet(it)
                    true
                }
                R.id.camera ->{
                    cameraSet()
                    true
                }else->{
                false
            }
            }
        }

        callend.setOnClickListener {
            showIntertisialAdd()
        }

        var listener = View.OnTouchListener(function = { view, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_MOVE) {

                view.y = motionEvent.rawY - view.height / 2
                view.x = motionEvent.rawX - view.width / 2
            }

            true

        })

        textureView.setOnTouchListener(listener)


    }

    fun audioSet(it:MenuItem){
        isSpeakerCamera = !isSpeakerCamera

        //Change the Image icon
        if (isSpeakerCamera) {
            it.setIcon(R.drawable.ic_mic_black_24dp)
            simpleExoPlayer?.volume = 1f
        } else {
           it.setIcon(R.drawable.ic_mic_off_black_24dp)
            simpleExoPlayer?.volume = 0f
        }
    }

    fun videoSet(it:MenuItem){
        isPreviewCamera = !isPreviewCamera

        if (isPreviewCamera) {
            textureView.visibility = View.VISIBLE
            startCameraAndThread()
           it.setIcon(R.drawable.ic_videocam_black_24dp)
        } else {
            textureView.visibility = View.GONE
            stopCameraAndThread()
            it.setIcon(R.drawable.ic_videocam_off_black_24dp)
        }
    }

    fun cameraSet(){

        if (!isPreviewCamera)
            return

        if (cameraId == CAMERA_FRONT) {
            cameraId = CAMERA_BACK;
            closeCamera();
            openCamera();
        } else if (cameraId == CAMERA_BACK) {
            cameraId = CAMERA_FRONT;
            closeCamera();
            openCamera();
        }
    }


    var textureListener: TextureView.SurfaceTextureListener = object :
        TextureView.SurfaceTextureListener {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun openCamera() {
        if (permisttion == true) {
            val manager = getSystemService(CAMERA_SERVICE) as CameraManager
            Log.e("TAG", "is camera open")
            try {
                //cameraId = manager.cameraIdList[0]
                val characteristics = manager.getCameraCharacteristics(cameraId)
                val map =
                    characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]

                Log.e(
                    "Sanjay",
                    "width : " + imageDimension?.width + " height : " + imageDimension?.height
                )

                // Add permission for camera and let user grant the permission
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.CAMERA
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    REQUEST_CAMERA_PERMISSION
//                )
//                return
//            }

                manager.openCamera(cameraId, stateCallback, null)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
            Log.e("TAG", "openCamera X")
        }
    }


    private val stateCallback: CameraDevice.StateCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : CameraDevice.StateCallback() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onOpened(camera: CameraDevice) {
            //This is called when the camera is open
            Log.e("TAG", "onOpened")
            cameraDevice = camera
            createCameraPreview()
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
        }
    }

    private fun Permisstion(){
        var permisstions = mutableListOf<String>()

        if((ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED)){

            permisstions.add(Manifest.permission.CAMERA)
        }

        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED){
            permisstions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if(permisstions.isNotEmpty()){
            Log.d("Setp","Asking permisstion")
            ActivityCompat.requestPermissions(
                this,
                permisstions.toTypedArray(),
                REQUEST_CAMERA_PERMISSION)
        }

        else{
            permisttion = true
            Log.d("DEEP","Permisstion is gratnted" )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            200 ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("Setp","permisstion granted")
                    permisttion = true
                    setupPlayer(ListOfVideos.videos!!.VideoUrl)
                }
                else{
                    permisstionDailog()
                }
            }
        }


    }

    fun permisstionDailog(){
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Permission needed")
        alertDialogBuilder.setMessage("Camera and Storage permission needed")
        alertDialogBuilder.setPositiveButton("Open Setting",
            DialogInterface.OnClickListener { dialogInterface, i ->
                onBackPressed()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package", this@MainActivity.packageName,
                    null
                )
                intent.data = uri
                this@MainActivity.startActivity(intent)
            })
        alertDialogBuilder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialogInterface, i ->
                Log.d(
                    "NagavtiveButton",
                    "onClick: Cancelling"
                )
                snackBar(playerView,"Permission needed")
                onBackPressed()
            })

        val dialog: AlertDialog = alertDialogBuilder.create()
        dialog.show()
    }

    private fun setupPlayer(url: String) {

        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        val mediaDataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(this, Util.getUserAgent(this, "kids"))
        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
            MediaItem.fromUri(Uri.parse(url))
        )
        simpleExoPlayer?.prepare(mediaSource)
        playerView!!.player = simpleExoPlayer
        simpleExoPlayer?.playWhenReady = true
        simpleExoPlayer?.repeatMode = Player.REPEAT_MODE_OFF
        simpleExoPlayer?.addListener(object: Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                if (playbackState == ExoPlayer.STATE_BUFFERING) {

                }
                if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
                    //setProgress()
                }
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    //  openAdScreen("OnClose")
                   showIntertisialAdd()
                }
            }
        })
    }

    private fun createCameraPreview() {
        try {
            val texture = textureView.surfaceTexture!!
            //texture.setDefaultBufferSize(imageDimension!!.width, imageDimension!!.height)
            texture.setDefaultBufferSize(100, 125)
            val surface = Surface(texture)
            captureRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder?.addTarget(surface)
            cameraDevice!!.createCaptureSession(
                Arrays.asList(surface),
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        //The camera is already closed
                        if (null == cameraDevice) {
                            return
                        }
                        // When the session is ready, we start displaying the preview.
                        cameraCaptureSessions = cameraCaptureSession
                        updatePreview()
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        Toast.makeText(
                            this@MainActivity,
                            "Configuration change",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    protected fun updatePreview() {
        if (null == cameraDevice) {
            Log.e("TAG", "updatePreview error, return")
        }
        captureRequestBuilder!![CaptureRequest.CONTROL_MODE] = CameraMetadata.CONTROL_MODE_AUTO
        try {
            cameraCaptureSessions?.setRepeatingRequest(
                captureRequestBuilder!!.build(),
                null,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
    protected fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread?.looper!!)
    }

    private fun closeCamera() {
        try {
            cameraCaptureSessions?.close()
            cameraCaptureSessions = null
            cameraDevice?.close()
            cameraDevice = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {

        }
    }



    fun blockuser(video: Data){
        var oldlist = ArrayList<Data>()
        if(BlockList.getBlockVideos(this).isNotEmpty()){
            oldlist = BlockList.getBlockVideos(this).also {
                it.add(video)
            }
        }else{
            oldlist.add(video)
        }
        Log.d("DEEP list size", oldlist.size.toString())
        BlockList.saveBlockVideos(this, oldlist)
        Log.d("DEEP", BlockList.getBlockVideos(this).toString())
        Toast.makeText(this,"User is blocked", Toast.LENGTH_SHORT).show()
        showIntertisialAdd()
    }

    protected fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun releasePlayer() {
        if (simpleExoPlayer == null) {
            return
        }
        simpleExoPlayer?.playWhenReady = false
        simpleExoPlayer?.stop()
        simpleExoPlayer?.release()
        playerView!!.onPause()
        playerView?.player = null
    }

    private fun startCameraAndThread() {
        startBackgroundThread()
        if (textureView.isAvailable) {
            openCamera()
        } else {
            textureView.surfaceTextureListener = textureListener
        }
    }
    private fun stopCameraAndThread() {
        closeCamera()
        stopBackgroundThread()
    }

    override fun onPause() {
        if (isPreviewCamera)
            stopCameraAndThread()

        super.onPause()

        if (simpleExoPlayer != null)
            simpleExoPlayer?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onResume() {
        super.onResume()
        Log.d("DEEP","MAINRESUME")
        if (isPreviewCamera)
            startCameraAndThread()

        if (simpleExoPlayer != null)
            simpleExoPlayer?.playWhenReady = true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showIntertisialAdd()
    }


}