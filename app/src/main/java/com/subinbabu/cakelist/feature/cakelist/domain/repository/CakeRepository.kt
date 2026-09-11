package com.subinbabu.cakelist.feature.cakelist.domain.repository

import com.subinbabu.cakelist.feature.cakelist.domain.model.Cake

interface CakeRepository {
    suspend fun getCakes(): List<Cake>
}