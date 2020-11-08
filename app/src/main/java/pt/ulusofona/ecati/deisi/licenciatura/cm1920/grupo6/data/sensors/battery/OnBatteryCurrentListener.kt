package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo6.data.sensors.battery

interface OnBatteryCurrentListener {
    fun onCurrentChanged(current: Double)
}