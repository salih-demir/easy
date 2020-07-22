package com.cascade.easy.adapter

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import com.cascade.easy.R
import com.cascade.easy.adapter.base.AdapterListener
import com.cascade.easy.adapter.base.BaseViewHolder
import com.cascade.easy.adapter.base.IdentityAdapter
import com.cascade.easy.model.network.response.feedback.Feedback
import com.cascade.easy.util.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_feedback.view.*

class FeedbackAdapter(items: LiveData<List<Feedback>>) :
    IdentityAdapter<Feedback, FeedbackListener>(items, notifyDataSetChanged = true) {

    override var listener: FeedbackListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FeedbackViewHolder(parent.inflate(R.layout.item_feedback), listener)
}

class FeedbackViewHolder(itemView: View, private val listener: FeedbackListener?) :
    BaseViewHolder<Feedback>(itemView),
    LayoutContainer {
    override fun bind(data: Feedback) = with(itemView) {
        textViewComment.text = data.comment?.trim()
        textViewComment.setOnClickListener {
            listener?.onCommentClicked(data)
        }

        data.labels?.joinToString(separator = ", ")?.let {
            textViewTags.text = it
        } ?: run {
            textViewTags.text = null
        }

        imageButtonInfo.setOnClickListener {
            listener?.onInfoRequested(data)
        }

        data.geolocation?.let {
            imageButtonLocation.visibility = View.VISIBLE
            imageButtonLocation.setOnClickListener {
                listener?.onLocationRequested(data)
            }
        } ?: run {
            imageButtonLocation.visibility = View.GONE
        }

        data.rating?.takeIf { it in 0..5 }?.let {
            ratingBar.alpha = 1f
            ratingBar.progress = it
        } ?: run {
            ratingBar.alpha = 0.5f
        }
    }

    override fun clear() = with(itemView) {
        textViewComment.text = null
        textViewComment.setOnClickListener(null)
        textViewTags.text = null

        imageButtonInfo.setOnClickListener(null)
        imageButtonLocation.visibility = View.VISIBLE
        imageButtonLocation.setOnClickListener(null)

        ratingBar.progress = 0
        ratingBar.alpha = 1f
    }
}

interface FeedbackListener : AdapterListener<Feedback> {
    fun onCommentClicked(feedback: Feedback)
    fun onLocationRequested(feedback: Feedback)
    fun onInfoRequested(feedback: Feedback)
}