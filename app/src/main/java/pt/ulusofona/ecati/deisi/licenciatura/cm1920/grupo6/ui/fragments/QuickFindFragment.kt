package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Context
import android.hardware.SensorEvent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.accelerometer.OnPhoneShake
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.accelerometer.QuickFindAccelerometer
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.activities.MainActivity
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils.NavigationManager
import kotlin.math.abs

class QuickFindFragment : Fragment(), OnPhoneShake {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = getString(R.string.quick_find)
        (activity as MainActivity).quickFind = true
        return inflater.inflate(R.layout.fragment_quick_find, container, false)
    }

    override fun onStart() {
        QuickFindAccelerometer.start(activity as Context)
        QuickFindAccelerometer.registerListener(this)
        super.onStart()
    }

    override fun onDestroyView() {
        (activity as MainActivity).quickFind = false
        QuickFindAccelerometer.unregisterListener()
        super.onDestroyView()
    }

    override fun onPhoneShake(event: SensorEvent?) {
        if (event?.values?.get(0)?.let { abs(it) }!! >= 25) {
            fragmentManager?.let { NavigationManager.goToQuickFindResultFragment(it) }

        }
    }
}