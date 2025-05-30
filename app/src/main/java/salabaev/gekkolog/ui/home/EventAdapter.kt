package salabaev.gekkolog.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import salabaev.gekkolog.R
import salabaev.gekkolog.data.event.Event
import salabaev.gekkolog.data.gecko.Gecko
import java.io.File

class EventAdapter(
    private val events: List<Event>,
    private val geckos: Map<Int, Gecko>,
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val type: TextView = itemView.findViewById(R.id.event_type)
        val details: TextView = itemView.findViewById(R.id.event_details)
        val geckoIcon: ImageView = itemView.findViewById(R.id.gecko_icon)
        val geckoName: TextView = itemView.findViewById(R.id.gecko_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        holder.type.text = when (event.type) {
            "FEED" -> "Кормление"
            "SHED" -> "Линька"
            "WEIGHT" -> "Взвешивание"
            "HEALTH" -> "Здоровье"
            "OTHER" -> "Другое"
            else -> "Unknown"
        }

        holder.details.text = when (event.type) {
            "FEED" -> {
                val feedType = when (event.feedType) {
                    "CA" -> " Кальций"
                    "VIT" -> " Витамины"
                    else -> ""
                }
                val refused = if (event.feedSucces != false) " (Отказался)" else ""
                "${event.description}$feedType$refused"
            }
            "SHED" -> {
                if (event.shedSuccess == true) "Успешно" else "Была нужна помощь"
            }
            "WEIGHT" -> {
                "${event.weight} г."
            }
            else -> event.description
        }
        holder.geckoName.text = geckos[event.geckoId]?.name
        geckos[event.geckoId]?.photoPath?.let { path: String ->
            Glide.with(holder.itemView.context)
                .load(File(path))
                .circleCrop()
                .into(holder.geckoIcon)
        }

        holder.itemView.setOnClickListener { onItemClick(event) }
    }

    override fun getItemCount() = events.size
}
