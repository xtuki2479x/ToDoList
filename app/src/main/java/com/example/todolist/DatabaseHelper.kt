package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE, null, VERSION) {

    companion object {

        const val NOMBRE = "soft_days.db"
        const val VERSION = 2

        // TAREA
        const val TABLA_TAREA = "TAREA"
        const val COL_ID_TAREA = "id_tarea"
        const val COL_NOMBRE = "nombre"
        const val COL_DIFICULTAD = "dificultad"
        const val COL_ACADEMICA = "academica"
        const val COL_FECHA = "fecha_vencimiento"
        const val COL_COMPLETADA = "completada"
        const val COL_PREREQUISITO = "prerequisite"
        const val COL_PREREQUISITO_ID = "prerequisite_id"

        // NOTA
        const val TABLA_NOTA = "NOTA"
        const val COL_ID_NOTA = "id_nota"
        const val COL_CONTENIDO = "contenido"

        // PRODUCTO_COMPRA
        const val TABLA_PRODUCTO = "PRODUCTO_COMPRA"
        const val COL_ID_PRODUCTO = "id_producto"
        const val COL_CANTIDAD = "cantidad"
        const val COL_COMPRADO = "comprado"

        // PALETA
        const val TABLA_PALETA = "PALETA"
        const val COL_ID_PALETA = "id_paleta"
        const val COL_COLOR_PRIMARIO = "color_primario"
        const val COL_COLOR_PRIMARIO_CLARO = "color_primario_claro"
        const val COL_COLOR_FONDO = "color_fondo"
        const val COL_COLOR_SUPERFICIE = "color_superficie"
        const val COL_COLOR_ACENTO = "color_acento"
        const val COL_COLOR_ACENTO_FUERTE = "color_acento_fuerte"
        const val COL_COLOR_TEXTO = "color_texto"
        const val COL_COLOR_TEXTO_SECUNDARIO = "color_texto_secundario"

        // CONFIGURACION
        const val TABLA_CONFIGURACION = "CONFIGURACION"
        const val COL_ID_CONFIGURACION = "id_configuracion"

        // REGISTRO DIARIO
        const val TABLA_REGISTRO = "REGISTRO_DIARIO"
        const val COL_FECHA_REGISTRO = "fecha"
        const val COL_VASOS = "vasos"
        const val COL_ANIMO = "animo"
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE $TABLA_TAREA (
                $COL_ID_TAREA INTEGER PRIMARY KEY,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_DIFICULTAD TEXT NOT NULL
                    CHECK ($COL_DIFICULTAD IN ('Fácil','Media','Difícil')),
                $COL_ACADEMICA INTEGER NOT NULL DEFAULT 0
                    CHECK ($COL_ACADEMICA IN (0,1)),
                $COL_FECHA TEXT,
                $COL_COMPLETADA INTEGER NOT NULL DEFAULT 0
                    CHECK ($COL_COMPLETADA IN (0,1)),
                $COL_PREREQUISITO TEXT NOT NULL DEFAULT '',
                $COL_PREREQUISITO_ID INTEGER,
                FOREIGN KEY ($COL_PREREQUISITO_ID)
                    REFERENCES $TABLA_TAREA($COL_ID_TAREA)
                    ON DELETE SET NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLA_NOTA (
                $COL_ID_NOTA INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CONTENIDO TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLA_PRODUCTO (
                $COL_ID_PRODUCTO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CANTIDAD INTEGER NOT NULL DEFAULT 1
                    CHECK ($COL_CANTIDAD >= 1),
                $COL_COMPRADO INTEGER NOT NULL DEFAULT 0
                    CHECK ($COL_COMPRADO IN (0,1))
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLA_PALETA (
                $COL_ID_PALETA INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL UNIQUE,
                $COL_COLOR_PRIMARIO TEXT NOT NULL,
                $COL_COLOR_PRIMARIO_CLARO TEXT NOT NULL,
                $COL_COLOR_FONDO TEXT NOT NULL,
                $COL_COLOR_SUPERFICIE TEXT NOT NULL,
                $COL_COLOR_ACENTO TEXT NOT NULL,
                $COL_COLOR_ACENTO_FUERTE TEXT NOT NULL,
                $COL_COLOR_TEXTO TEXT NOT NULL,
                $COL_COLOR_TEXTO_SECUNDARIO TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLA_CONFIGURACION (
                $COL_ID_CONFIGURACION INTEGER PRIMARY KEY
                    CHECK ($COL_ID_CONFIGURACION = 1),
                $COL_ID_PALETA INTEGER NOT NULL,
                FOREIGN KEY ($COL_ID_PALETA)
                    REFERENCES $TABLA_PALETA($COL_ID_PALETA)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLA_REGISTRO (
                $COL_FECHA_REGISTRO TEXT PRIMARY KEY,
                $COL_VASOS INTEGER NOT NULL DEFAULT 0,
                $COL_ANIMO TEXT
            )
            """.trimIndent()
        )

        insertarPaletasIniciales(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // Necesario para que ON DELETE SET NULL funcione
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        if (oldVersion < 2) {

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS $TABLA_REGISTRO (
                    $COL_FECHA_REGISTRO TEXT PRIMARY KEY,
                    $COL_VASOS INTEGER NOT NULL DEFAULT 0,
                    $COL_ANIMO TEXT
                )
                """.trimIndent()
            )
        }
    }

    // ---------------- TAREA ----------------

    fun insertarTarea(tarea: Task): Long {

        val valores = ContentValues()

        valores.put(COL_ID_TAREA, tarea.id)
        valores.put(COL_NOMBRE, tarea.name)
        valores.put(COL_DIFICULTAD, tarea.difficulty)
        valores.put(COL_ACADEMICA, if (tarea.academic) 1 else 0)
        valores.put(COL_FECHA, tarea.dueDate)
        valores.put(COL_COMPLETADA, if (tarea.completed) 1 else 0)

        if (tarea.prerequisiteId != null) {
            valores.put(COL_PREREQUISITO_ID, tarea.prerequisiteId)
        } else {
            valores.putNull(COL_PREREQUISITO_ID)
        }

        return writableDatabase.insert(TABLA_TAREA, null, valores)
    }

    fun cargarTareas(): List<Task> {

        val lista = mutableListOf<Task>()

        val cursor = readableDatabase.query(
            TABLA_TAREA,
            null,
            null,
            null,
            null,
            null,
            "$COL_ID_TAREA ASC"
        )

        cursor.use {

            val idxId = it.getColumnIndexOrThrow(COL_ID_TAREA)
            val idxNombre = it.getColumnIndexOrThrow(COL_NOMBRE)
            val idxDificultad = it.getColumnIndexOrThrow(COL_DIFICULTAD)
            val idxAcademica = it.getColumnIndexOrThrow(COL_ACADEMICA)
            val idxFecha = it.getColumnIndexOrThrow(COL_FECHA)
            val idxCompletada = it.getColumnIndexOrThrow(COL_COMPLETADA)
            val idxPrereq = it.getColumnIndexOrThrow(COL_PREREQUISITO_ID)

            while (it.moveToNext()) {

                lista.add(
                    Task(
                        id = it.getLong(idxId),
                        name = it.getString(idxNombre),
                        difficulty = it.getString(idxDificultad),
                        academic = it.getInt(idxAcademica) == 1,
                        dueDate = if (it.isNull(idxFecha)) null else it.getString(idxFecha),
                        completed = it.getInt(idxCompletada) == 1,
                        prerequisiteId = if (it.isNull(idxPrereq)) null else it.getLong(idxPrereq)
                    )
                )
            }
        }

        return lista
    }

    fun actualizarCompletada(idTarea: Long, completada: Boolean) {

        val valores = ContentValues()
        valores.put(COL_COMPLETADA, if (completada) 1 else 0)

        writableDatabase.update(
            TABLA_TAREA,
            valores,
            "$COL_ID_TAREA = ?",
            arrayOf(idTarea.toString())
        )
    }

    fun borrarTarea(idTarea: Long) {

        writableDatabase.delete(
            TABLA_TAREA,
            "$COL_ID_TAREA = ?",
            arrayOf(idTarea.toString())
        )
    }

    // ---------------- REGISTRO DIARIO ----------------

    fun hoyIso(): String {
        return java.text.SimpleDateFormat(
            "yyyy-MM-dd",
            java.util.Locale.getDefault()
        ).format(java.util.Date())
    }

    fun leerVasosHoy(): Int {

        val cursor = readableDatabase.query(
            TABLA_REGISTRO,
            arrayOf(COL_VASOS),
            "$COL_FECHA_REGISTRO = ?",
            arrayOf(hoyIso()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) return it.getInt(0)
        }

        return 0
    }

    fun leerAnimoHoy(): String? {

        val cursor = readableDatabase.query(
            TABLA_REGISTRO,
            arrayOf(COL_ANIMO),
            "$COL_FECHA_REGISTRO = ?",
            arrayOf(hoyIso()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst() && !it.isNull(0)) {
                return it.getString(0)
            }
        }

        return null
    }

    fun guardarVasosHoy(vasos: Int) {

        val valores = ContentValues()

        valores.put(COL_FECHA_REGISTRO, hoyIso())
        valores.put(COL_VASOS, vasos)
        valores.put(COL_ANIMO, leerAnimoHoy())

        writableDatabase.insertWithOnConflict(
            TABLA_REGISTRO,
            null,
            valores,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun guardarAnimoHoy(animo: String) {

        val valores = ContentValues()

        valores.put(COL_FECHA_REGISTRO, hoyIso())
        valores.put(COL_VASOS, leerVasosHoy())
        valores.put(COL_ANIMO, animo)

        writableDatabase.insertWithOnConflict(
            TABLA_REGISTRO,
            null,
            valores,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    // ---------------- PALETAS ----------------

    private fun insertarPaletasIniciales(db: SQLiteDatabase) {

        val paletas = listOf(
            listOf(
                "Naturaleza", "#588A87", "#B6C6BE", "#FAF8F3",
                "#DFD9CA", "#EFCABF", "#EEA995", "#454545", "#77736E"
            ),
            listOf(
                "Rosa suave", "#CF8997", "#F2BEC8", "#FDF8F9",
                "#F5E0DE", "#EEB8C2", "#DD91A0", "#4B4043", "#7E6C71"
            ),
            listOf(
                "Lavanda", "#8E84BE", "#C6C0E0", "#FAF8FD",
                "#E7E2F4", "#D3CAE8", "#AB9DD3", "#444050", "#746E82"
            ),
            listOf(
                "Arena", "#AE8F6C", "#DAC5AB", "#FBF8F2",
                "#EBDCC8", "#E8D3BB", "#C9A782", "#4B443D", "#7D7267"
            ),
            listOf(
                "Coral", "#E28B76", "#F6BEAE", "#FDF8F5",
                "#F7E0D7", "#F8CABE", "#E6947F", "#4E413D", "#7E6C65"
            ),
            listOf(
                "Amanecer", "#DAA44E", "#EFCD90", "#FDF9F1",
                "#F1E1C1", "#F8DAA7", "#E0AE5C", "#4D4436", "#7C6F5B"
            ),
            listOf(
                "Océano", "#4CA6C2", "#A6DAE7", "#F5FBFD",
                "#D8EDF3", "#BCE1EC", "#5BB5CF", "#3B464B", "#69777D"
            )
        )

        paletas.forEach { p ->

            val valores = ContentValues()

            valores.put(COL_NOMBRE, p[0])
            valores.put(COL_COLOR_PRIMARIO, p[1])
            valores.put(COL_COLOR_PRIMARIO_CLARO, p[2])
            valores.put(COL_COLOR_FONDO, p[3])
            valores.put(COL_COLOR_SUPERFICIE, p[4])
            valores.put(COL_COLOR_ACENTO, p[5])
            valores.put(COL_COLOR_ACENTO_FUERTE, p[6])
            valores.put(COL_COLOR_TEXTO, p[7])
            valores.put(COL_COLOR_TEXTO_SECUNDARIO, p[8])

            db.insert(TABLA_PALETA, null, valores)
        }
    }
}
