package xd.company.prohorclicker

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    private var isMuted = false
    private var player = MediaPlayer()
    private var isSFXMuted = false
    private var tag = "SettingsInfo"
    private val file = ReadWriteSD(tag, this)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        isMuted = intent.getBooleanExtra("isMuted", isMuted)
        isSFXMuted = intent.getBooleanExtra("isSFXMuted", isSFXMuted)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                player.setDataSource(assets.openFd("files/backgroundMusic.mp3"))
            }
            player.setVolume(20F, 20F)
            player.isLooping = true
            player.prepare()
            player.seekTo(intent.getIntExtra("currentPosition", player.currentPosition))
        if(!isMuted) {
            player.start()
            volume.setImageResource(R.drawable.audio_dark)
        }
        else
            volume.setImageResource(R.drawable.mute_dark)
        if (!isSFXMuted)
            sfx.text = "SFX: On"
        else
            sfx.text = "SFX: Off"
        
    }

    fun volumeOnOff(view: View){
        if(!isMuted) {
            volume.setImageResource(R.drawable.mute_dark)
            player.stop()
        }
        else {
            volume.setImageResource(R.drawable.audio_dark)
            player.start()
        }
            isMuted = !isMuted
    }

    @SuppressLint("SetTextI18n")
    fun sfxOnOff(view: View) {
        if (!isSFXMuted)
            sfx.text = "SFX: Off"
        else
            sfx.text = "SFX: On"
        isSFXMuted = !isSFXMuted
    }
    fun back(view: View){
        player.stop()
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.putExtra("isMuted", isMuted)
        mainIntent.putExtra("isSFXMuted",isSFXMuted)
        mainIntent.putExtra("currentPosition", player.currentPosition)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
    }

    override fun onStop() {
        super.onStop()
        file.writeToSDFile("isMuted", isMuted.toString())
        file.writeToSDFile("isSFXMuted", isSFXMuted.toString())
    }
}
