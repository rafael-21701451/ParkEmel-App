package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.vehicle_display.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Vehicle
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.activities.MainActivity
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils.NavigationManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.VehiclesViewModel

class VehicleAdapter(private val context: Context, private val layout: Int, private val items: MutableList<Vehicle>) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    var vm: VehiclesViewModel? = null
    var myActivity = context as MainActivity

    class VehicleViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val licensePlate: TextView? = view.licensePlate
        val licence: TextView? = view.licenceO
        val date: TextView? = view.date
        val car: TextView = view.car
        val delete: ImageButton? = view.delete
        val message: ImageButton? = view.message

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        return VehicleViewHolder(
            LayoutInflater.from(context).inflate(layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.licensePlate?.text = "${items[position].licensePlate} ${items[position].date}"
        holder.car.text = "${items[position].brand} ${items[position].model}"
        holder.licence?.text = items[position].licensePlate
        holder.date?.text = items[position].date

        holder.itemView.setOnClickListener{
            NavigationManager.goToEditVehicleFragment(myActivity.supportFragmentManager, items[position].uuid)
        }

        holder.delete?.setOnClickListener {
            vm!!.onDeleteClick(items[position].uuid)
        }
        holder.message?.setOnClickListener {
            var message = "${context.getString(R.string.messageP1)} ${items[position].licensePlate}"
            vm!!.goToMessage(message)
        }
    }

    override fun getItemCount() = items.size
}