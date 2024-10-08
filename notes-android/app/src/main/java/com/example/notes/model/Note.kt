package com.example.notes.model

data class Note(
    val id: String = "0",
    val title: String,
    val description: String,
) {
    init {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(description.isNotBlank()) { "Description cannot be blank" }
    }
}
