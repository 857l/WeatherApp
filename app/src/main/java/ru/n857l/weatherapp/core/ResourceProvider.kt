package ru.n857l.weatherapp.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ResourceProvider {

    fun getString(resId: Int, vararg args: Any): String

    class Base @Inject constructor(
        @ApplicationContext private val context: Context
    ) : ResourceProvider {

        override fun getString(resId: Int, vararg args: Any): String =
            context.getString(resId, *args)
    }
}