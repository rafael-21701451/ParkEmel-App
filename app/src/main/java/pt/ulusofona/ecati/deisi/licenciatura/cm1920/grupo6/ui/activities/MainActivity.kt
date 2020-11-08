package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.accelerometer.Accelerometer
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.battery.Battery
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.battery.OnBatteryCurrentListener
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments.LowBatteryDialogFragment
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils.NavigationManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnParkChange, OnBatteryCurrentListener {

    private lateinit var viewModel: MainActivityViewModel

    val DARK = "dark"
    val TIMETHREAD = "timethread"
    val SETTING_2 = "setting_2"
    val LOW_BATTERY = "low_battery"
    val BATTERYTHREAD = "batterythread"
    val IGNOREBATTERY = "ignorebattery"
    var preferences: SharedPreferences? = null
    val preferencesFileName = "ParkEmel_Preferences"
    var dialog = false
    var quickFind = false

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_parks -> NavigationManager.goToParksFragment(
                supportFragmentManager
            )
            R.id.nav_map -> NavigationManager.goToMapFragment(
                supportFragmentManager
            )
            R.id.nav_quick_find -> NavigationManager.goToQuickFindFragment(
                supportFragmentManager
            )
            R.id.nav_trip_planner -> NavigationManager.goToTripPlannerFragment(
                supportFragmentManager
            )
            R.id.nav_vehicles -> NavigationManager.goToVehiclesFragment(
                supportFragmentManager
            )
            R.id.nav_contacts -> NavigationManager.goToContactsFragment(
                supportFragmentManager
            )
            R.id.nav_settings -> NavigationManager.goToSettingsFragment(
                supportFragmentManager
            )
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        preferences = (this).getSharedPreferences(preferencesFileName, 0)
        Battery.registerListener(this)
        if(!(preferences!!.getBoolean(LOW_BATTERY, false))){
                Battery.registerListener(this)
            }
        val editor = preferences!!.edit()
        if (preferences!!.getBoolean(DARK, true) && preferences!!.getBoolean(SETTING_2,true)) {
            this.setTheme(R.style.AppThemeDark)
            editor.putBoolean(DARK, true)
        } else {
            this.setTheme(R.style.AppThemeLight)
            editor.putBoolean(DARK, false)
        }
        if (!(preferences!!.getBoolean(TIMETHREAD, false))) {
            CoroutineScope(Dispatchers.IO).launch {
                checkTime()
            }
            editor.putBoolean(TIMETHREAD, true)
        }
        if (!(preferences!!.getBoolean(BATTERYTHREAD, false))) {
            CoroutineScope(Dispatchers.IO).launch {
                checkBattery()
            }
            editor.putBoolean(BATTERYTHREAD, true)
        }
        editor.apply()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupDrawerMenu()
        if (!screenRotated(savedInstanceState)) {
            NavigationManager.goToParksFragment(
                supportFragmentManager
            )
        }
    }

    override fun onDestroy() {
        Battery.unregisterListener()
        Accelerometer.unregisterListener()
        viewModel.unregisterListener()
        super.onDestroy()
    }

    private fun setupDrawerMenu() {
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        nav_drawer.setNavigationItemSelectedListener(this)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_icon, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        viewModel.registerParkMeNowListener(this)
        return super.onOptionsItemSelected(item)
    }

    suspend fun checkTime() {
        while (true) {
            delay(10000)
            var date = viewModel.getDate()
            val editor = preferences!!.edit()
            if(!preferences!!.getBoolean(DARK,false) && !preferences!!.getBoolean(LOW_BATTERY,false)){
                if ((date.toInt() >= 20 || date.toInt() < 8) && preferences!!.getBoolean(SETTING_2, true)) {
                    this.setTheme(R.style.AppThemeDark)
                    editor.putBoolean(DARK, true)
                    editor.apply()
                    Handler(Looper.getMainLooper()).post {
                        this.recreate()
                    }
                }
            }else if (!preferences!!.getBoolean(LOW_BATTERY,false)){
                if (date.toInt() >= 8 && date.toInt() != 20 && date.toInt() != 21 && date.toInt() != 22 && date.toInt() != 23 && preferences!!.getBoolean(SETTING_2, true)) {
                    this.setTheme(R.style.AppThemeLight)
                    editor.putBoolean(DARK, false)
                    editor.apply()
                    Handler(Looper.getMainLooper()).post {
                        this.recreate()
                    }
                }
            }
        }
    }

    suspend fun checkBattery(){
        while (true) {
            delay(10000)
            if (!preferences!!.getBoolean(IGNOREBATTERY, false)){
                val editor = preferences!!.edit()
                if(preferences!!.getBoolean(LOW_BATTERY,false) && preferences!!.getBoolean(SETTING_2, true) && !preferences!!.getBoolean(DARK, false)){
                    this.setTheme(R.style.AppThemeDark)
                    editor.putBoolean(DARK, true)
                    editor.apply()
                    Handler(Looper.getMainLooper()).post {
                        this.recreate()
                    }
                }
            }
        }
    }

    private fun screenRotated(savedInstanceState: Bundle?): Boolean {
        return savedInstanceState != null
    }

    override fun onParkChange(parkList: MutableList<Park>) {
        Handler(Looper.getMainLooper()).post {

            val park = viewModel.parkMeNow(parkList)
            if (park == null) {
                var toast = Toast.makeText(this, getString(R.string.noPark), Toast.LENGTH_LONG)
                toast.view.background.setTintList(
                    ContextCompat.getColorStateList(
                        this,
                        R.color.very_light_red
                    )
                )
                toast.show()
            } else if (park.distance == -1.0) {
                var toast =
                    Toast.makeText(this, getString(R.string.parkMeNowNoDistance), Toast.LENGTH_LONG)
                toast.view.background.setTintList(
                    ContextCompat.getColorStateList(
                        this,
                        R.color.very_light_red
                    )
                )
                toast.show()
            } else {
                val gmmIntentUri: Uri =
                    Uri.parse("google.navigation:q=${park.latitude},${park.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }
    }

    override fun connected() {
    }

    override fun disconnected() {
    }

    override fun onCurrentChanged(current: Double) {
        if (!preferences!!.getBoolean(IGNOREBATTERY, false)){
            if(current <= 20.0 && !(preferences!!.getBoolean(LOW_BATTERY, false)) && preferences!!.getBoolean(SETTING_2, true) && !(preferences!!.getBoolean(DARK, false)) && !dialog) {
                dialog = true
                fragmentManager?.let {
                    LowBatteryDialogFragment()
                        .show(supportFragmentManager, "lowbattery")
                }
            }else if(current>20.0 && (preferences!!.getBoolean(LOW_BATTERY, false))){
                val editor = preferences!!.edit()
                editor.putBoolean(LOW_BATTERY, false)
                editor.apply()
                dialog = false
            }
        }
    }


}