package salabaev.gekkolog.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.withContext
import salabaev.gekkolog.R
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.reminder.Reminder
import java.io.File

class NotificationAdapter(
    private val notifications: List<Reminder>,
    private val geckos: Map<Int, Gecko>,
    private val onEditClick: (Reminder) -> Unit,
    private val onCompleteClick: (Reminder) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val type: TextView = itemView.findViewById(R.id.notification_type)
        val description: TextView = itemView.findViewById(R.id.notification_description)
        val geckoIcon: ImageView = itemView.findViewById(R.id.gecko_icon)
        val geckoName: TextView = itemView.findViewById(R.id.gecko_name)
        val editButton: View = itemView.findViewById(R.id.btn_edit)
        val completeButton: View = itemView.findViewById(R.id.btn_complete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        //TODO добавить изменение цвета при типе события
        val notification = notifications[position]

        holder.type.text = when (notification.type) {
            "FEED" -> "Кормление"
            "HEALTH" -> "Здоровье"
            else -> "Другое"
        }

        holder.description.text = notification.description
        holder.geckoName.text = geckos[notification.geckoId]?.name
        geckos[notification.geckoId]?.photoPath?.let { path: String ->
            Glide.with(holder.itemView.context)
                .load(File(path))
                .circleCrop()
                .into(holder.geckoIcon)
        }
        holder.editButton.setOnClickListener { onEditClick(notification) }
        holder.completeButton.setOnClickListener { onCompleteClick(notification) }
    }

    override fun getItemCount() = notifications.size
}
