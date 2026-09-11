package com.subinbabu.cakelist.feature.cakelist.data.repository

import com.subinbabu.cakelist.feature.cakelist.data.remote.CakeApi
import com.subinbabu.cakelist.feature.cakelist.data.remote.toDomain
import com.subinbabu.cakelist.feature.cakelist.domain.model.Cake
import com.subinbabu.cakelist.feature.cakelist.domain.repository.CakeRepository
import jakarta.inject.Inject

class CakeRepositoryImpl @Inject constructor(
    private val cakeApi: CakeApi,
) : CakeRepository {

    override suspend fun getCakes(): List<Cake> =
        cakeApi
            .getCakes()
            .map { cakeDto -> cakeDto.toDomain() }
}