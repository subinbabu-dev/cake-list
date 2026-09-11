package com.subinbabu.cakelist.feature.cakelist.data.repository

import com.subinbabu.cakelist.feature.cakelist.data.remote.CakeApi
import com.subinbabu.cakelist.feature.cakelist.data.remote.toDomain
import com.subinbabu.cakelist.feature.cakelist.domain.model.Cake
import com.subinbabu.cakelist.feature.cakelist.domain.repository.CakeRepository

class CakeRepositoryImpl (
    private val api: CakeApi,
) : CakeRepository {

    override suspend fun getCakes(): List<Cake> =
        api.getCakes()
            .map { it.toDomain() }
}