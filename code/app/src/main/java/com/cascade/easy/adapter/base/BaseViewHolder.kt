package com.cascade.easy.adapter.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView),
    LayoutContainer {
    override val containerView: View = itemView

    abstract fun bind(data: T)
    abstract fun clear()
}