
package com.example.todolist

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductoRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val coleccion = db.collection("productos")

    // Guarda un producto nuevo y devuelve su ID.
    suspend fun guardar(producto: Producto): String {
        val referencia = coleccion.document()
        val nuevoProducto = producto.copy(id = referencia.id)

        referencia.set(nuevoProducto).await()

        return referencia.id
    }

    // Obtiene todos los productos.
    suspend fun listar(): List<Producto> {
        val resultado = coleccion.get().await()

        return resultado.documents.mapNotNull { documento ->
            documento.toObject(Producto::class.java)?.copy(
                id = documento.id
            )
        }
    }

    // Actualiza el estado de comprado.
    suspend fun actualizarComprado(
        id: String,
        comprado: Boolean
    ) {
        require(id.isNotBlank()) { "El ID no puede estar vacío" }

        coleccion.document(id)
            .update("comprado", comprado)
            .await()
    }

    // Elimina un producto.
    suspend fun eliminar(id: String) {
        require(id.isNotBlank()) { "El ID no puede estar vacío" }

        coleccion.document(id)
            .delete()
            .await()
    }
}
