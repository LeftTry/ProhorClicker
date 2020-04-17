package xd.company.prohorclicker

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    private val tag = "MainActivityInfo"
    private var money = 0
    private var profit = 1
    private var time: Long = 0
    private var offlineProfit: Long = 0
    private var offlineMultiple:Long = 0
    private val player:MediaPlayer = MediaPlayer()
    private var clickPlayer = MediaPlayer()
    private var isMuted = false
    private var isSFXMuted = false
    private val file = ReadWriteSD(tag, this)
    private val currentTime = System.currentTimeMillis()
    private var clicks = 0
    private var level = 1

    @SuppressLint("SdCardPath", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        money = file.getValues("Money", "0").toInt()
        profit = file.getValues("Profit", "1").toInt()
        isMuted = file.getValues("isMuted", false.toString()).toBoolean()
        isSFXMuted = file.getValues("isSFXMuted", false.toString()).toBoolean()
        offlineMultiple = file.getValues("OfflineMultiple", "0").toLong()
        time = file.getValues("Time", currentTime.toString()).toLong()
        level = file.getValues("Level", "1").toInt()
        money = intent.getIntExtra("MONEY", money)
        profit = intent.getIntExtra("PROFIT", profit)
        isMuted = intent.getBooleanExtra("isMuted", isMuted)
        isSFXMuted = intent.getBooleanExtra("isSFXMuted", isSFXMuted)
        offlineMultiple = intent.getLongExtra("offlineMultiple", offlineMultiple)
        d(tag, "Money : $money")
        d(tag, "Profit : $profit")
        d(tag, "CurrentTime : $currentTime")
        d(tag, "OfflineMultiple : $offlineMultiple")
        d(tag, "IsMuted : $isMuted")
        d(tag, "IsSFXMuted : $isSFXMuted")
        d(tag, "Experience : ${exp.progress}")
        d(tag, "Level : $level")
        offlineProfit = (currentTime - time) * offlineMultiple
        d(tag, "OfflineProfit : $offlineProfit")
        money += offlineProfit.toInt()
        exp.max = level * 1000
        exp.progress = money
        when {
            money > 1000 -> {money /= 1000
                moneyView.text = "$money K"}
            money > 1000000 -> {money /= 1000
                moneyView.text = "$money M"}
            money > 1000000000 -> {money /= 1000
                moneyView.text = "$money B"}
            money >= 1000000000000 -> {money /= 1000
                moneyView.text = "$money T"}
            else -> moneyView.text = "$money $"
        }
        if (!isMuted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                player.setDataSource(assets.openFd("files/backgroundMusic.mp3"))
            }
            player.setVolume(20F, 20F)
            player.isLooping = true
            player.prepare()
            player.start()
        }
        if (!isSFXMuted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                clickPlayer.setDataSource(assets.openFd("files/click_sound.mp3"))
            }
            clickPlayer.setVolume(1000F, 1000F)
            clickPlayer.prepare()
        }
    }

    override fun onPause() {
        super.onPause()
        file.checkExternalMedia()
        file.writeToSDFile("Money", money.toString())
        file.writeToSDFile("Level", level.toString())
        file.writeToSDFile("Time", Calendar.getInstance().timeInMillis.toString())
    }

    override fun onStop() {
        super.onStop()
        player.pause()
    }

    override fun onResume() {
        super.onResume()
        player.seekTo(intent.getIntExtra("currentPosition", player.currentPosition))
        if (!isMuted)
            player.start()
        else
            player.stop()
    }

    @SuppressLint("SetTextI18n")
    fun click(view: View) {
        if(!clickPlayer.isPlaying)
            if (!isSFXMuted)
                clickPlayer.start()
        money += profit
        when {
            money > 1000 -> {money /= 1000
                moneyView.text = "$money K"}
            money > 1000000 -> {money /= 1000
                moneyView.text = "$money M"}
            money > 1000000000 -> {money /= 1000
                moneyView.text = "$money B"}
            money >= 1000000000000 -> {money /= 1000
                moneyView.text = "$money T"}
            else -> moneyView.text = "$money $"
        }
        if (exp.progress == exp.max) {
            level++
            levelView.text = "Level: $level"
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Your level is now $level")
                .setTitle("Level up!")
            builder.setPositiveButton("Ok") { dialog, id ->
                dialog.cancel()
            }
            val dialog = builder.create()
        }
        exp.progress += profit
    }

    @SuppressLint("ShowToast")
    override fun onBackPressed() {
        super.onBackPressed()
        if(clicks == 1){
            clicks++
            Toast.makeText(this, "Do you sure to leave app?", Toast.LENGTH_LONG)
            val time = System.currentTimeMillis()
            while(System.currentTimeMillis() - time < 3000 && clicks == 1)
                if(clicks == 2)
                    onPause()
            clicks = 0
        }
        if (clicks == 2){
            onPause()
        }
    }

    fun shopTransition(view: View) {
        val shopIntent = Intent(this, ShopActivity::class.java)
        shopIntent.putExtra("MONEY", money)
        shopIntent.putExtra("PROFIT", profit)
        shopIntent.putExtra("isMuted", isMuted)
        shopIntent.putExtra("isSFXMuted", isSFXMuted)
        shopIntent.putExtra("currentPosition", player.currentPosition)
        startActivity(shopIntent)
    }

    fun settingsTransition(view: View) {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        settingsIntent.putExtra("isMuted", isMuted)
        settingsIntent.putExtra("isSFXMuted", isSFXMuted)
        settingsIntent.putExtra("currentPosition", player.currentPosition)
        startActivity(settingsIntent)
    }

    @SuppressLint("SetTextI18n")
    fun mysteryClick(view: View) {
        if(!clickPlayer.isPlaying)
            if (!isSFXMuted)
                clickPlayer.start()
        money += profit + 1
        moneyView.text = "Money: $money"
    }
}