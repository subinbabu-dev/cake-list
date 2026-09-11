package com.subinbabu.cakelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.subinbabu.cakelist.feature.cakelist.presentation.CakeListRoute
import com.subinbabu.cakelist.feature.cakelist.presentation.CakeListViewModel
import com.subinbabu.cakelist.ui.theme.CakeListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: CakeListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CakeListTheme {
                CakeListRoute(viewModel = viewModel)
            }
        }
    }
}