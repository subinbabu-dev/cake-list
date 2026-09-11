package com.subinbabu.cakelist.feature.cakelist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subinbabu.cakelist.feature.cakelist.domain.usecase.GetCakesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException


@HiltViewModel
class CakeListViewModel @Inject constructor(
    private val getCakesUseCase: GetCakesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CakeListUiState>(CakeListUiState.Loading)
    val uiState: StateFlow<CakeListUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    init {
        loadCakes()
    }

    private fun loadCakes(
        isRefresh: Boolean = false,
    ) {
        if (isRefresh) {
            val currentContent = _uiState.value as? CakeListUiState.Content ?: return
            _uiState.value = currentContent.copy(isRefreshing = true)
        } else {
            _uiState.value = CakeListUiState.Loading
        }

        viewModelScope.launch {
            try {
                val cakes = getCakesUseCase()
                _uiState.value = CakeListUiState.Content(cakes = cakes)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                val previousContent = _uiState.value as? CakeListUiState.Content
                if (isRefresh && previousContent != null) {
                    _uiState.value = previousContent.copy(isRefreshing = false)
                    _snackbarMessage.emit(REFRESH_ERROR_MESSAGE)
                } else {
                    _uiState.value = CakeListUiState.Error(message = LOAD_ERROR_MESSAGE)
                }
            }
        }
    }

    fun refresh() {
        val currentState = _uiState.value
        if (currentState !is CakeListUiState.Content || currentState.isRefreshing) return
        loadCakes(isRefresh = true)
    }

    fun retry() {
        if (_uiState.value is CakeListUiState.Error) loadCakes()
    }

    private companion object {
        const val LOAD_ERROR_MESSAGE = "Unable to load cakes. Please try again."
        const val REFRESH_ERROR_MESSAGE = "Unable to refresh cakes. Please try again."
    }
}