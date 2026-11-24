
package com.example.todolist

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class TareaRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val coleccion = db.collection("tareas")

    // Guarda una tarea nueva y devuelve su ID.
    suspend fun guardar(tarea: Tarea): String {
        val referencia = coleccion.document()
        val nuevaTarea = tarea.copy(id = referencia.id)

        referencia.set(nuevaTarea).await()

        return referencia.id
    }

    // Obtiene todas las tareas ordenadas por fecha de vencimiento.
    suspend fun listar(): List<Tarea> {
        val resultado = coleccion
            .orderBy("fechaVencimiento", Query.Direction.ASCENDING)
            .get()
            .await()

        return resultado.documents.mapNotNull { documento ->
            documento.toObject(Tarea::class.java)?.copy(
                id = documento.id
            )
        }
    }

    // Busca una tarea por su ID.
    suspend fun obtener(id: String): Tarea? {
        require(id.isNotBlank()) { "El ID de la tarea no puede estar vacío" }

        val documento = coleccion.document(id).get().await()

        return documento.toObject(Tarea::class.java)?.copy(
            id = documento.id
        )
    }

    // Actualiza todos los datos de una tarea existente.
    suspend fun actualizar(tarea: Tarea) {
        require(tarea.id.isNotBlank()) { "La tarea debe tener un ID" }

        coleccion.document(tarea.id)
            .set(tarea)
            .await()
    }

    // Cambia el estado de completada de una tarea.
    suspend fun actualizarCompletada(id: String, completada: Boolean) {
        require(id.isNotBlank()) { "El ID de la tarea no puede estar vacío" }

        coleccion.document(id)
            .update("completada", completada)
            .await()
    }

    // Elimina una tarea por su ID.
    suspend fun eliminar(id: String) {
        require(id.isNotBlank()) { "El ID de la tarea no puede estar vacío" }

        coleccion.document(id)
            .delete()
            .await()
    }
}
