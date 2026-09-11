package com.subinbabu.cakelist.feature.cakelist.domain.usecase

import com.subinbabu.cakelist.feature.cakelist.domain.model.Cake
import com.subinbabu.cakelist.feature.cakelist.domain.repository.CakeRepository
import jakarta.inject.Inject
import java.util.Locale

class GetCakesUseCase @Inject constructor(
    private val repository: CakeRepository,
) {

    suspend operator fun invoke(): List<Cake> =
        repository
            .getCakes()
            .distinctBy { cake ->
                DuplicateKey(
                    title = cake.title.trim().lowercase(Locale.ROOT),
                    description = cake.description.trim().lowercase(Locale.ROOT),
                    imageUrl = cake.imageUrl.trim(),
                )
            }
            .sortedBy { cake ->
                cake.title.trim().lowercase(Locale.ROOT)
            }

    private data class DuplicateKey(
        val title: String,
        val description: String,
        val imageUrl: String
    )
}