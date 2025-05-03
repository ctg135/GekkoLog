package salabaev.gekkolog.data.gecko

import androidx.recyclerview.widget.DiffUtil

class GeckoDiffCallback : DiffUtil.ItemCallback<Gecko>() {
    override fun areItemsTheSame(oldItem: Gecko, newItem: Gecko): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Gecko, newItem: Gecko): Boolean {
        return oldItem.name == newItem.name &&
            oldItem.gender == newItem.gender &&
            oldItem.morph == newItem.morph &&
            oldItem.feedPeriod == newItem.feedPeriod &&
            oldItem.photoPath == newItem.photoPath
    }
}