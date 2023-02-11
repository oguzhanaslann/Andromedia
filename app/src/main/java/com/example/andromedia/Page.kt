package com.example.andromedia

import androidx.compose.runtime.Composable

data class Page(
    val route: String,
    val title: String,
    val content: @Composable () -> Unit
)