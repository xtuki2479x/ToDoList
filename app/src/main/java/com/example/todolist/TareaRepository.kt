package com.example.todolist

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class TareaRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val coleccion = db.collection("tareas")

    /** Guarda una tarea nueva y devuelve el ID generado por Firestore. */
    suspend fun guardar(tarea: Tarea): String {
        val ref = coleccion.add(tarea).await()
        ref.update("id", ref.id).await()
        return ref.id
    }

    /** Consulta todas las tareas ordenadas por fecha de vencimiento. */
    suspend fun listar(): List<Tarea> {
        return coleccion
            .orderBy("fechaVencimiento", Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(Tarea::class.java)
    }

    /** Marca una tarea como completada o pendiente. */
    suspend fun actualizarCompletada(id: String, completada: Boolean) {
        coleccion.document(id).update("completada", completada).await()
    }

    /** Elimina una tarea por su ID. */
    suspend fun eliminar(id: String) {
        coleccion.document(id).delete().await()
    }
}
