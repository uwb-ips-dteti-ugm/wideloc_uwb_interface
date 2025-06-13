package com.rizqi.wideloc.usecase

class GenerateIDInteractor : GenerateIDUseCase {
    private val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    override fun invoke(): String {
        return (1..4)
            .map { chars.random() }
            .joinToString("")
    }
}