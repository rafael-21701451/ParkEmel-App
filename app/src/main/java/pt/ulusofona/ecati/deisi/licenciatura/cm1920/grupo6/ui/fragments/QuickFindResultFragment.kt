package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
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
import kotlinx.android.synthetic.main.fragment_quick_find_result.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.accelerometer.QuickFindAccelerometer
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.activities.MainActivity
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.adapters.ParkAdapter
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.GoToPark
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.OnParkChange
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.ParksViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.QuickFindResultViewModel

class QuickFindResultFragment : Fragment(), OnParkChange, GoToPark {

    private lateinit var viewModel: QuickFindResultViewModel

    val preferencesFileName = "ParkEmel_Preferences"
    val CONNECTED = "connected"
    val DISCONNECTED = "disconnected"
    var preferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = getString(R.string.quick_find)
        preferences = (activity as Context).getSharedPreferences(preferencesFileName, 0)
        (activity as MainActivity).quickFind = true
        val view = inflater.inflate(R.layout.fragment_quick_find_result, container, false)
        viewModel = ViewModelProviders.of(this).get(QuickFindResultViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onParkChange(parkList: MutableList<Park>) {
        val resultParks = viewModel.quickFindResult(parkList)
        Handler(Looper.getMainLooper()).post {
            if (resultParks.size == 0){
                var toast = Toast.makeText(activity as Context, getString(R.string.noParksNearYou), Toast.LENGTH_LONG)
                toast.view.background.setTintList(
                    ContextCompat.getColorStateList(
                        activity as Context,
                        R.color.very_light_red
                    )
                )
                toast.show()
            }
            parksQuickFind?.layoutManager = LinearLayoutManager(activity as Context)
            val parkAdapter =
                ParkAdapter(
                    activity as Context,
                    R.layout.park_display,
                    resultParks
                )
            parkAdapter.vm = ViewModelProviders.of(this).get(ParksViewModel::class.java)
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
            itemTouchHelper.attachToRecyclerView(parksQuickFind)
            parksQuickFind?.adapter = parkAdapter
        }
    }

    override fun onStart() {
        viewModel.registerListener(
            this,
            this
        )
        super.onStart()
    }
    override fun onDestroy() {
        viewModel = ViewModelProviders.of(this).get(QuickFindResultViewModel::class.java)
        viewModel.unregisterListener()
        super.onDestroy()
    }

    override fun onDestroyView() {
        (activity as MainActivity).quickFind = false
        QuickFindAccelerometer.unregisterListener()
        super.onDestroyView()
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

    override fun goToPark(park: Park) {
        val gmmIntentUri: Uri = Uri.parse("google.navigation:q=${park.latitude},${park.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
}