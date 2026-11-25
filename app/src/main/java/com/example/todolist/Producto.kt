
package com.example.todolist

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val cantidad: Int = 1,
    var comprado: Boolean = false
)
