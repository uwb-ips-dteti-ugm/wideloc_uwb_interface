package com.rizqi.wideloc.usecase

class GenerateIDInteractor : GenerateIDUseCase {
    private val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    override fun invoke(length: Int): String {
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
}