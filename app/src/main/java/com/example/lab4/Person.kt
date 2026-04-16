package com.example.lab4

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize


@Parcelize
data class Person(
    val id: Int,
    val name: String,
    val age: Int,
    val profession: String,
    val bio: String,
    @DrawableRes val photoRes: Int,
    val isFavorite: Boolean = false
) : Parcelable


