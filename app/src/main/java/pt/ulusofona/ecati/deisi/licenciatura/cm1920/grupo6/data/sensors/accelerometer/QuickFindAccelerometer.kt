package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class QuickFindAccelerometer private constructor(context: Context): SensorEventListener {
    private val TAG = QuickFindAccelerometer::class.java.simpleName

    private var sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    companion object{

         private var instance: QuickFindAccelerometer? = null
        private var shakeListener: OnPhoneShake? = null

        fun start(context: Context){
            instance = if(instance == null) QuickFindAccelerometer(context) else instance
            instance?.start()
        }

        fun registerListener(shake: OnPhoneShake){
            this.shakeListener = shake
        }

        fun notifyListener(current: SensorEvent?){
            shakeListener?.onPhoneShake(current)
        }

        fun unregisterListener(){
            this.shakeListener = null
        }
    }

    private fun start(){
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.i(TAG, "onAccuracyChanged")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        notifyListener(event)
    }

}