package com.cascade.easy.adapter.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

typealias DiffCallback<T> = (oldItem: T, newItem: T) -> Boolean

abstract class BaseAdapter<T, A: AdapterListener<T>>(
    protected val items: LiveData<List<T>>,
    isSame: DiffCallback<T> = { oldItem: T, newItem: T -> oldItem === newItem },
    isEqual: DiffCallback<T> = { oldItem: T, newItem: T -> oldItem == newItem },
    private val notifyDataSetChanged: Boolean = false
) : ListAdapter<T, BaseViewHolder<T>>(createDiffCallback(isSame, isEqual)),
    Observer<List<T>> {
    open var listener: A? = null

    override fun onBindViewHolder(holderBase: BaseViewHolder<T>, position: Int) = with(holderBase) {
        items.value?.get(position)?.let { item ->
            bind(item)
            itemView.setOnClickListener {
                listener?.onItemClick(item)
            }
            itemView.setOnLongClickListener {
                return@setOnLongClickListener listener?.onItemLongClick(item) ?: false
            }
        } ?: run {
            clear()
            itemView.setOnClickListener(null)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        items.observeForever(this)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        items.removeObserver(this)
    }

    override fun onChanged(list: List<T>?) {
        if (notifyDataSetChanged) {
            notifyDataSetChanged()
        } else {
            submitList(list)
        }
    }

    override fun getItemCount(): Int {
        return items.value?.size ?: 0
    }

    companion object {
        private fun <T> createDiffCallback(isSame: DiffCallback<T>, isEqual: DiffCallback<T>) =
            object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T) = isSame(oldItem, newItem)

                override fun areContentsTheSame(oldItem: T, newItem: T) = isEqual(oldItem, newItem)
            }
    }
}