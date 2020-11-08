package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.fragment_contacts.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Vehicle
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.listeners.CheckVehicles
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils.NavigationManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.ContactsViewModel

class ContactsFragment : Fragment(), CheckVehicles{

    lateinit var vm: ContactsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.contacts)
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        vm = ViewModelProviders.of(this).get(ContactsViewModel::class.java)
        view.details_SS.text = (Html.fromHtml(getString(R.string.ss_details)))
        ButterKnife.bind(this,view)
        return view
    }

    override fun onStart() {
        vm.registerCheckVehiclesListener(this)
        super.onStart()
    }

    override fun onDestroy() {
        vm = ViewModelProviders.of(this).get(ContactsViewModel::class.java)
        vm.unregisterListener()
        super.onDestroy()
    }

    @OnClick(
        R.id.expand_button_SS,
        R.id.button_SS
    )
    fun clickSS(view: View){
        if (expand_button_SS.contentDescription == "collapse"){
            expand_button_SS.contentDescription = "expand"
            expand_button_SS.setImageResource(R.drawable.ic_expand)
            details_SS.text = ""
        } else {
            expand_button_SS.contentDescription = "collapse"
            expand_button_SS.setImageResource(R.drawable.ic_collapse)
            details_SS.text = (Html.fromHtml(getString(R.string.ss_details)))
        }
    }

    @OnClick(
        R.id.expand_button_GC,
        R.id.button_GC
    )
    fun clickGC(view: View){
        if (expand_button_GC.contentDescription == "collapse"){
            expand_button_GC.contentDescription = "expand"
            expand_button_GC.setImageResource(R.drawable.ic_expand)
            details_GC.text = ""
        } else {
            expand_button_GC.contentDescription = "collapse"
            expand_button_GC.setImageResource(R.drawable.ic_collapse)
            details_GC.text = (Html.fromHtml(getString(R.string.gc_details)))
        }
    }

    @OnClick(R.id.expand_button_BV, R.id.button_BV)
    fun clickBV(view: View){
        if (expand_button_BV.contentDescription == "collapse"){
            expand_button_BV.contentDescription = "expand"
            expand_button_BV.setImageResource(R.drawable.ic_expand)
            details_BV.text = ""
        } else {
            expand_button_BV.contentDescription = "collapse"
            expand_button_BV.setImageResource(R.drawable.ic_collapse)
            details_BV.text = (Html.fromHtml(getString(R.string.bv_details)))
        }
    }

    @OnClick(R.id.button_CBV, R.id.message_button_CBV)
    fun clickCBV(view: View){
        vm.checkVehicles()
    }

    override fun userHasVehicles(vehicles: MutableList<Vehicle>) {
        if(vehicles.size == 0){
            goToMessage(getString(R.string.messageP2))

        }else{
            fragmentManager?.let { NavigationManager.goToSelectVehicleFragment(it) }
        }
    }

    override fun goToMessage(message: String) {
        val uri = Uri.parse("smsto:3838")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", message)
        startActivity(intent)
    }
}