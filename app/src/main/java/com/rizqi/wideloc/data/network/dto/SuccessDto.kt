package com.rizqi.wideloc.data.network.dto

/**
 * Represents a generic success response returned by the server.
 *
 * This DTO is commonly used for simple acknowledgments where no
 * additional data is required besides confirming that an operation
 * has completed successfully.
 *
 * @property status A textual indicator of the operation result,
 * typically containing values such as `"ok"`, `"success"`, or similar.
 */
data class SuccessDto(
    val status: String,
)
