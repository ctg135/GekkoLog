package salabaev.gekkolog.ui.pets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import salabaev.gekkolog.R
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.gecko.GeckoDiffCallback
import salabaev.gekkolog.databinding.ItemGeckoBinding
import java.io.File



class PetListAdapter : ListAdapter<Gecko, PetListAdapter.GeckoViewHolder>(GeckoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeckoViewHolder {
        val binding = ItemGeckoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GeckoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GeckoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GeckoViewHolder(private val binding: ItemGeckoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(gecko: Gecko) {
            binding.apply {
                // Установка изображения (Glide/Coil)
                gecko.photoPath?.let { path ->
                    Glide.with(root.context)
                        .load(File(path))
                        .into(geckoImage)
                } ?: geckoImage.setImageResource(R.drawable.ic_gekko_colored)

                geckoName.text = gecko.name ?: "Без имени"
                geckoGender.text = when(gecko.gender) {
                    "M" -> "♂"
                    "F" -> "♀"
                    else -> ""
                }
                geckoMorph.text = gecko.morph ?: "Морфа не указана"
            }
        }
    }
}

