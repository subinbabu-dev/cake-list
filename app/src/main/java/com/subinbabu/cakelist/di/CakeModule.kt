package com.subinbabu.cakelist.di

import com.subinbabu.cakelist.feature.cakelist.data.remote.CakeApi
import com.subinbabu.cakelist.feature.cakelist.data.repository.CakeRepositoryImpl
import com.subinbabu.cakelist.feature.cakelist.domain.repository.CakeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class CakeModule {

    @Binds
    abstract fun bindCakeRepository(
        implementation: CakeRepositoryImpl,
    ): CakeRepository

    companion object {

        @Provides
        @Singleton
        fun provideCakeApi(
            retrofit: Retrofit,
        ): CakeApi =
            retrofit.create(CakeApi::class.java)
    }
}