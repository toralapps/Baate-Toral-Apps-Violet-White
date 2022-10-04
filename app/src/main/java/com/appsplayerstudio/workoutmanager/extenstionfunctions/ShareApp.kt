import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.appsplayerstudio.workoutmanager.BuildConfig


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


fun Activity.rateUs(){
    try {
        this.startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$packageName")))
    } catch (e: ActivityNotFoundException) {
        this.startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
    }


}

fun Activity.openOtherApp(url:String){
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    this.startActivity(intent)

}