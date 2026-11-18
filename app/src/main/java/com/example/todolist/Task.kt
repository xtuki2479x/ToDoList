package com.example.todolist

data class Task(
    val id: Long,
    var name: String,
    var difficulty: String,
    var academic: Boolean = false,
    var dueDate: String? = "",
    var completed: Boolean = false,
    var prerequisiteId: Long? = null
)
