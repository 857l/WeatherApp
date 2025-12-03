package ru.n857l.weatherapp.findcity.domain

sealed class DomainException : Exception()

data object NoInternetException : DomainException() {
    private fun readResolve(): Any = NoInternetException
}