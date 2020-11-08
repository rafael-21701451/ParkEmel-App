package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import butterknife.ButterKnife
import butterknife.OnClick
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R

class LowBatteryDialogFragment : DialogFragment(){

    val DARK = "dark"
    val LOW_BATTERY = "low_battery"
    val IGNOREBATTERY = "ignorebattery"
    var preferences: SharedPreferences? = null
    val preferencesFileName = "ParkEmel_Preferences"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.low_battery_dialog, container, false)
        ButterKnife.bind(this,view)
        preferences = (activity as Context).getSharedPreferences(preferencesFileName, 0)
        return view
    }

    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = RelativeLayout.LayoutParams.MATCH_PARENT
        params?.height = RelativeLayout.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    @OnClick(R.id.ignore)
    fun onClickIgnore(view: View){
        val editor = preferences!!.edit()
        editor.putBoolean(IGNOREBATTERY, true)
        editor.apply()
        dismiss()
    }

    @OnClick(R.id.activate)
    fun onClickActivate(view: View){
        val editor = preferences!!.edit()
        editor.putBoolean(LOW_BATTERY, true)
        editor.putBoolean(DARK, true)
        editor.apply()
        activity?.recreate()
        dismiss()
    }
}
