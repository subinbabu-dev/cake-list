package com.subinbabu.cakelist.feature.cakelist.testutil

import com.subinbabu.cakelist.feature.cakelist.domain.model.Cake
import com.subinbabu.cakelist.feature.cakelist.domain.repository.CakeRepository

class FakeCakeRepository(
    private val cakes: List<Cake>,
) : CakeRepository {
    override suspend fun getCakes(): List<Cake> = cakes
}