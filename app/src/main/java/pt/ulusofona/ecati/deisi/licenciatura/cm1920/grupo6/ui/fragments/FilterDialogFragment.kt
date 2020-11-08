package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.fragment_filter_dialog.*
import kotlinx.android.synthetic.main.fragment_filter_dialog.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils.NavigationManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.ParksViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R

class FilterDialogFragment : DialogFragment(){

    val preferencesFileName = "ParkEmel_Preferences"
    val TYPE = "type"
    val SURFACE = "surface"
    val STRUCTURE = "structure"
    val OCCUPATION = "occupation"
    val FREE = "free"
    val PFULL = "pfull"
    val FULL = "full"
    val DISTANCE = "distance"
    var preferences: SharedPreferences? = null
    var distance : Double = 0.0
    private lateinit var viewModel: ParksViewModel
    private lateinit var seekBar: SeekBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter_dialog, container, false)
        preferences = (activity as Context).getSharedPreferences(preferencesFileName,0)
        if(preferences!!.getBoolean(SURFACE,false)){
            view.surface.isChecked = true
        }else if(preferences!!.getBoolean(STRUCTURE,false)){
            view.structure.isChecked = true
        }
        if(preferences!!.getBoolean(FREE,false)){
            view.checkbox_free.isChecked = true
        }
        if(preferences!!.getBoolean(PFULL,false)){
            view.checkbox_partially_Full.isChecked = true
        }
        if(preferences!!.getBoolean(FULL,false)){
            view.checkbox_full.isChecked = true
        }
        seekBar = view.SeekBar
        seekBar.progress = preferences!!.getInt(DISTANCE,0)
        if(seekBar.progress == 0){
            distance = 0.0
            view.distanceValue.text = getString(R.string.any)
        }else{
            distance = seekBar.progress.toDouble()
            distance /= 10.0
            view.distanceValue.text = "${getString(R.string.less)} ${distance} Km"
        }
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress == 0){
                    distance = 0.0
                    view.distanceValue.text = getString(R.string.any)
                }else{
                    distance = progress.toDouble()
                    distance /= 10.0
                    view.distanceValue.text = "${getString(R.string.less)} ${distance} Km"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        ButterKnife.bind(this,view)
        viewModel = ViewModelProviders.of(this).get(ParksViewModel::class.java)

        return view
    }

    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = RelativeLayout.LayoutParams.MATCH_PARENT
        params?.height = RelativeLayout.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    @OnClick(R.id.apply)
    fun onClickApply(view: View){
        val editor = preferences!!.edit()
        if(surface.isChecked){
            editor.putBoolean(SURFACE,true)
            editor.putBoolean(STRUCTURE,false)
            editor.putString(TYPE,"Superfície")
            editor.apply()
        }else if(structure.isChecked){
            editor.putBoolean(STRUCTURE,true)
            editor.putBoolean(SURFACE,false)
            editor.putString(TYPE,"Estrutura")
            editor.apply()
        }else{
            editor.putBoolean(SURFACE,false)
            editor.putBoolean(STRUCTURE,false)
            editor.putString(TYPE,"Both")
            editor.apply()
        }
        if(checkbox_free.isChecked) {
            editor.putBoolean(FREE, true)
            editor.apply()
        } else{
            editor.putBoolean(FREE, false)
            editor.apply()
        }
        if(checkbox_partially_Full.isChecked) {
            editor.putBoolean(PFULL, true)
            editor.apply()
        }else{
            editor.putBoolean(PFULL, false)
            editor.apply()
        }
        if(checkbox_full.isChecked) {
            editor.putBoolean(FULL, true)
            editor.apply()
        }else{
            editor.putBoolean(FULL, false)
            editor.apply()
        }
        occupationFilter(checkbox_free.isChecked, checkbox_partially_Full.isChecked, checkbox_full.isChecked)
        editor.putInt(DISTANCE, (distance*10).toInt())
        editor.apply()
        if(parentFragment?.activity?.title == "${getString(R.string.app_name)} - ${getString(
                R.string.parks
            )}"){
            parentFragment?.fragmentManager?.let {
                NavigationManager.reloadParksFragment(
                    it
                )
            }
        }else{
            parentFragment?.fragmentManager?.let {
                NavigationManager.reloadMapFragment(
                    it
                )
            }
        }
        dismiss()
    }

    @OnClick(R.id.clear)
    fun onClickClear(view: View){
        val editor = preferences!!.edit()
        editor.putBoolean(SURFACE,false)
        editor.putBoolean(STRUCTURE,false)
        editor.putBoolean(FREE,false)
        editor.putBoolean(PFULL,false)
        editor.putBoolean(FULL,false)
        editor.putString(TYPE,"Both")
        editor.putString(OCCUPATION,"All")
        editor.putInt(DISTANCE,0)
        editor.apply()
        if(parentFragment?.activity?.title == "${getString(R.string.app_name)} - ${getString(
                R.string.parks
            )}"){
            parentFragment?.fragmentManager?.let {
                NavigationManager.reloadParksFragment(
                    it
                )
            }
        }else{
            parentFragment?.fragmentManager?.let {
                NavigationManager.reloadMapFragment(
                    it
                )
            }
        }
        dismiss()
    }

    fun occupationFilter(free: Boolean, pFull: Boolean, full: Boolean){
        val editor = preferences!!.edit()
        if ((free && pFull && full) || (!free && !pFull && !full)){
            editor.putString(OCCUPATION, "All")
        } else if (free && pFull && !(full)){
            editor.putString(OCCUPATION, "Free_PFull")
        } else if (free && !(pFull) && full){
            editor.putString(OCCUPATION, "Free_Full")
        } else if (!(free)&& pFull && full) {
            editor.putString(OCCUPATION, "PFull_Full")
        } else if (!(free) && !(pFull) && full){
            editor.putString(OCCUPATION, "Full")
        } else if (free && !(pFull) && !(full)){
            editor.putString(OCCUPATION, "Free")
        } else if (!(free) && pFull && !(full)){
            editor.putString(OCCUPATION, "PFull")
        }
        editor.apply()
    }
}