package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log

class Battery private constructor(private val context: Context): Runnable{
    private val TAG = Battery::class.java.simpleName
    private val TIME_BETWEEN_UPDATES = 5000L

    companion object{
        private var instance: Battery? = null
        private val handler = android.os.Handler()
        private var batteryListener: OnBatteryCurrentListener? = null

        fun start(context: Context){
            instance = if (instance ==null) Battery(context) else instance
            instance?.start()
        }

        fun registerListener(batteryCurrentListener: OnBatteryCurrentListener){
            this.batteryListener = batteryCurrentListener
        }

        fun notifyListener(current: Double){
            batteryListener?.onCurrentChanged(current)
        }

        fun unregisterListener(){
            this.batteryListener = null
        }
    }

    private fun start(){
        handler.postDelayed(this, TIME_BETWEEN_UPDATES)
    }

    private fun getBatteryCurrentNow(): Double{
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)}

        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
             level * 100 / scale.toFloat()}
        return batteryPct?.toDouble()!!
    }

    override fun run() {
        val current = getBatteryCurrentNow()
        Log.i(TAG, current.toString())
        notifyListener(current)
        handler.postDelayed(this, TIME_BETWEEN_UPDATES)
    }
}