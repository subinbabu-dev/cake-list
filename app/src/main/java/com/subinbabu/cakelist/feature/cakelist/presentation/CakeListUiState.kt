package com.subinbabu.cakelist.feature.cakelist.presentation

import com.subinbabu.cakelist.feature.cakelist.domain.model.Cake

sealed class CakeListUiState {

    data object Loading : CakeListUiState()

    data class Content(
        val cakes: List<Cake>,
        val isRefreshing: Boolean = false,
    ) : CakeListUiState()

    data class Error(
        val message: String,
    ) : CakeListUiState()
}