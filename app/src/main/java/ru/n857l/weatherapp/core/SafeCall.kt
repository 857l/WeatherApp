package ru.n857l.weatherapp.core

import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeCall(call: suspend () -> T): T {
    return try {
        call()
    } catch (e: IOException) {
        throw NoInternetException
    } catch (e: HttpException) {
        throw when (e.code()) {
            401 -> UnauthorizedException
            429 -> TooManyRequestsException
            else -> ServiceUnavailableException
        }
    }
}