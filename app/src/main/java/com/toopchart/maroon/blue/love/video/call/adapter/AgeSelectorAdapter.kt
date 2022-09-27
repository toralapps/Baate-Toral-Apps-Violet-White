package com.toopchart.maroon.blue.love.video.call.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.toopchart.maroon.blue.love.video.call.databinding.AgeSelectoreLayoutBinding
import com.toopchart.maroon.blue.love.video.call.videolistmodel.AgeSelection


class AgeSelectorAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AgeSelection>() {

        override fun areItemsTheSame(oldItem: AgeSelection, newItem: AgeSelection): Boolean {
          return  oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AgeSelection, newItem: AgeSelection): Boolean {
            return  oldItem.isSelected == newItem.isSelected
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return AgeSelectorViewHolder(
            AgeSelectoreLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction,
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AgeSelectorViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<AgeSelection>) {
        differ.submitList(list)
        notifyDataSetChanged()
    }

    class AgeSelectorViewHolder(
        private val binding: AgeSelectoreLayoutBinding,
        private val interaction: Interaction?,
    ) : RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("ResourceAsColor")
        fun bind(item: AgeSelection) = with(itemView) {
            if(item.isSelected){
                binding.agetext.apply {
                    setBackgroundColor(Color.RED)
                    binding.container.apply {
                            this.setBackgroundColor(currentTextColor)
                    }
                }
            }else{
                binding.agetext.apply {
                    setBackgroundColor(Color.TRANSPARENT)
                    binding.container.apply {
                        this.setBackgroundColor(currentTextColor)
                    }
                }

            }

            binding.cardview.setOnClickListener {
                interaction?.onItemSelected(absoluteAdapterPosition,item)
            }

            binding.ageNumber = item.number

        }

    }



    interface Interaction {
        fun onItemSelected(position: Int, item: AgeSelection)
    }
}
