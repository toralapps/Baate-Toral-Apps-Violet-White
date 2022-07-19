package com.lovechat.red.pink.girl.dating.call.adapter

import android.animation.Animator
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.lovechat.red.pink.girl.dating.call.R
import com.lovechat.red.pink.girl.dating.call.videolistmodel.Data

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
                R.layout.layout_girls_list,
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
        val likebtn = itemView.findViewById<ImageView>(R.id.likebtn)
        val likeAnimation = itemView.findViewById<LottieAnimationView>(R.id.like_animation)
        val name = itemView.findViewById<TextView>(R.id.username)
        val distance = itemView.findViewById<TextView>(R.id.disctance)

        fun bind(item: Data) = with(itemView) {
            Glide.with(thumbnail).load(item.ThumbnailUrl).placeholder(R.drawable.ic_person_pin)
                .error(R.drawable.ic_person_pin).into(thumbnail)

            name.text = item.FirstName ?: "Milla"
            randomeaddress()

            likebtn.setOnClickListener {
                likeAnimation.visibility = View.VISIBLE
                likeAnimation.playAnimation()
            }

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            likeAnimation.addAnimatorListener(object :Animator.AnimatorListener{
                override fun onAnimationStart(p0: Animator?) {
                    likebtn.visibility = View.GONE
                }

                override fun onAnimationEnd(p0: Animator?) {
                    likebtn.visibility = View.VISIBLE
                    likebtn.isSelected = !likebtn.isSelected
                    likeAnimation.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationRepeat(p0: Animator?) {

                }

            });
        }

        fun randomeaddress(){
            distance.text = "${(0..15).random()} Km"
        }

    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Data)
    }
}
