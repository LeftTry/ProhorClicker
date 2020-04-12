package xd.company.prohorclicker


import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_shop.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ShopActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {
    private var money = 0
    private var profit = 1
    private val tag = "ShopActivityInfo"
    private var cost1 = 1000
    private var cost2 = 4000
    private val percent1 = 0.4
    private val percent2 = 0.5
    private val file = ReadWriteSD(tag, this)
    private var offlineProfit = 0
    private var isMuted = false
    private var isSFXMuted = false
    private var player = MediaPlayer()

    @SuppressLint("SdCardPath", "SetTextI18n")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        file.deleteFile("Cost")
        money = intent.getIntExtra("MONEY", money)
        d(tag, money.toString())
        profit = intent.getIntExtra("PROFIT", profit)
        d(tag, profit.toString())
        offlineProfit = intent.getIntExtra("offlineProfit", offlineProfit)
        MoneyView.text = "Money: $money"
        ProfitView.text = "Money for click: $profit"
        cost1 = file.getValues("Cost1", "1000").toInt()
        cost2 = file.getValues("Cost2", "4000").toInt()
        CostView1.text = "Cost: $cost1"
        CostView2.text = "Cost: $cost2"
        isMuted = intent.getBooleanExtra("isMuted", isMuted)
        isSFXMuted = intent.getBooleanExtra("isSFXMuted", isSFXMuted)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            player.setDataSource(assets.openFd("files/backgroundMusic.mp3"))
        }
        player.setVolume(20F, 20F)
        player.isLooping = true
        player.prepare()
        player.seekTo(intent.getIntExtra("currentPosition", player.currentPosition))
        if(!isMuted)
            player.start()
        when {
            money > 1000 -> {money /= 1000
                MoneyView.text = "$money K"}
            money > 1000000 -> {money /= 1000
                MoneyView.text = "$money M"}
            money > 1000000000 -> {money /= 1000
                MoneyView.text = "$money B"}
            money >= 1000000000000 -> {money /= 1000
                MoneyView.text = "$money T"}
            else -> MoneyView.text = "$money $"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        back()
    }

    private fun back(){
        player.stop()
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.putExtra("MONEY", money)
        mainIntent.putExtra("PROFIT", profit)
        mainIntent.putExtra("CurrentPosition", player.currentPosition)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
    }

    fun backButton(view: View) {
        back()
    }

    @SuppressLint("SetTextI18n")
    fun buy(view: View, cost: Int, percent: Double, Value: Int): Int {
        view.playSoundEffect(0)
        var cost1 = cost
        var value = Value
        if(money >= cost)
            money -= cost
        else
            return 0
        value++
        ProfitView.text = "Money for click: $profit"
        cost1 += (cost * percent).toInt()
        CostView1.text = "Cost: $cost1"
        when {
            money > 1000 -> {money /= 1000
                MoneyView.text = "$money K"}
            money > 1000000 -> {money /= 1000
                MoneyView.text = "$money M"}
            money > 1000000000 -> {money /= 1000
                MoneyView.text = "$money B"}
            money >= 1000000000000 -> {money /= 1000
                MoneyView.text = "$money T"}
            else -> MoneyView.text = "$money $"
        }
        return value
    }

    @SuppressLint("SetTextI18n")
    fun buyX10(view: View, cost: Int, percent:Double, Value: Int): Int {
        view.playSoundEffect(0)
        var cost1 = cost
        var value = Value
        if(money >= cost * 10)
            money -= cost * 10
        else
            return 0
        value++
        ProfitView.text = "Money for click: $profit"
        cost1 += (cost * 10 * percent).toInt()
        CostView1.text = "Cost: $cost1"
        when {
            money > 1000 -> {money /= 1000
                MoneyView.text = "$money K"}
            money > 1000000 -> {money /= 1000
                MoneyView.text = "$money M"}
            money > 1000000000 -> {money /= 1000
                MoneyView.text = "$money B"}
            money >= 1000000000000 -> {money /= 1000
                MoneyView.text = "$money T"}
            else -> MoneyView.text = "$money $"
        }
        return value
    }

    override fun onPause() {
        super.onPause()
        file.writeToSDFile("Cost1", cost1.toString())
        file.writeToSDFile("Cost2", cost2.toString())
        file.writeToSDFile("Profit", profit.toString())
        file.writeToSDFile("OfflineProfit", offlineProfit.toString())
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

    }

    fun buy1x10(view: View) {
        buyX10(view, cost1, percent1, profit)
    }
    fun buy1(view: View) {
        buy(view, cost1, percent1, profit)
    }
    fun buy2x10(view: View) {
        buyX10(view, cost2, percent2, offlineProfit)
    }
    fun buy2(view: View) {
        buy(view, cost2, percent2, offlineProfit)
    }
}
