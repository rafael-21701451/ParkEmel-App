package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.fragment_add_vehicle.brand
import kotlinx.android.synthetic.main.fragment_add_vehicle.brandInput
import kotlinx.android.synthetic.main.fragment_add_vehicle.date
import kotlinx.android.synthetic.main.fragment_add_vehicle.dateInput
import kotlinx.android.synthetic.main.fragment_add_vehicle.license
import kotlinx.android.synthetic.main.fragment_add_vehicle.licenseInput
import kotlinx.android.synthetic.main.fragment_add_vehicle.model
import kotlinx.android.synthetic.main.fragment_add_vehicle.modelInput
import kotlinx.android.synthetic.main.fragment_add_vehicle.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils.NavigationManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.CheckIfLicenseExists
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.VehiclesViewModel

class AddVehicleFragment : Fragment(), CheckIfLicenseExists {
    private lateinit var viewModel: VehiclesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.add_vehicle)
        val view = inflater.inflate(R.layout.fragment_add_vehicle, container, false)

        view.dateInput.addTextChangedListener(object : TextWatcher {
            var lastCount = 5
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                view.date.error = null
                if(s.length==2  && lastCount<s.length && view.dateInput.selectionStart == 2){
                    view.dateInput.setText("${view.dateInput.text}/")
                    view.dateInput.setSelection(s.length+1)
                }
                if(s.length == 5 && view.dateInput.selectionStart == 5){
                    view.addVehicleButton.requestFocus()
                }
                lastCount = s.length
            }
        })

        view.licenseInput.addTextChangedListener(object : TextWatcher {
            var lastCount = 0
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                view.license.error = null
                if((s.length==2 || s.length==5) && lastCount<s.length && (view.licenseInput.selectionStart == 2 || view.licenseInput.selectionStart == 5)){
                    view.licenseInput.setText("${view.licenseInput.text}-")
                    view.licenseInput.setSelection(s.length+1)
                }
                if(s.length == 8 && view.licenseInput.selectionStart == 8){
                    view.dateInput.requestFocus()
                }
                lastCount = s.length
            }
        })

        view.brandInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                view.brand.error = null
                if(s.length == 10 && view.brandInput.selectionStart == 10){
                    view.modelInput.requestFocus()
                }
            }
        })

        view.modelInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                view.model.error = null
                if(s.length == 10 && view.modelInput.selectionStart == 10){
                    view.licenseInput.requestFocus()
                }
            }
        })
        viewModel = ViewModelProviders.of(this).get(VehiclesViewModel::class.java)
        ButterKnife.bind(this,view)
        return view
    }

    override fun onStart() {
        viewModel.registerListenerLicenseCheck(this)
        super.onStart()
    }

    @OnClick(R.id.addVehicleButton)
    fun onClickAddVehicleButton(view: View) {
        val imm: InputMethodManager =
            context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        if (brandInput.text.toString().isEmpty()) {
            brand.error = getString(R.string.brandError)
        } else {
            brand.error = null
        }
        if (modelInput.text.toString().isEmpty()) {
            model.error = getString(R.string.modelError)
        } else {
            model.error = null
        }
        if (licenseInput.text.toString().isEmpty()) {
            license.error = getString(R.string.licenseError)
        } else if (licenseInput.text.toString().length !=8 || licenseInput.text.toString().elementAt(2) != '-' || licenseInput.text.toString().elementAt(5) != '-'){
            license.error = getString(R.string.licenseError2)
        }else {
            license.error = null
        }
        if (dateInput.text.toString().isEmpty()) {
            date.error = getString(R.string.dateError)
        } else if (dateInput.text.toString().length !=5 || dateInput.text.toString().elementAt(2) != '/' || dateInput.text.toString().substring(0,2).toInt() !in 1..12){
            date.error = getString(R.string.invalid_date)
        } else {
            date.error = null
        }
        if (brand.error == null && model.error == null && license.error == null && date.error == null) {
            viewModel.onAddVehicleButton(brandInput.text.toString(), modelInput.text.toString(), licenseInput.text.toString(), dateInput.text.toString())
        }
    }

    override fun checkIfLicenseExists(exists: Boolean) {
        Handler(Looper.getMainLooper()).post {
            if (exists) {
                license.error = getString(R.string.duplicateLicense)
            } else {
                fragmentManager?.let {
                    NavigationManager.goToVehiclesFragment(
                        it
                    )
                }
            }
        }
    }
}
