package salabaev.gekkolog.ui.event

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.databinding.DropdownGeckoItemBinding
import java.io.File
import salabaev.gekkolog.R

class GeckoDropdownAdapter(
    context: Context,
    private val geckos: List<Gecko>,
    private val autoCompleteTextView: com.google.android.material.textfield.MaterialAutoCompleteTextView
) : ArrayAdapter<Gecko>(context, 0, geckos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val gecko = getItem(position)!!
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.dropdown_gecko_item, parent, false)

        val geckoName = view.findViewById<TextView>(R.id.geckoName)
        val geckoImage = view.findViewById<ImageView>(R.id.geckoImage)

        geckoName.text = gecko.name ?: "Без имени"

        if (gecko.photoPath != null) {
            Glide.with(context)
                .load(File(gecko.photoPath))
                .circleCrop()
                .placeholder(R.drawable.ic_gekko_colored)
                .into(geckoImage)
        } else {
            geckoImage.setImageResource(R.drawable.ic_gekko_colored)
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}