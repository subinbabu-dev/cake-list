package com.subinbabu.cakelist.feature.cakelist.presentation

import com.subinbabu.cakelist.feature.cakelist.domain.model.Cake
import com.subinbabu.cakelist.feature.cakelist.domain.repository.CakeRepository
import com.subinbabu.cakelist.feature.cakelist.domain.usecase.GetCakesUseCase
import com.subinbabu.cakelist.feature.cakelist.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CakeListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `startup success shows processed content`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repository = FakeCakeRepository(
                responses = mutableListOf(
                    Result.success(
                        listOf(
                            cake("Victoria Sponge"),
                            cake("Chocolate Cake"),
                            cake("banana Cake"),
                            Cake(
                                title = " chocolate cake ",
                                description = " description ",
                                imageUrl = "Chocolate Cake.jpg",
                            ),
                        ),
                    ),
                ),
            )

            val viewModel = createViewModel(repository)
            advanceUntilIdle()

            assertEquals(
                CakeListUiState.Content(
                    cakes = listOf(
                        cake("banana Cake"),
                        cake("Chocolate Cake"),
                        cake("Victoria Sponge"),
                    ),
                ),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `startup failure shows error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repository = FakeCakeRepository(
                responses = mutableListOf(
                    Result.failure(
                        RuntimeException("Network error"),
                    ),
                ),
            )

            val viewModel = createViewModel(repository)

            advanceUntilIdle()

            assertEquals(
                CakeListUiState.Error(
                    message = "Unable to load cakes. Please try again.",
                ),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `retry loads cakes after startup failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val cakes = listOf(
                cake("Chocolate Cake"),
            )

            val repository = FakeCakeRepository(
                responses = mutableListOf(
                    Result.failure(
                        RuntimeException("Network error"),
                    ),
                    Result.success(cakes),
                ),
            )

            val viewModel = createViewModel(repository)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value is CakeListUiState.Error)
            viewModel.retry()
            assertEquals(
                CakeListUiState.Loading,
                viewModel.uiState.value,
            )

            advanceUntilIdle()
            val state = viewModel.uiState.value

            assertTrue(state is CakeListUiState.Content)
            state as CakeListUiState.Content

            assertEquals(cakes, state.cakes)
            assertFalse(state.isRefreshing)
        }

    @Test
    fun `refresh keeps existing cakes visible and updates content`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val initialCakes = listOf(
                cake("Chocolate Cake"),
            )

            val refreshedCakes = listOf(
                cake("Chocolate Cake"),
                cake("Apple Cake"),
            )

            val repository = FakeCakeRepository(
                responses = mutableListOf(
                    Result.success(initialCakes),
                    Result.success(refreshedCakes),
                ),
            )

            val viewModel = createViewModel(repository)

            advanceUntilIdle()

            viewModel.refresh()

            val refreshingState =
                viewModel.uiState.value as CakeListUiState.Content

            assertTrue(refreshingState.isRefreshing)
            assertEquals(
                initialCakes,
                refreshingState.cakes,
            )

            advanceUntilIdle()

            val refreshedState =
                viewModel.uiState.value as CakeListUiState.Content

            assertFalse(refreshedState.isRefreshing)
            assertEquals(
                listOf(
                    "Apple Cake",
                    "Chocolate Cake",
                ),
                refreshedState.cakes.map { it.title },
            )
        }

    private fun createViewModel(
        repository: CakeRepository,
    ): CakeListViewModel =
        CakeListViewModel(
            getCakesUseCase = GetCakesUseCase(repository),
        )

    private fun cake(
        title: String,
    ): Cake =
        Cake(
            title = title,
            description = "Description",
            imageUrl = "$title.jpg",
        )

    private class FakeCakeRepository(
        private val responses: MutableList<Result<List<Cake>>>,
    ) : CakeRepository {

        override suspend fun getCakes(): List<Cake> =
            responses
                .removeFirst()
                .getOrThrow()
    }

    @Test
    fun `refresh failure keeps existing cakes and emits snackbar`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val existingCake =
                Cake(
                    title = "Chocolate Cake",
                    description = "Chocolate cake",
                    imageUrl = "image.jpg",
                )

            val repository =
                FakeCakeRepository(
                    responses = mutableListOf(
                        Result.success(
                            listOf(existingCake),
                        ),
                        Result.failure(
                            RuntimeException("Network error"),
                        ),
                    ),
                )

            val viewModel = createViewModel(repository)
            advanceUntilIdle()

            val messages = mutableListOf<String>()
            backgroundScope.launch(
                UnconfinedTestDispatcher(testScheduler),
            ) {
                viewModel.snackbarMessage.toList(messages)
            }

            viewModel.refresh()
            advanceUntilIdle()
            assertEquals(
                CakeListUiState.Content(
                    cakes = listOf(existingCake),
                    isRefreshing = false,
                ),
                viewModel.uiState.value,
            )

            assertEquals(
                listOf(
                    "Unable to refresh cakes. Please try again.",
                ),
                messages,
            )
        }
}