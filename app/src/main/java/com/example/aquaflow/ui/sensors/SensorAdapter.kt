package com.example.aquaflow.ui.sensors

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.aquaflow.R
import com.example.aquaflow.model.Sensor
import com.example.aquaflow.model.SensorStatus

class SensorsAdapter : RecyclerView.Adapter<SensorsAdapter.SensorViewHolder>() {

    private val items = mutableListOf<Sensor>()

    fun submitList(list: List<Sensor>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sensor, parent, false)
        return SensorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvName: TextView = itemView.findViewById(R.id.tvSensorName)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvSensorLocation)
        private val tvVolume: TextView = itemView.findViewById(R.id.tvSensorVolume)
        private val ivStatus: ImageView = itemView.findViewById(R.id.ivSensorStatus)
        private val tvLastUpdate: TextView = itemView.findViewById(R.id.tvSensorLastUpdate)

        fun bind(sensor: Sensor) {
            val ctx = itemView.context

            tvName.text = sensor.name
            tvLocation.text = sensor.location
            tvVolume.text = "Volume : ${sensor.volumeLiters}L"
            tvLastUpdate.text = "Mise à jour : ${sensor.lastUpdate}"

            when (sensor.status) {
                SensorStatus.OK -> {
                    ivStatus.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.status_good)
                    )
                }
                SensorStatus.WARNING -> {
                    ivStatus.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.status_warning)
                    )
                }
                SensorStatus.ERROR -> {
                    ivStatus.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.status_critical)
                    )
                }

                SensorStatus.INACTIF -> {
                    ivStatus.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.gray_light)
                    )
                }
            }
        }
    }
}