package com.udacity.project.app

/**
 * Sealed class representing the result of a network operation.
 */
sealed class NetworkResult<out T> {
    /**
     * Successful network operation with data.
     */
    data class Success<T>(val data: T) : NetworkResult<T>()

    /**
     * Failed network operation with error details.
     */
    data class Error(val error: NetworkError) : NetworkResult<Nothing>()

    /**
     * Network operation in progress.
     */
    object Loading : NetworkResult<Nothing>()
}

/**
 * Sealed class hierarchy for different types of network errors.
 */
sealed class NetworkError(open val message: String) {
    /**
     * No internet connection available.
     */
    data class NoInternet(
        override val message: String = "No internet connection"
    ) : NetworkError(message)

    /**
     * Request timed out.
     */
    data class Timeout(
        override val message: String = "Request timed out"
    ) : NetworkError(message)

    /**
     * Server error (5xx status codes).
     */
    data class ServerError(
        val code: Int,
        override val message: String = "Server error: $code"
    ) : NetworkError(message)

    /**
     * Resource not found (404 status code).
     */
    data class NotFound(
        override val message: String = "Resource not found"
    ) : NetworkError(message)

    /**
     * Unknown or unexpected error.
     */
    data class Unknown(
        override val message: String = "An unexpected error occurred"
    ) : NetworkError(message)
}

/**
 * Configuration for retry logic with exponential backoff.
 */
data class RetryConfig(
    val maxRetries: Int = 3,
    val initialDelayMs: Long = 1000,
    val maxDelayMs: Long = 10000,
    val factor: Double = 2.0
)
