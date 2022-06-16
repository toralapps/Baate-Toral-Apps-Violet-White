package com.baatechat.blackwhite.swip.call.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.baatechat.blackwhite.swip.call.R
import com.baatechat.blackwhite.swip.call.videolistmodel.Falgs

class FlagsAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Falgs>() {

        override fun areItemsTheSame(oldItem: Falgs, newItem: Falgs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Falgs, newItem: Falgs): Boolean {
           return  oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return FlagVideHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_flag_list,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FlagVideHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Falgs>) {
        differ.submitList(list)
    }

    class FlagVideHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        val flagIcon = itemView.findViewById<ImageView>(R.id.user)
        val categorytext = itemView.findViewById<TextView>(R.id.agetext)

        fun bind(item: Falgs) = with(itemView) {

            flagIcon.setImageResource(item.flag)
            categorytext.text = item.category

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

        }





//        fun likecounter() {
//            flagLives.text = ("${(500..999).random()}live")
//        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Falgs)
    }
}
