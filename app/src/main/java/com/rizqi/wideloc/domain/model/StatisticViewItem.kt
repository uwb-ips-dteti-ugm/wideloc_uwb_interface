package com.rizqi.wideloc.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class StatisticViewItem(
    @StringRes
    val nameResId: Int,
    @DrawableRes
    val iconResId: Int,
    @StringRes
    val unitResId: Int,
    val data: StatisticData,
)
