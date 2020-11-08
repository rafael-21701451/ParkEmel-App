package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.ui.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.park_map_details.view.*


class InfoWindowAdapter(var context: Context, var layout: Int): GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(marker: Marker?): View? {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(layout, null)
        view.parkName.text = marker?.title
        view.details.text = marker?.snippet
        return view
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }
}