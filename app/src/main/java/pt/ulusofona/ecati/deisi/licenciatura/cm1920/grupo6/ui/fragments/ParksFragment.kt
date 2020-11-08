package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.OnClick
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.location.FusedLocation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.location.OnLocationChangedListener
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.fragment_parks.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.accelerometer.Accelerometer
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.accelerometer.OnPhoneShake
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.adapters.ParkAdapter
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.GoToPark
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.ParksViewModel
import kotlin.math.abs

class ParksFragment : PermissionedFragment(REQUEST_CODE),
    OnParkChange, OnLocationChangedListener, GoToPark, OnPhoneShake {
    private lateinit var viewModel: ParksViewModel
    var dialog = false
    val preferencesFileName = "ParkEmel_Preferences"
    val TYPE = "type"
    val OCCUPATION = "occupation"
    val DISTANCE = "distance"
    val CONNECTED = "connected"
    val DISCONNECTED = "disconnected"
    val DIALOG = "dialog"
    val FIRSTSTART = "firststart"
    val NODISTANCE = "nodistance"
    val SETTING_1 = "setting_1"
    var preferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = "${getString(R.string.app_name)} - ${getString(R.string.parks)}"
        preferences = (activity as Context).getSharedPreferences(preferencesFileName, 0)
        val view = inflater.inflate(R.layout.fragment_parks, container, false)
        viewModel = ViewModelProviders.of(this).get(ParksViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onStart() {
        super.onRequestPermissions(
            activity?.baseContext!!,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        if (preferences!!.getBoolean(DIALOG, false)) {
            fragmentManager?.let {
                NoConnectionDialogFragment()
                    .show(childFragmentManager, "noConnection")
            }
            val editor = preferences!!.edit()
            editor.putBoolean(DIALOG, false)
            editor.apply()
        }
        super.onStart()
    }

    override fun onRequestPermissionsSuccess() {
        FusedLocation.start(activity as Context)
        FusedLocation.registerListener(this)
        viewModel.registerListener(
            this,
            this
        )
        Accelerometer.registerListener(this)
    }

    override fun onRequestPermissionsFailure() {
        Toast.makeText(activity as Context, getString(R.string.noPermissions), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onParkChange(parkList: MutableList<Park>) {
        Handler(Looper.getMainLooper()).post {
            if(parkList[0].distance == -1.0 && !preferences!!.getBoolean(NODISTANCE,false)){
                var toast = Toast.makeText(activity as Context, getString(R.string.noDistanceToast), Toast.LENGTH_LONG)
                toast.view.background.setTintList(
                    ContextCompat.getColorStateList(
                        activity as Context,
                        R.color.very_light_red
                    )
                )
                toast.show()
                val editor = preferences!!.edit()
                editor.putBoolean(NODISTANCE, true)
                editor.apply()
            }else if (parkList[0].distance != -1.0){
                val editor = preferences!!.edit()
                editor.putBoolean(NODISTANCE, false)
                editor.apply()
            }
            var filteredParks = viewModel.applyFilters(
                parkList, preferences!!.getString(TYPE, "Both"),
                preferences!!.getInt(DISTANCE, 0),
                preferences!!.getString(OCCUPATION, "All")
            )
            parks?.layoutManager = LinearLayoutManager(activity as Context)
            val parkAdapter =
                ParkAdapter(
                    activity as Context,
                    R.layout.park_display,
                    filteredParks
                )
            parkAdapter.vm = viewModel
            val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    parkAdapter.goToPark(viewHolder.adapterPosition)
                }
            }
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(parks)
            parks?.adapter = parkAdapter
        }

    }

    override fun connected() {
        if(!preferences!!.getBoolean(FIRSTSTART,true)){
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
        }else{
            val editor = preferences!!.edit()
            editor.putBoolean(FIRSTSTART, false)
            editor.apply()
        }

    }

    override fun disconnected() {
        if(!preferences!!.getBoolean(FIRSTSTART,true)) {
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
    }

    override fun onDestroy() {
        viewModel = ViewModelProviders.of(this).get(ParksViewModel::class.java)
        viewModel.unregisterListener()
        Accelerometer.unregisterListener()
        super.onDestroy()
    }


    @OnClick(R.id.filterParks)
    fun onClickFilters(view: View) {
        fragmentManager?.let {
            FilterDialogFragment()
                .show(childFragmentManager, "filters")
        }
    }

    override fun goToPark(park: Park) {
        val gmmIntentUri: Uri = Uri.parse("google.navigation:q=${park.latitude},${park.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    override fun onLocationChanged(locationResult: LocationResult) {
        viewModel.updateParkList()
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