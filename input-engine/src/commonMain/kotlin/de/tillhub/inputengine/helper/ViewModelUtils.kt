package de.tillhub.inputengine.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun <reified VM : ViewModel> rememberViewModel(
    crossinline factoryBuilder: () -> ViewModelProvider.Factory,
): VM {
    val factory = remember { factoryBuilder() }
    return viewModel(factory = factory)
}
