package com.race.game.domain.race

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.race.game.R
import com.race.game.databinding.ItemRaceBinding

class RaceAdapter(private val onItemClick: (position: Int, item: RaceItem) -> Unit) : RecyclerView.Adapter<RaceViewHolder>() {
    var items = mutableListOf<RaceItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        return RaceViewHolder(
            ItemRaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), parent.context
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        holder.bind(items[position])
        holder.onItemClick = onItemClick
    }

}

class RaceViewHolder(private val binding: ItemRaceBinding, private val context: Context) :
    RecyclerView.ViewHolder(binding.root) {
    var onItemClick: ((position: Int, item: RaceItem) -> Unit)? = null

    fun bind(item: RaceItem) {
        val img = when (item.number) {
            1 -> R.drawable.air01
            2 -> R.drawable.air02
            3 -> R.drawable.air03
            4 -> R.drawable.air04
            5 -> R.drawable.air05
            6 -> R.drawable.air06
            7 -> R.drawable.air07
            8 -> R.drawable.air08
            9 -> R.drawable.air09
            10 -> R.drawable.air10
            11 -> R.drawable.air11
            12 -> R.drawable.air12
            13 -> R.drawable.air13
            14 -> R.drawable.air14
            15 -> R.drawable.air15
            else -> null
        }

        if (img != null) {
            binding.plane.setImageResource(img)
            binding.number.text = item.number.toString()
            binding.number.isVisible = true
        } else {
            binding.plane.setImageDrawable(null)
            binding.number.isVisible = false
        }

        if (item.isSelected) {
            binding.box.setColorFilter(
            ResourcesCompat.getColor(
                context.resources,
                R.color.white,
                null
            ))
        } else {
            binding.box.setColorFilter(
                ResourcesCompat.getColor(
                    context.resources,
                    android.R.color.transparent,
                    null
                ))
        }

        if (item.isPresent) {
            binding.plane.setImageResource(R.drawable.gift)
            binding.box.isVisible = false
            binding.number.isVisible = false
        } else {
            binding.box.isVisible = true
        }

        binding.root.setOnClickListener {
            onItemClick?.invoke(adapterPosition, item)
        }
    }
}
