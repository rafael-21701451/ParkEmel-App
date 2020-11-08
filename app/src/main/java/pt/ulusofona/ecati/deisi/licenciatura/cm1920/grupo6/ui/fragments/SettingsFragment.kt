package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.fragment_settings.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R

class SettingsFragment : Fragment() {

    val preferencesFileName = "ParkEmel_Preferences"
    val SETTING_1 = "setting_1"
    val SETTING_2 = "setting_2"
    val IGNOREBATTERY = "ignorebattery"
    var preferences: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = this.getString(R.string.settings)
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        preferences = (activity as Context).getSharedPreferences(preferencesFileName,0)
        view.switch1.isChecked = preferences!!.getBoolean(SETTING_1,true)
        view.switch2.isChecked = preferences!!.getBoolean(SETTING_2,true)
        ButterKnife.bind(this, view)
        return view
    }

    @OnClick(R.id.switch1)
    fun onClickSwitch1(view: View){
        if (view.switch1.isChecked){
            view.switch1.isChecked = true
            val editor = preferences!!.edit()
            editor.putBoolean(SETTING_1,true)
            editor.apply()
        }else{
            view.switch1.isChecked = false
            val editor = preferences!!.edit()
            editor.putBoolean(SETTING_1,false)
            editor.apply()
        }
    }

    @OnClick(R.id.switch2)
    fun onClickSwitch2(view: View){
        if (view.switch2.isChecked){
            view.switch2.isChecked = true
            val editor = preferences!!.edit()
            editor.putBoolean(SETTING_2,true)
            editor.putBoolean(IGNOREBATTERY,false)
            editor.apply()
            activity?.recreate()
        }else{
            view.switch2.isChecked = false
            val editor = preferences!!.edit()
            editor.putBoolean(SETTING_2,false)
            editor.apply()
            activity?.recreate()
        }
    }
}