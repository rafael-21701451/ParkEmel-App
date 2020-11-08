package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.adapters

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.park_display.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.local.entities.Park
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.viewmodels.ParksViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.activities.MainActivity
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.utils.NavigationManager

class ParkAdapter(
    private val context: Context,
    private val layout: Int,
    private val items: MutableList<Park>
) : RecyclerView.Adapter<ParkAdapter.ParkViewHolder>() {

    var vm: ParksViewModel? = null
    var myActivity = context as MainActivity

    class ParkViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.park_name
        val distanceAndType: TextView? = view.distanceAndType
        val distance: TextView? = view.distance
        val occupation: TextView? = view.occupation
        val type: TextView? = view.type
        val capacity: TextView? = view.capacity
        var occupationProgressBar:ProgressBar = view.progress_bar
        val currentOccupation: TextView? = view.current_occupation
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkViewHolder {
        return ParkViewHolder(
            LayoutInflater.from(context).inflate(layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ParkViewHolder, position: Int) {
        var currentOccupation: Int =
            ((items[position].currentOccupation.toDouble() / items[position].maxCapacity.toDouble()) * 100).toInt()
        Log.i(TAG, currentOccupation.toString())
        if (vm?.checkOccupation(currentOccupation) == "Full") {
            holder.name.setTextColor(context.getColor(R.color.red))
        } else if (vm?.checkOccupation(currentOccupation) == "Potentially full") {
            holder.name.setTextColor(context.getColor(R.color.yellow))
        } else {
            holder.name.setTextColor(context.getColor(R.color.green))
        }

        holder.occupationProgressBar.setProgress(currentOccupation)
        holder.name.text = items[position].name
        if (items[position].distance == -1.0){
            holder.distance?.text =
                "${myActivity.getString(R.string.distance)} ${myActivity.getString(R.string.noDistance)}"
        }else{
            holder.distance?.text =
                "${myActivity.getString(R.string.distance)} ${items[position].distance} Km"
        }
        if (currentOccupation > 100){
            currentOccupation = 100
        }
        holder.occupation?.text = "$currentOccupation%"
        holder.currentOccupation?.text = "${myActivity.getString(R.string.occupation)} ${items[position].currentOccupation}"
        if (items[position].type.substring(0, 1) == "S") {
            holder.type?.text = "${myActivity.getString(R.string.type)} ${context.getString(
                R.string.surface
            )}"
            if (items[position].distance == -1.0){
                holder.distanceAndType?.text =
                    "${myActivity.getString(R.string.distance)} ${myActivity.getString(R.string.noDistance)} \t\t\t${myActivity.getString(
                        R.string.type
                    )} ${context.getString(
                        R.string.surface
                    )}"
            }else{
                holder.distanceAndType?.text =
                    "${myActivity.getString(R.string.distance)} ${items[position].distance} Km\t\t\t${myActivity.getString(
                        R.string.type
                    )} ${context.getString(
                        R.string.surface
                    )}"
            }
        } else {
            holder.type?.text = "${myActivity.getString(R.string.type)} ${context.getString(
                R.string.structure
            )}"
            if (items[position].distance == -1.0){
                holder.distanceAndType?.text =
                    "${myActivity.getString(R.string.distance)} ${myActivity.getString(R.string.noDistance)} \t\t\t${myActivity.getString(
                        R.string.type
                    )} ${context.getString(
                        R.string.structure
                    )}"
            }else{
                holder.distanceAndType?.text =
                    "${myActivity.getString(R.string.distance)} ${items[position].distance} Km\t\t\t${myActivity.getString(
                        R.string.type
                    )} ${context.getString(
                        R.string.structure
                    )}"
            }
        }
        holder.capacity?.text =
            "${myActivity.getString(R.string.capacity)} ${items[position].maxCapacity}"

        holder.itemView.setOnClickListener {
            NavigationManager.goToParkDetailsFragment(
                myActivity.supportFragmentManager,
                items[position].name
            )
        }

    }

    fun goToPark(pos: Int) {
        vm?.getParkInPos(pos)
    }

    override fun getItemCount() = items.size
}