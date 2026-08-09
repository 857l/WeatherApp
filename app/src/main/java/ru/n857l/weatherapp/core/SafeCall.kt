package ru.n857l.weatherapp.core

import ru.n857l.weatherapp.findcity.domain.NoInternetException
import ru.n857l.weatherapp.findcity.domain.ServiceUnavailableException
import ru.n857l.weatherapp.findcity.domain.TooManyRequestsException
import ru.n857l.weatherapp.findcity.domain.UnauthorizedException
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