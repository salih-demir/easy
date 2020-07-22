package com.cascade.easy.data

import androidx.annotation.StringRes
import com.cascade.easy.R

enum class SortType(@StringRes val labelResId: Int) {
    NONE(R.string.label_sort_by_default),
    COMMENT_LENGTH(R.string.label_sort_by_comment_length),
    RATING(R.string.label_sort_by_rating)
}