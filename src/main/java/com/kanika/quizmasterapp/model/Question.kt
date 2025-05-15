package com.kanika.quizmasterapp.model

data class Question(
    val questionText: String,
    val options: List<String>,
    val correctAnswer: Int
)