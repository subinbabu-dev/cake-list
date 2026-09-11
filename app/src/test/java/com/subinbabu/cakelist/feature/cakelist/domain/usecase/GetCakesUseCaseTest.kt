package com.subinbabu.cakelist.feature.cakelist.domain.usecase

import com.subinbabu.cakelist.feature.cakelist.domain.model.Cake
import com.subinbabu.cakelist.feature.cakelist.testutil.FakeCakeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetCakesUseCaseTest {

    @Test
    fun `invoke removes equivalent duplicates ignoring case and whitespace`() = runTest {
        val repository = FakeCakeRepository(
            cakes = listOf(
                Cake(
                    title = "Chocolate Cake",
                    description = "Rich chocolate cake",
                    imageUrl = "image.jpg",
                ),
                Cake(
                    title = " chocolate cake ",
                    description = "RICH CHOCOLATE CAKE",
                    imageUrl = "image.jpg",
                ),
            ),
        )

        val useCase = GetCakesUseCase(repository)
        val result = useCase()
        assertEquals(1, result.size)
    }

    @Test
    fun `invoke keeps cakes with same title but different descriptions`() = runTest {
        val repository = FakeCakeRepository(
            cakes = listOf(
                Cake(
                    title = "Chocolate Cake",
                    description = "Rich chocolate cake",
                    imageUrl = "image.jpg",
                ),
                Cake(
                    title = "Chocolate Cake",
                    description = "Light chocolate cake",
                    imageUrl = "image.jpg",
                ),
            ),
        )

        val useCase = GetCakesUseCase(repository)
        val result = useCase()
        assertEquals(2, result.size)
    }

    @Test
    fun `invoke keeps cakes with same title and description but different image urls`() = runTest {
        val firstCake = Cake(
            title = "Chocolate Cake",
            description = "Rich chocolate cake",
            imageUrl = "https://example.com/image-one.jpg",
        )
        val secondCake = Cake(
            title = "Chocolate Cake",
            description = "Rich chocolate cake",
            imageUrl = "https://example.com/image-two.jpg",
        )

        val repository = FakeCakeRepository(
            cakes = listOf(firstCake, secondCake),
        )
        val useCase = GetCakesUseCase(repository)
        val result = useCase()
        assertEquals(
            listOf(firstCake, secondCake),
            result,
        )
    }

    @Test
    fun `invoke sorts cakes alphabetically ignoring case`() = runTest {
        val repository = FakeCakeRepository(
            cakes = listOf(
                Cake(
                    title = "Victoria Sponge",
                    description = "Description",
                    imageUrl = "image3.jpg",
                ),
                Cake(
                    title = "apple Cake",
                    description = "Description",
                    imageUrl = "image1.jpg",
                ),
                Cake(
                    title = "Chocolate Cake",
                    description = "Description",
                    imageUrl = "image2.jpg",
                ),
            ),
        )

        val useCase = GetCakesUseCase(repository)
        val result = useCase()
        assertEquals(
            listOf(
                "apple Cake",
                "Chocolate Cake",
                "Victoria Sponge",
            ),
            result.map { it.title },
        )
    }
}