package com.cascade.easy.data

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.cascade.easy.R

enum class MainContent(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    @IdRes val menuGroupId: Int
) {
    FEEDBACK_LIST(
        R.string.title_feedback_list,
        R.drawable.launcher_icon_feedback_list,
        R.id.groupFeedbackList
    ),
    SETTINGS(
        R.string.title_content_settings,
        R.drawable.launcher_icon_settings,
        R.id.groupSettings
    );
}