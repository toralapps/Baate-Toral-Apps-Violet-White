package com.footballchat.orangewhite.videochat.call.adapter

import android.animation.Animator
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.airbnb.lottie.LottieAnimationView
import com.footballchat.orangewhite.videochat.call.R
import com.footballchat.orangewhite.videochat.call.videolistmodel.Falgs

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
        val categorytext = itemView.findViewById<TextView>(R.id.categorytxt)
        val cardview = itemView.findViewById<CardView>(R.id.cardview)
        val nextbtn = itemView.findViewById<ImageView>(R.id.nextbtn)
        val likeBtn = itemView.findViewById<ImageView>(R.id.likebtn)
        val likeAnimation = itemView.findViewById<LottieAnimationView>(R.id.likeanimation)
        val reportBtn = itemView.findViewById<ImageView>(R.id.reportbtn)

        fun bind(item: Falgs) = with(itemView) {

            flagIcon.setImageResource(item.flag)
            categorytext.text = item.category

            cardview.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            likeBtn.setOnClickListener {
                likeAnimation.visibility = View.VISIBLE
                likeAnimation.playAnimation()
            }

            likeAnimation.addAnimatorListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(p0: Animator) {
                    likeBtn.visibility = View.GONE
                }

                override fun onAnimationEnd(p0: Animator) {
                    likeBtn.visibility = View.VISIBLE
                    likeAnimation.visibility = View.GONE
                }

                override fun onAnimationCancel(p0: Animator) {

                }

                override fun onAnimationRepeat(p0: Animator) {

                }

            });

            nextbtn.setOnClickListener {
                interaction?.closeBtnClick(absoluteAdapterPosition,item)
            }

            reportBtn.setOnClickListener {
                interaction?.reportBtnClick()
            }
        }





//        fun likecounter() {
//            flagLives.text = ("${(500..999).random()}live")
//        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Falgs)
        fun closeBtnClick(position:Int,item: Falgs)
        fun onNextClick(position: Int,item: Falgs)
        fun reportBtnClick()
    }
}
