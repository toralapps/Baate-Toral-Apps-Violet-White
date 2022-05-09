package com.chaatyvideo.navyblue.pink.purple.chat.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.chaatyvideo.navyblue.pink.purple.chat.R
import com.chaatyvideo.navyblue.pink.purple.chat.videolistmodel.Falgs

class FlagsAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Falgs>() {

        override fun areItemsTheSame(oldItem: Falgs, newItem: Falgs): Boolean {
            TODO("not implemented")
        }

        override fun areContentsTheSame(oldItem: Falgs, newItem: Falgs): Boolean {
            TODO("not implemented")
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

        val flagIcon = itemView.findViewById<ImageView>(R.id.flagicon)
        val flagName = itemView.findViewById<TextView>(R.id.flagname)
        val flagLives = itemView.findViewById<TextView>(R.id.livestxt)

        fun bind(item: Falgs) = with(itemView) {

            flagIcon.setImageResource(item.flag)
            flagName.text = item.name
            likecounter()
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

        }


        fun likecounter() {
            flagLives.text = ("${(500..999).random()}live")
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Falgs)
    }
}
