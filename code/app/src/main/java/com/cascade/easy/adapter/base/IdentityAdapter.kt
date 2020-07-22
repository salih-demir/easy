package com.cascade.easy.adapter.base

import androidx.lifecycle.LiveData
import com.cascade.easy.model.network.base.Identity

abstract class IdentityAdapter<T : Identity, A: AdapterListener<T>>(
    items: LiveData<List<T>>,
    isSame: DiffCallback<T> = { oldItem: T, newItem: T -> oldItem.id == newItem.id },
    notifyDataSetChanged: Boolean = false
) : BaseAdapter<T, A>(items, isSame, notifyDataSetChanged = notifyDataSetChanged) {
    init {
        setHasStableIds(true)
    }

    final override fun setHasStableIds(hasStableIds: Boolean) {
        if (!hasStableIds) {
            throw IllegalArgumentException("Adapter must have stable IDs!")
        }

        super.setHasStableIds(hasStableIds)
    }

    override fun getItemId(position: Int): Long {
        return items.value?.get(position)?.id?.hashCode()?.toLong()
            ?: throw IllegalStateException("All items must have a stable ID!")
    }
}