package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_park_details.*
import kotlinx.android.synthetic.main.fragment_park_details.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkGet
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.ParkDetailsViewModel

private const val NAME = "name"

class ParkDetailsFragment : Fragment(), OnParkGet, OnMapReadyCallback {

    private lateinit var viewModel: ParkDetailsViewModel
    private var name: String? = null
    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(NAME)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String) =
            ParkDetailsFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(NAME, name)
                    }
                }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = getString(R.string.viewPark)
        val view = inflater.inflate(R.layout.fragment_park_details, container, false)
        view.map_view_park_details.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ParkDetailsViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onStart() {
        map_view_park_details.getMapAsync(this)
        map_view_park_details.onResume()
        name?.let { viewModel.registerGetParkListener(this, it) }
        super.onStart()
    }

    override fun onParkGet(park: Park) {
        Handler(Looper.getMainLooper()).post {
            var currentOccupation: Double =
                (park.currentOccupation.toDouble() / park.maxCapacity.toDouble()) * 100
            pName.text = "${getString(R.string.name)}"
            park_name.text = "${park.name}"
            pCoordinates.text = "${getString(R.string.coordinates)}"
            coordinates.text = "${park.latitude}, ${park.longitude}"
            pDistance.text = "${getString(R.string.distance)}"
            var location = Location("park")
            location.latitude = park.latitude
            location.longitude = park.longitude
            distance.text = "${park.distance} Km"
            pOccupation.text = "${getString(R.string.occupation)}"
            if (currentOccupation > 100.0){
                currentOccupation = 100.0
            }
            occupation.text = "${currentOccupation.toInt()} %"
            pType.text = "${getString(R.string.type)}"
            if (park.type.substring(0, 1) == "S") {
                type.text = "${getString(R.string.surface)}"
            } else {
                type.text = "${getString(R.string.structure)}"
            }
            pCapacity.text = "${getString(R.string.capacity)}"
            capacity.text = "${park.maxCapacity}"
            pLastUpdate.text = "${getString(R.string.lastUpdate)}"
            lastUpdate.text = "${park.lastUpdate}"

            val parkSelected = LatLng(park.latitude, park.longitude)
            this.map?.addMarker(MarkerOptions().position(parkSelected))
            val cameraPosition =
                CameraPosition.fromLatLngZoom(LatLng(park.latitude, park.longitude), 15.0F)
            this.map?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    override fun onDestroy() {
        viewModel = ViewModelProviders.of(this).get(ParkDetailsViewModel::class.java)
        viewModel.unregisterListener()
        super.onDestroy()
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        map?.uiSettings?.isScrollGesturesEnabled = false
        map?.uiSettings?.isScrollGesturesEnabledDuringRotateOrZoom = false
        map?.uiSettings?.isZoomGesturesEnabled = false
        map?.uiSettings?.isMapToolbarEnabled = false
        map?.setOnMapClickListener {
            val gmmIntentUri: Uri = Uri.parse("google.navigation:q=${coordinates.text}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
        map?.setOnMarkerClickListener {
            val gmmIntentUri: Uri = Uri.parse("google.navigation:q=${coordinates.text}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
            return@setOnMarkerClickListener false
        }
    }
}