package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.net.Uri
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
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.CancelableCallback
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_trip_result.*
import kotlinx.android.synthetic.main.fragment_trip_result.park_name
import kotlinx.android.synthetic.main.fragment_trip_result.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.GiraStation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnGiraChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.TripPlannerViewModel
import java.math.RoundingMode

class TripResultFragment : Fragment(), OnParkChange, OnGiraChange, OnMapReadyCallback {

    private lateinit var vm: TripPlannerViewModel
    private var map: GoogleMap? = null

    val preferencesFileName = "ParkEmel_Preferences"
    val CONNECTED = "connected"
    val DISCONNECTED = "disconnected"
    var preferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = getString(R.string.trip_planner_result)
        preferences = (activity as Context).getSharedPreferences(preferencesFileName, 0)
        val view = inflater.inflate(R.layout.fragment_trip_result, container, false)
        view.trip_map.onCreate(savedInstanceState)
        vm = ViewModelProviders.of(this).get(TripPlannerViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onStart() {
        trip_map.getMapAsync(this)
        trip_map.onResume()
        vm.registerListener(this, this)
        super.onStart()
    }

    override fun onDestroy() {
        vm = ViewModelProviders.of(this).get(TripPlannerViewModel::class.java)
        vm.unregister()
        super.onDestroy()
    }

    override fun onParkChange(parkList: MutableList<Park>) {
        vm.saveParks(parkList)
        vm.getGiras()
    }


    override fun onGiraChange(giraList: MutableList<GiraStation>) {
        Handler(Looper.getMainLooper()).post {
            val results = vm.getResults(giraList)
            if (results[0] as Boolean) {
                Toast.makeText(activity as Context, getString(R.string.noResults), Toast.LENGTH_LONG).show()
            } else {
                if (results[1] != null) {
                    trip_map.visibility = View.VISIBLE
                    card1.visibility = View.VISIBLE
                    card4.visibility = View.VISIBLE

                    this.map?.setOnMapClickListener {
                        val gmmIntentUri: Uri =
                            Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${(results[1] as Park).latitude},${(results[1] as Park).longitude}&destination=${vm.getAddress()[0]},${vm.getAddress()[1]}&waypoints=18.520561,73.872435|18.519254,73.876614|18.52152,73.877327|18.52019,73.879935&travelmode=driving")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        startActivity(mapIntent)
                    }

                    park_name.text = (results[1] as Park).name
                    freeSpots.text = "${getString(R.string.freeSpots)} ${(results[1] as Park).currentOccupation}"
                    finalAddress.text = vm.getAddressName()

                    val parkLocation = Location("park")
                    parkLocation.latitude = (results[1] as Park).latitude
                    parkLocation.longitude = (results[1] as Park).longitude

                    val addressLocation = Location("address")
                    addressLocation.latitude = vm.getAddress()[0]
                    addressLocation.longitude = vm.getAddress()[1]

                    distance1.text = "${getString(R.string.distance3)} ${(parkLocation.distanceTo(addressLocation)/1000).toBigDecimal().setScale(2,
                        RoundingMode.UP).toDouble()} Km"



                    val pos = LatLng((results[1] as Park).latitude, (results[1] as Park).longitude)

                    val pos1 = LatLng(vm.getAddress()[0], vm.getAddress()[1])

                    val b = LatLngBounds.Builder()
                    b.include(pos)
                    b.include(pos1)


                    this.map?.addMarker(
                        MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.step_1))
                    )

                    this.map?.addMarker(
                        MarkerOptions().position(pos1).icon(BitmapDescriptorFactory.fromResource(R.drawable.finish))
                    )



                    val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(b.build(), 10)
                    map?.animateCamera(cu, 30, object : CancelableCallback {
                        override fun onFinish() {
                            val cp = map?.cameraPosition?.let {
                                CameraPosition.Builder()
                                    .zoom(11.5F)
                                    .target(pos)
                                    .build()
                            }
                            map?.moveCamera(CameraUpdateFactory.newCameraPosition(cp))
                        }
                        override fun onCancel() {
                        }
                    })

                }
                if (results[2] != null) {
                    card2.visibility = View.VISIBLE
                    card3.visibility = View.VISIBLE
                    inicial_bike.text = (results[2] as GiraStation).name.substring(6)
                    free_initial_bikes.text = "${getString(R.string.freeBikes)} ${(results[2] as GiraStation).bikeNum}"
                    final_bike.text = (results[3] as GiraStation).name.substring(6)
                    free_final_docks.text = "${getString(R.string.freeDocks)} ${((results[2] as GiraStation).numDocks) - ((results[2] as GiraStation).bikeNum)}"

                    this.map?.setOnMapClickListener {
                        val gmmIntentUri: Uri =
                            Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${(results[1] as Park).latitude},${(results[1] as Park).longitude}&destination=${vm.getAddress()[0]},${vm.getAddress()[1]}&waypoints=${(results[2] as GiraStation).latitude},${(results[2] as GiraStation).longitude}|${(results[3] as GiraStation).latitude},${(results[3] as GiraStation).longitude}&travelmode=driving")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        startActivity(mapIntent)
                    }

                    val pos = LatLng((results[1] as Park).latitude, (results[1] as Park).longitude)
                    val pos1 = LatLng(vm.getAddress()[0], vm.getAddress()[1])
                    val pos2 = LatLng((results[2] as GiraStation).latitude, (results[2] as GiraStation).longitude)
                    val pos3 = LatLng((results[3] as GiraStation).latitude, (results[3] as GiraStation).longitude)

                    val parkLocation = Location("park")
                    parkLocation.latitude = (results[1] as Park).latitude
                    parkLocation.longitude = (results[1] as Park).longitude

                    val giraStartLocation = Location("giraStart")
                    giraStartLocation.latitude = (results[2] as GiraStation).latitude
                    giraStartLocation.longitude = (results[2] as GiraStation).longitude

                    val giraEndLocation = Location("park")
                    giraEndLocation.latitude = (results[3] as GiraStation).latitude
                    giraEndLocation.longitude = (results[3] as GiraStation).longitude

                    val addressLocation = Location("address")
                    addressLocation.latitude = vm.getAddress()[0]
                    addressLocation.longitude = vm.getAddress()[1]

                    distance1.text = "${getString(R.string.distance1)} ${(parkLocation.distanceTo(giraStartLocation)/1000).toBigDecimal().setScale(2,
                        RoundingMode.UP).toDouble()} Km"

                    distance2.text = "${getString(R.string.distance2)} ${(giraStartLocation.distanceTo(giraEndLocation)/1000).toBigDecimal().setScale(2,
                        RoundingMode.UP).toDouble()} Km"

                    distance3.text = "${getString(R.string.distance3)} ${(giraEndLocation.distanceTo(addressLocation)/1000).toBigDecimal().setScale(2,
                        RoundingMode.UP).toDouble()} Km"

                    val b = LatLngBounds.Builder()
                    b.include(pos)
                    b.include(pos1)
                    b.include(pos2)
                    b.include(pos3)


                    this.map?.addMarker(
                        MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.step_1))
                    )

                    this.map?.addMarker(
                        MarkerOptions().position(pos1).icon(BitmapDescriptorFactory.fromResource(R.drawable.finish))
                    )

                    this.map?.addMarker(
                        MarkerOptions().position(pos2).icon(BitmapDescriptorFactory.fromResource(R.drawable.step_2))
                    )

                    this.map?.addMarker(
                        MarkerOptions().position(pos3).icon(BitmapDescriptorFactory.fromResource(R.drawable.step_3))
                    )


                    val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(b.build(), 10)
                    map?.animateCamera(cu, 30, object : CancelableCallback {
                        override fun onFinish() {
                            val cp = map?.cameraPosition?.let {
                                CameraPosition.Builder()
                                    .zoom(11.5F)
                                    .target(pos)
                                    .build()
                            }
                            map?.moveCamera(CameraUpdateFactory.newCameraPosition(cp))
                        }
                        override fun onCancel() {
                        }
                    })
                }
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

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        map?.uiSettings?.isMapToolbarEnabled = false
    }
}