import android.app.Activity
import android.content.Intent
import com.baatechat.blackwhite.swip.call.BuildConfig

fun Activity.shareApp(){
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    var shareMessage = "\nHey Check out this Great app:\n\n"
    shareMessage =
        "${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
    intent.putExtra(Intent.EXTRA_TEXT, shareMessage)
    intent.type = "text/plain"
    startActivity(Intent.createChooser(intent, "Share To:"))
}