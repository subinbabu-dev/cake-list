package com.subinbabu.cakelist.feature.cakelist.data.remote

import com.subinbabu.cakelist.feature.cakelist.domain.model.Cake

data class CakeDto(
    val title: String,
    val desc: String,
    val image: String,
)

fun CakeDto.toDomain(): Cake =
    Cake(
        title = title,
        description = desc,
        imageUrl = image,
    )