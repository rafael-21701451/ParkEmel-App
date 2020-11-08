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
import butterknife.OnClick
import kotlinx.android.synthetic.main.fragment_vehicles.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Vehicle
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.adapters.VehicleAdapter
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.VehiclesViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnVehicleChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils.NavigationManager

class VehiclesFragment : Fragment(),
    OnVehicleChange {

    private lateinit var viewModel: VehiclesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = getString(R.string.vehicles)
        val view = inflater.inflate(R.layout.fragment_vehicles, container, false)
        viewModel = ViewModelProviders.of(this).get(VehiclesViewModel::class.java)
        ButterKnife.bind(this,view)
        return view
    }

    override fun onStart() {
        viewModel.registerListener(this)
        super.onStart()
    }

    override fun onVehicleChange(vehicleList: MutableList<Vehicle>) {
        Handler(Looper.getMainLooper()).post{
            vehicles?.layoutManager = LinearLayoutManager(activity as Context)
            val vehicleAdapter =
                VehicleAdapter(
                    activity as Context,
                    R.layout.vehicle_display,
                    vehicleList
                )
            vehicleAdapter.vm = viewModel
            vehicles?.adapter = vehicleAdapter
        }
    }

    override fun goToMessage(message: String) {
        val uri = Uri.parse("smsto:3838")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", message)
        startActivity(intent)
    }

    override fun onDestroy() {
        viewModel = ViewModelProviders.of(this).get(VehiclesViewModel::class.java)
        viewModel.unregisterListener()
        super.onDestroy()
    }

    @OnClick(R.id.addVehicle)
    fun onClickAddVehicle(view: View){
        fragmentManager?.let { NavigationManager.goToAddVehicleFragment(it) }
    }
}
