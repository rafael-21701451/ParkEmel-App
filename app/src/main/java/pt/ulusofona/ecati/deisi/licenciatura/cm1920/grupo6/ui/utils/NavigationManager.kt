package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments.*

class NavigationManager {

    companion object {
        private fun placeFragment(fm: FragmentManager, fragment: Fragment) {
            val transition = fm.beginTransaction()
            transition.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            );
            transition.replace(R.id.frame, fragment)
            transition.addToBackStack(null)
            transition.commit()
        }

        private fun reloadFragment(fm: FragmentManager, fragment: Fragment) {
            val transition = fm.beginTransaction()
            transition.replace(R.id.frame, fragment)
            transition.commit()
        }

        fun goToParksFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                ParksFragment()
            )
        }

        fun reloadParksFragment(fm: FragmentManager) {
            reloadFragment(
                fm,
                ParksFragment()
            )
        }

        fun reloadMapFragment(fm: FragmentManager) {
            reloadFragment(
                fm,
                MapFragment()
            )
        }

        fun goToParkDetailsFragment(fm: FragmentManager, name: String) {
            placeFragment(
                fm,
                ParkDetailsFragment.newInstance(
                    name
                )
            )
        }

        fun goToQuickFindFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                QuickFindFragment()
            )
        }

        fun goToQuickFindResultFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                QuickFindResultFragment()
            )
        }

        fun goToTripPlannerFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                TripPlannerFragment()
            )
        }

        fun goToTripResultFragment(fm: FragmentManager){
            placeFragment(
                fm,
                TripResultFragment()
            )
        }

        fun goToVehiclesFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                VehiclesFragment()
            )
        }

        fun goToSelectVehicleFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                SelectVehicleFragment()
            )
        }

        fun goToAddVehicleFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                AddVehicleFragment()
            )
        }

        fun goToEditVehicleFragment(fm: FragmentManager, uuid: String) {
            placeFragment(
                fm,
                EditVehicleFragment.newInstance(uuid)
            )
        }

        fun goToMapFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                MapFragment()
            )
        }

        fun goToContactsFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                ContactsFragment()
            )
        }

        fun goToSettingsFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                SettingsFragment()
            )
        }
    }
}