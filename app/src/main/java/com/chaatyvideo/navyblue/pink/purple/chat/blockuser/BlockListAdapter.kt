package com.chaatyvideo.navyblue.pink.purple.chat.blockuser

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.chaatyvideo.navyblue.pink.purple.chat.R
import com.chaatyvideo.navyblue.pink.purple.chat.videolistmodel.Data


class BlockListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Data>() {

        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
           return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.VideoId == newItem.VideoId
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return BlockViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.block_list_layout,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BlockViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Data>) {
        differ.submitList(list)
    }

    class BlockViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        val unblockbtn:Button = itemView.findViewById(R.id.unblockbtn)
        val username:TextView = itemView.findViewById(R.id.username)
        val userdp:ImageView = itemView.findViewById(R.id.userdp)

        fun bind(item: Data) = with(itemView) {

            Glide.with(userdp).load(item.ThumbnailUrl).into(userdp)
            username.text = item.FirstName ?: "Name not Avaliable"


            unblockbtn.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }


        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Data)
    }
}
