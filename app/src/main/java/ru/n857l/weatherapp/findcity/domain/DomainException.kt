package ru.n857l.weatherapp.findcity.domain

sealed class DomainException : Exception()

data object NoInternetException : DomainException() {
    private fun readResolve(): Any = NoInternetException
}

data object ServiceUnavailableException : DomainException() {
    private fun readResolve(): Any = ServiceUnavailableException
}

data object UnauthorizedException : DomainException() {
    private fun readResolve(): Any = UnauthorizedException
}

data object TooManyRequestsException : DomainException() {
    private fun readResolve(): Any = TooManyRequestsException
}