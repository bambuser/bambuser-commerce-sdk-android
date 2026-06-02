package com.bambuser.commerce_sdk_demo_app.ui

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Content<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
