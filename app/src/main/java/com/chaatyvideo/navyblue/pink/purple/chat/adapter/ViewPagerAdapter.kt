package com.chaatyvideo.navyblue.pink.purple.chat.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.chaatyvideo.navyblue.pink.purple.chat.R
import com.chaatyvideo.navyblue.pink.purple.chat.videolistmodel.Data
import com.google.android.material.chip.Chip

class ViewPagerAdapter(private val interaction: Interaction? = null) :
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

        return ViewPagerHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.viewpager_list,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewPagerHolder -> {
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

    class ViewPagerHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        val chip1 = itemView.findViewById<Chip>(R.id.chip1)
        val chip2 = itemView.findViewById<Chip>(R.id.chip2)
        val chip3 = itemView.findViewById<Chip>(R.id.chip3)



       private val userImage = itemView.findViewById<ImageView>(R.id.userImage)

        fun bind(item: Data) = with(itemView) {

            chip1.text = getRandomeIntrest()
            chip2.text = getRandomeIntrest()
            chip3.text = getRandomeIntrest()

            Glide.with(userImage).load(item.ThumbnailUrl).placeholder(R.drawable.ic_person_pin)
                .error(R.drawable.ic_person_pin).into(userImage)

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

        }

        fun getRandomeIntrest(): String {
            val instrestlist = listOf<String>("Basketball",
            "Football",
            "Volleyball",
            "Marathon",
            "running",
            "Skiing",
            "Tennis",
            "Cycling",
            "Swimming"
            )

            val random = (0..instrestlist.size-1).random()
            return instrestlist[random]
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Data)
    }
}
