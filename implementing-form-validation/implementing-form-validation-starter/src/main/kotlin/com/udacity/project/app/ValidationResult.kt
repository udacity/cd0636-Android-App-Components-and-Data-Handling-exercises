package com.udacity.project.app

/**
 * Sealed class representing the result of form validation.
 */
sealed class ValidationResult {
    /**
     * Validation passed successfully.
     */
    object Valid : ValidationResult()

    /**
     * Validation failed with an error message.
     */
    data class Invalid(val message: String) : ValidationResult()
}
