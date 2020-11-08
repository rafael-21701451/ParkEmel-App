package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.fragment_select_vehicle.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Vehicle
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.adapters.SelectVehicleAdapter
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.CheckVehicles
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.VehiclesViewModel

class SelectVehicleFragment : Fragment(), CheckVehicles {

    private lateinit var viewModel: VehiclesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = getString(R.string.select_vehicle)
        val view = inflater.inflate(R.layout.fragment_select_vehicle, container, false)
        viewModel = ViewModelProviders.of(this).get(VehiclesViewModel::class.java)
        ButterKnife.bind(this,view)
        return view
    }

    override fun onStart() {
        viewModel.registerCheckVehiclesListener(this)
        viewModel.checkVehicles()
        super.onStart()
    }

    override fun onDestroy() {
        viewModel = ViewModelProviders.of(this).get(VehiclesViewModel::class.java)
        viewModel.unregisterListener()
        super.onDestroy()
    }

    override fun userHasVehicles(vehicles: MutableList<Vehicle>) {
        Handler(Looper.getMainLooper()).post{
            vehicleSelect?.layoutManager = LinearLayoutManager(activity as Context)
            val vehicleAdapter =
                SelectVehicleAdapter(
                    activity as Context,
                    R.layout.vehicle_select,
                    vehicles
                )
            vehicleAdapter.vm = viewModel
            vehicleSelect.adapter = vehicleAdapter
        }
    }

    override fun goToMessage(message: String) {
        val uri = Uri.parse("smsto:3838")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", message)
        startActivity(intent)
    }
}