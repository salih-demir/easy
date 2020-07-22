package com.cascade.easy.adapter.base

interface AdapterListener<T> {
    fun onItemClick(item: T) {}
    fun onItemLongClick(item: T) = false
}