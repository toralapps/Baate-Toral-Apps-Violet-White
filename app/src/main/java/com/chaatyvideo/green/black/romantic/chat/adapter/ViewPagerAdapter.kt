package com.chaatyvideo.green.black.romantic.chat.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.chaatyvideo.green.black.romantic.chat.R
import com.chaatyvideo.green.black.romantic.chat.videolistmodel.Data
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
                R.layout.new_viewpager_list,
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




       private val userImage = itemView.findViewById<ImageView>(R.id.imageView)
        private val likes = itemView.findViewById<TextView>(R.id.likescounter)
        private val user_name = itemView.findViewById<TextView>(R.id.user_name)
        private val city = itemView.findViewById<TextView>(R.id.cityname)
        private val age = itemView.findViewById<TextView>(R.id.agetext)
        private val blockbutton = itemView.findViewById<ImageView>(R.id.blockBtn)
        private val reportBtn = itemView.findViewById<ImageView>(R.id.report)

        fun bind(item: Data) = with(itemView) {

            city.text = getRandomeIntrest()
            likecounter()
            user_name.text = item.FirstName ?: "Urvashi"
            Glide.with(userImage).load(item.ThumbnailUrl).placeholder(R.drawable.ic_person_pin)
                .error(R.drawable.ic_person_pin).into(userImage)

            blockbutton.setOnClickListener {
                interaction?.onBlockUser(adapterPosition, item)
            }

            reportBtn.setOnClickListener {
                interaction?.reportUser()
            }

        }

        fun getRandomeIntrest(): String {
            val instrestlist = listOf<String>(" Vashi,Navi Mumbai",
            ", Ashok Nagar Road,Mumbai",
            "Heera Panna,Mumbai",
            "Near Saini ,Delhi",
            "Ballard Estate,Mumbai",
            "Palme Marg,Delhi",
            " Tawade Marg,Mumbai",
            " Rampura,Delhi"
            )

            val random = (0..instrestlist.size-1).random()
            return instrestlist[random]
        }


        fun likecounter() {
            likes.text = ("${(500..999).random()}K")
        }

    }

    interface Interaction {
        fun onBlockUser(position: Int, item: Data)
        fun reportUser()
    }
}
