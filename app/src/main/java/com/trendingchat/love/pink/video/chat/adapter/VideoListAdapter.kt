package com.trendingchat.love.pink.video.chat.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.trendingchat.love.pink.video.chat.R
import com.trendingchat.love.pink.video.chat.videolistmodel.Data

class VideoListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Data>() {

        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return  oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.VideoId == newItem.VideoId
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return VideoListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.chat_room_list_layout,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoListViewHolder -> {
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

    class VideoListViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        val thumbnail = itemView.findViewById<ImageView>(R.id.userdp)
        val name = itemView.findViewById<TextView>(R.id.username)
        val country = itemView.findViewById<TextView>(R.id.country)
        val likes = itemView.findViewById<TextView>(R.id.likecounter)

        fun bind(item: Data) = with(itemView) {
            likecounter()
            Glide.with(thumbnail).load(item.ThumbnailUrl).placeholder(R.drawable.ic_person_pin)
                .error(R.drawable.ic_person_pin).into(thumbnail)

            name.text = item.FirstName

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

        }

        fun likecounter() {
            likes.text = ("${(5..99).random()}.${(5..99).random()}K")
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Data)
    }
}
