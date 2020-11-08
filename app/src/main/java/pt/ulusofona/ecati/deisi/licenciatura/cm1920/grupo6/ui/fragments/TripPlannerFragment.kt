package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.fragment_trip_planner.*
import kotlinx.android.synthetic.main.fragment_trip_planner.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils.NavigationManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.TripPlannerViewModel
import java.util.*

class TripPlannerFragment : Fragment() {

    private lateinit var vm : TripPlannerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = getString(R.string.trip_planner)
        val view = inflater.inflate(R.layout.fragment_trip_planner, container, false)
        vm = ViewModelProviders.of(this).get(TripPlannerViewModel::class.java)

        view.addressInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                view.address.error = null
            }
        })

        ButterKnife.bind(this, view)
        return view
    }

    @OnClick(R.id.goToSearchResults)
    fun onClickSearch(view: View) {
        val imm: InputMethodManager =
            context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        if (addressInput.text.toString().isEmpty()) {
            address.error = getString(R.string.errorAddress)
        } else {
            address.error = null
        }
        if (address.error == null) {
            try{
                var geoCoder = Geocoder(activity as Context, Locale("pt", "PT", "PT"))
                if (geoCoder.getFromLocationName(addressInput.text.toString(), 1).size == 0){
                    address.error = getString(R.string.invalidAddress)
                } else {
                    vm.setAddress(geoCoder.getFromLocationName(addressInput.text.toString(), 1)[0].latitude, geoCoder.getFromLocationName(addressInput.text.toString(), 1)[0].longitude, geoCoder.getFromLocationName(addressInput.text.toString(), 1)[0].getAddressLine(0))
                    Log.i(TAG, geoCoder.getFromLocationName(addressInput.text.toString(), 1)[0].toString())
                    fragmentManager?.let { NavigationManager.goToTripResultFragment(it) }
                }
            } catch (ex: Exception){
                var toast = Toast.makeText(activity as Context, getString(R.string.noConnection), Toast.LENGTH_SHORT)
                toast.view.background.setTintList(
                    ContextCompat.getColorStateList(
                        activity as Context,
                        R.color.very_light_red
                    )
                )
                toast.show()
            }
        }
    }
}