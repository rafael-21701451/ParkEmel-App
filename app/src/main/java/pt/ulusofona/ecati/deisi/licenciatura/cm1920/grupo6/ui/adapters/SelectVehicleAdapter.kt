package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.vehicle_display.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Vehicle
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.activities.MainActivity
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.VehiclesViewModel

class SelectVehicleAdapter(private val context: Context, private val layout: Int, private val items: MutableList<Vehicle>) : RecyclerView.Adapter<SelectVehicleAdapter.VehicleViewHolder>() {

    var vm: VehiclesViewModel? = null
    var myActivity = context as MainActivity

    class VehicleViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val licensePlate: TextView? = view.licensePlate
        val licence: TextView? = view.licenceO
        val date: TextView? = view.date
        val car: TextView = view.car

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
            var message = "${context.getString(R.string.messageP1)} ${items[position].licensePlate}"
            vm!!.goToSelectedVehicleMessage(message)
        }

    }

    override fun getItemCount() = items.size
}