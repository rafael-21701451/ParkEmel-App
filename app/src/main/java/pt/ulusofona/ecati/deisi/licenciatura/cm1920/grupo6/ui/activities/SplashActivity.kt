package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.GetDataListener
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.SplashViewModel

class SplashActivity : AppCompatActivity(), GetDataListener {

    private lateinit var viewModel: SplashViewModel
    val CONNECTED = "connected"
    val DISCONNECTED = "disconnected"
    val DIALOG = "dialog"
    val SETTING_2 = "setting_2"
    val DARK = "dark"
    val LOW_BATTERY = "low_battery"
    val IGNOREBATTERY = "ignorebattery"
    var preferences: SharedPreferences? = null
    val preferencesFileName = "ParkEmel_Preferences"
    val TIMETHREAD = "timethread"
    val BATTERYTHREAD = "batterythread"
    val NODISTANCE = "nodistance"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        val date = viewModel.getDate()
        preferences = (this).getSharedPreferences(preferencesFileName, 0)
        val editor = preferences!!.edit()
        editor.putBoolean(LOW_BATTERY, false)
        editor.putBoolean(DARK, false)
        if((date.toInt() >= 20 || date.toInt() < 8) && preferences!!.getBoolean(SETTING_2,true)){
            this.setTheme(R.style.AppThemeDark)
            editor.putBoolean(DARK, true)
        }else{
            this.setTheme(R.style.AppThemeLight)
            editor.putBoolean(DARK, false)
        }
        setContentView(R.layout.activity_splash)
        editor.putBoolean(TIMETHREAD,false)
        editor.putBoolean(BATTERYTHREAD,false)
        editor.putBoolean(CONNECTED,true)
        editor.putBoolean(DISCONNECTED,false)
        editor.putBoolean(DIALOG, false)
        editor.putBoolean(IGNOREBATTERY, false)
        editor.putBoolean(NODISTANCE, false)
        editor.apply()
        showParkEmel()
    }

    fun showParkEmel(){
        val duration = 3000L
        val handle = Handler()
        handle.postDelayed({
            viewModel.registerListener(this)
        }, duration)
    }

    override fun connected() {
        if(preferences!!.getBoolean(CONNECTED,true)){
            val editor = preferences!!.edit()
            editor.putBoolean(CONNECTED,true)
            editor.putBoolean(DISCONNECTED,false)
            editor.apply()
            Handler(Looper.getMainLooper()).post {
                viewModel.unregister()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun disconnected() {
        if(!(preferences!!.getBoolean(DISCONNECTED,false))){
            val editor = preferences!!.edit()
            editor.putBoolean(DISCONNECTED,true)
            editor.putBoolean(CONNECTED,false)
            editor.putBoolean(DIALOG, true)
            editor.apply()
            Handler(Looper.getMainLooper()).post {
                viewModel.unregister()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}