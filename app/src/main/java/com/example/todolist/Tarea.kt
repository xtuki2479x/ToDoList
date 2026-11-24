package com.example.todolist

data class Tarea(
    val id: String = "",
    val nombre: String = "",
    val dificultad: String = "",
    val academica: Boolean = false,
    val fechaVencimiento: String? = null,
    val completada: Boolean = false
)
