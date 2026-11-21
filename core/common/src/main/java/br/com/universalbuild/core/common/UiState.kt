package br.com.universalbuild.core.common

/** Cross-feature immutable loading contract; user-facing messages belong in resources. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data object Empty : UiState<Nothing>
    data object Offline : UiState<Nothing>
    data class Failure(val reason: FailureReason) : UiState<Nothing>
    data class Content<T>(val value: T) : UiState<T>
}
enum class FailureReason { UNKNOWN, UNAUTHORIZED, TIMEOUT }
