package me.fruitman.sprint.util

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ArgumentsViewModelProviderFactory(
    private val arguments: Bundle?): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T
        = modelClass.getConstructor(Bundle::class.java).newInstance(arguments)
}