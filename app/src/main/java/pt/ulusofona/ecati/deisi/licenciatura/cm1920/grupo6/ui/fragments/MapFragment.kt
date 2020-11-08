package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.hardware.SensorEvent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.location.FusedLocation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.location.OnLocationChangedListener
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.accelerometer.Accelerometer
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.accelerometer.OnPhoneShake
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.adapters.InfoWindowAdapter
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils.NavigationManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.MapViewModel
import java.lang.Exception
import kotlin.math.abs

const val REQUEST_CODE = 100

class MapFragment : Fragment(), OnMapReadyCallback,
    OnLocationChangedListener, OnParkChange, GoogleMap.OnMarkerClickListener, OnPhoneShake {

    private var map: GoogleMap? = null
    private var marker: Marker? = null
    private lateinit var viewModel: MapViewModel
    val preferencesFileName = "ParkEmel_Preferences"
    val TYPE = "type"
    val OCCUPATION = "occupation"
    val DISTANCE = "distance"
    val CONNECTED = "connected"
    val DISCONNECTED = "disconnected"
    val SETTING_1 = "setting_1"
    var preferences: SharedPreferences? = null
    var dialog = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "${getString(R.string.app_name)} - ${getString(
            R.string.map
        )}"
        preferences = (activity as Context).getSharedPreferences(preferencesFileName, 0)
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        view.map_view.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onStart() {
        FusedLocation.registerListener(this)
        Accelerometer.registerListener(this)
        viewModel.registerListener(
            this
        )
        map_view.getMapAsync(this)
        map_view.onResume()
        super.onStart()
    }

    override fun onDestroy() {
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        viewModel.unregisterListener()
        Accelerometer.unregisterListener()
        super.onDestroy()
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        map?.setOnMarkerClickListener(this)
        map?.setOnInfoWindowClickListener(OnInfoWindowClickListener {
            fragmentManager?.let { viewModel.getSelectedPark().let { it1 ->
                NavigationManager.goToParkDetailsFragment(it,
                    it1!!)
            } }
        })
        map?.uiSettings?.isMapToolbarEnabled = false
        try{
            val location = viewModel.getLastLocation()!!.lastLocation
            val me = LatLng(location.latitude, location.longitude)
            marker = this.map?.addMarker(
                MarkerOptions().position(me)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.position))
            )
            val cameraPosition =
                CameraPosition.fromLatLngZoom(LatLng(location.latitude, location.longitude), 15.0F)
            this.map?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }catch (ex: Exception){
            var toast = Toast.makeText((activity as Context), getString(R.string.locationOff),Toast.LENGTH_LONG)
            toast.view.background.setTintList(
                ContextCompat.getColorStateList(
                    activity as Context,
                    R.color.very_light_red
                )
            )
            toast.show()
            val cameraPosition =
                CameraPosition.fromLatLngZoom(LatLng(38.736946, -9.142685), 12.5F)
            this.map?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    override fun onLocationChanged(locationResult: LocationResult) {
        marker?.remove()
        val location = locationResult.lastLocation
        val me = LatLng(location.latitude, location.longitude)
        marker = this.map?.addMarker(
            MarkerOptions().position(me)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.position))
        )
        viewModel.updateParkList()
    }

    @OnClick(R.id.filterParks)
    fun onClickFilters(view: View) {
        fragmentManager?.let {
            FilterDialogFragment()
                .show(childFragmentManager, "filters")
        }
    }

    @OnClick(R.id.recenter)
    fun onClickRecenter(view: View) {
        val location = viewModel.getLastLocation()!!.lastLocation
        val cameraPosition =
            CameraPosition.fromLatLngZoom(LatLng(location.latitude, location.longitude), 15.0F)
        this.map?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onParkChange(parkList: MutableList<Park>) {
        var filteredParks = viewModel.applyFilters(
            parkList, preferences!!.getString(TYPE, "Both"),
            preferences!!.getInt(DISTANCE, 0),
            preferences!!.getString(OCCUPATION, "All")
        )
        Handler(Looper.getMainLooper()).post {
            for (p in filteredParks) {
                lateinit var bitmap: BitmapDescriptor
                val currentOccupation: Int =
                    ((p.currentOccupation.toDouble() / p.maxCapacity.toDouble()) * 100).toInt()
                val iconToDisplay = viewModel.iconToDisplay(p.type, currentOccupation, p.active)
                if(iconToDisplay == "inactive"){
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.inactive_park)
                } else if (iconToDisplay == "sup_free") {
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.surface_parks_free)
                } else if (iconToDisplay == "sup_semi") {
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.surface_parks_semi)
                } else if (iconToDisplay == "sup_full") {
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.surface_parks_full)
                } else if (iconToDisplay == "str_free") {
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.structure_parks_free)
                } else if (iconToDisplay == "str_semi") {
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.structure_parks_semi)
                } else {
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.structure_parks_full)
                }
                val parkLocation = LatLng(p.latitude, p.longitude)

                val marker = this.map?.addMarker(
                    MarkerOptions().position(parkLocation).title(p.name).icon(bitmap)
                        .snippet("${getString(R.string.occupiedPlaces)} ${p.currentOccupation}\n" +
                                "${getString(R.string.capacity)} ${p.maxCapacity}\n" +
                                "${getString(R.string.lastUpdate)} ${p.lastUpdate}")
                )
                marker?.tag = p.uuid
                val markerInfoWindowAdapter =
                    InfoWindowAdapter(activity as Context, R.layout.park_map_details)
                map?.setInfoWindowAdapter(markerInfoWindowAdapter)
            }
        }
    }

    override fun connected() {
        if (!(preferences!!.getBoolean(CONNECTED, true))) {
            Handler(Looper.getMainLooper()).post {
                var toast = Toast.makeText(
                    activity as Context,
                    getString(R.string.connectionResumed),
                    Toast.LENGTH_LONG
                )
                toast.view.background.setTintList(
                    ContextCompat.getColorStateList(
                        activity as Context,
                        R.color.very_light_green
                    )
                )
                toast.show()
            }
            val editor = preferences!!.edit()
            editor.putBoolean(CONNECTED, true)
            editor.putBoolean(DISCONNECTED, false)
            editor.apply()
        }
    }

    override fun disconnected() {
        if (!(preferences!!.getBoolean(DISCONNECTED, false))) {
            Handler(Looper.getMainLooper()).post {
                var toast = Toast.makeText(
                    activity as Context,
                    getString(R.string.connectionLost),
                    Toast.LENGTH_LONG
                )
                toast.view.background.setTintList(
                    ContextCompat.getColorStateList(
                        activity as Context,
                        R.color.very_light_red
                    )
                )
                toast.show()
            }
            val editor = preferences!!.edit()
            editor.putBoolean(DISCONNECTED, true)
            editor.putBoolean(CONNECTED, false)
            editor.apply()
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if(marker?.title != null){
            (marker?.title).let { viewModel.setSelectedPark(it!!) }
            val selectedPark = viewModel.getSelectedPark()
            return selectedPark == null
        }
        return true
    }

    override fun onDestroyView() {
        Accelerometer.unregisterListener()
        super.onDestroyView()
    }

    override fun onPhoneShake(event: SensorEvent?) {
        if(preferences!!.getBoolean(SETTING_1,true)){
            if(event?.values?.get(0)?.let { abs(it) }!! >= 25 && !dialog){
                dialog = true
                fragmentManager?.let {
                    ClearFiltersDialogFragment()
                        .show(childFragmentManager, "clearfilters")
                }
            }
        }
    }
}