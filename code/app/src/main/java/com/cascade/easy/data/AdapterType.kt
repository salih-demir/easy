package com.cascade.easy.data

import androidx.annotation.DrawableRes
import com.cascade.easy.R

enum class AdapterType(@DrawableRes val imageResId: Int) {
    GRID(R.drawable.ic_grid),
    LIST(R.drawable.ic_list),
}