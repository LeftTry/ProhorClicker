package xd.company.prohorclicker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.util.Log.e
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
    private var costsList:Array<String> = arrayOf()
    private val percent1 = 0.4
    private val percent2 = 0.5
    private val file = ReadWriteSD(tag, this)
    private var offlineProfit = 0

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
    }

    fun back(view: View) {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.putExtra("MONEY", money)
        mainIntent.putExtra("PROFIT", profit)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
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
        MoneyView.text = "Money: $money"
        ProfitView.text = "Money for click: $profit"
        cost1 += (cost * percent).toInt()
        CostView1.text = "Cost: $cost1"
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
        MoneyView.text = "Money: $money"
        ProfitView.text = "Money for click: $profit"
        cost1 += (cost * 10 * percent).toInt()
        CostView1.text = "Cost: $cost1"
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
        buy(view, cost2, percent2, offlineProfit)
    }
    fun buy2(view: View) {
        buyX10(view, cost2, percent2, offlineProfit)
    }
}
