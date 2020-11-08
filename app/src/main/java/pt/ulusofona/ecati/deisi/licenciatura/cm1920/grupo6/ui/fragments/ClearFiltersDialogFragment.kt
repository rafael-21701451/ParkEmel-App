package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Context
import android.content.DialogInterface
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
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.activities.MainActivity
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils.NavigationManager

class ClearFiltersDialogFragment : DialogFragment(){

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.clear_filters_dialog, container, false)
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

    override fun onDismiss(dialog: DialogInterface) {
        if(activity?.title == "${getString(R.string.app_name)} - ${getString(
                R.string.parks
            )}"){
            (parentFragment as ParksFragment).dialog = false
        }else if (activity?.title == "${getString(R.string.app_name)} - ${getString(
                R.string.map
            )}"){
            (parentFragment as MapFragment).dialog = false
        }
        super.onDismiss(dialog)
    }

    @OnClick(R.id.cancel)
    fun onClickIgnore(view: View){
        if(activity?.title == "${getString(R.string.app_name)} - ${getString(
                R.string.parks
            )}"){
            (parentFragment as ParksFragment).dialog = false
        }else if (activity?.title == "${getString(R.string.app_name)} - ${getString(
                R.string.map
            )}"){
            (parentFragment as MapFragment).dialog = false
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
        if(activity?.title == "${getString(R.string.app_name)} - ${getString(
                R.string.parks
            )}"){
            (parentFragment as ParksFragment).dialog = false
            parentFragment?.fragmentManager?.let {
                NavigationManager.reloadParksFragment(
                    it
                )
            }
        }else if (activity?.title == "${getString(R.string.app_name)} - ${getString(
                R.string.map
            )}"){
            (parentFragment as MapFragment).dialog = false
            parentFragment?.fragmentManager?.let {
                NavigationManager.reloadMapFragment(
                    it
                )
            }
        }
        dismiss()
    }
}