package com.subinbabu.cakelist.feature.cakelist.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil3.compose.AsyncImage
import com.subinbabu.cakelist.R
import com.subinbabu.cakelist.feature.cakelist.domain.model.Cake

@Composable
fun CakeListRoute(
    viewModel: CakeListViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(
            Lifecycle.State.STARTED,
        ) {
            viewModel.snackbarMessage.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    CakeListScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CakeListScreen(
    uiState: CakeListUiState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
) {
    var selectedCake by remember {
        mutableStateOf<Cake?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                    )
                },
                actions = {
                    val content =
                        uiState as? CakeListUiState.Content

                    if (content != null) {
                        // TODO: Add pull-to-refresh as an additional refresh interaction.
                        TextButton(
                            onClick = onRefresh,
                            enabled = !content.isRefreshing,
                        ) {
                            Text(
                                text = stringResource(R.string.refresh),
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            )
        },
    ) { innerPadding ->

        when (uiState) {
            CakeListUiState.Loading -> {
                LoadingContent(
                    modifier = Modifier.padding(innerPadding),
                )
            }

            is CakeListUiState.Content -> {
                CakeListContent(
                    state = uiState,
                    onCakeClick = { cake ->
                        selectedCake = cake
                    },
                    modifier = Modifier.padding(innerPadding),
                )
            }

            is CakeListUiState.Error -> {
                ErrorContent(
                    message = uiState.message,
                    onRetry = onRetry,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }

    selectedCake?.let { cake ->
        CakeDescriptionDialog(
            cake = cake,
            onDismiss = {
                selectedCake = null
            },
        )
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CakeListContent(
    state: CakeListUiState.Content,
    onCakeClick: (Cake) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        if (state.isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // TODO: Add an explicit empty state when the API returns no cakes.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(
                items = state.cakes,
            ) { index, cake ->
                AnimatedCakeRow(
                    cake = cake,
                    onClick = { onCakeClick(cake) }
                )

                if (index < state.cakes.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun AnimatedCakeRow(
    cake: Cake,
    onClick: () -> Unit,
) {
    var startAnimation by remember {
        mutableStateOf(false)
    }

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "cakeRowAlpha",
    )

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.96f,
        animationSpec = tween(durationMillis = 300),
        label = "cakeRowScale",
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .alpha(alpha)
            .scale(scale),
    ) {
        CakeRow(
            cake = cake,
            onClick = onClick,
        )
    }
}

@Composable
private fun CakeRow(
    cake: Cake,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = cake.imageUrl,
            contentDescription = cake.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(96.dp)
                .clip(MaterialTheme.shapes.medium),
            placeholder = painterResource(R.drawable.cake_placeholder),
            error = painterResource(R.drawable.cake_placeholder),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = cake.title,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
        )

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.retry),
            )
        }
    }
}

@Composable
private fun CakeDescriptionDialog(
    cake: Cake,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = cake.title,
            )
        },
        text = {
            Text(
                text = cake.description,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(
                    text = stringResource(R.string.close),
                )
            }
        },
    )
}