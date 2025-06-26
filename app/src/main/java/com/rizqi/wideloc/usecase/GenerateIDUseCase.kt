package com.rizqi.wideloc.usecase

interface GenerateIDUseCase {
    fun invoke(length: Int = 4): String
}
