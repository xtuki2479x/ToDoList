package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.navigation.NavigationView

data class AppPalette(
    val id: Long,
    val name: String,
    val primary: Int,
    val primaryLight: Int,
    val background: Int,
    val surface: Int,
    val accent: Int,
    val accentStrong: Int,
    val text: Int,
    val textLight: Int
)

object PaletteManager {

    /*
     * Paletas de respaldo: se usan solo si la BD
     * todavía no tiene filas (por ejemplo, si algo
     * falló al crearla).
     */

    private val paletasRespaldo = listOf(

        AppPalette(
            1L,
            "Naturaleza",
            Color.rgb(88, 138, 135),
            Color.rgb(182, 198, 190),
            Color.rgb(250, 248, 243),
            Color.rgb(223, 217, 202),
            Color.rgb(239, 202, 191),
            Color.rgb(238, 169, 149),
            Color.rgb(69, 69, 69),
            Color.rgb(119, 115, 110)
        ),

        AppPalette(
            2L,
            "Rosa suave",
            Color.rgb(207, 137, 151),
            Color.rgb(242, 190, 200),
            Color.rgb(253, 248, 249),
            Color.rgb(245, 224, 222),
            Color.rgb(238, 184, 194),
            Color.rgb(221, 145, 160),
            Color.rgb(75, 64, 67),
            Color.rgb(126, 108, 113)
        ),

        AppPalette(
            3L,
            "Lavanda",
            Color.rgb(142, 132, 190),
            Color.rgb(198, 192, 224),
            Color.rgb(250, 248, 253),
            Color.rgb(231, 226, 244),
            Color.rgb(211, 202, 232),
            Color.rgb(171, 157, 211),
            Color.rgb(68, 64, 80),
            Color.rgb(116, 110, 130)
        ),

        AppPalette(
            4L,
            "Arena",
            Color.rgb(174, 143, 108),
            Color.rgb(218, 197, 171),
            Color.rgb(251, 248, 242),
            Color.rgb(235, 220, 200),
            Color.rgb(232, 211, 187),
            Color.rgb(201, 167, 130),
            Color.rgb(75, 68, 61),
            Color.rgb(125, 114, 103)
        ),

        AppPalette(
            5L,
            "Coral",
            Color.rgb(226, 139, 118),
            Color.rgb(246, 190, 174),
            Color.rgb(253, 248, 245),
            Color.rgb(247, 224, 215),
            Color.rgb(248, 202, 190),
            Color.rgb(230, 148, 127),
            Color.rgb(78, 65, 61),
            Color.rgb(126, 108, 101)
        ),

        AppPalette(
            6L,
            "Amanecer",
            Color.rgb(218, 164, 78),
            Color.rgb(239, 205, 144),
            Color.rgb(253, 249, 241),
            Color.rgb(241, 225, 193),
            Color.rgb(248, 218, 167),
            Color.rgb(224, 174, 92),
            Color.rgb(77, 68, 54),
            Color.rgb(124, 111, 91)
        ),

        AppPalette(
            7L,
            "Océano",
            Color.rgb(76, 166, 194),
            Color.rgb(166, 218, 231),
            Color.rgb(245, 251, 253),
            Color.rgb(216, 237, 243),
            Color.rgb(188, 225, 236),
            Color.rgb(91, 181, 207),
            Color.rgb(59, 70, 75),
            Color.rgb(105, 119, 125)
        )
    )

    /*
     * Lee todas las paletas desde la tabla PALETA.
     */

    fun getPalettes(context: Context): List<AppPalette> {

        val db = DatabaseHelper(context)

        val lista = mutableListOf<AppPalette>()

        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLA_PALETA,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COL_ID_PALETA} ASC"
        )

        cursor.use {

            val colId = it.getColumnIndexOrThrow(
                DatabaseHelper.COL_ID_PALETA
            )

            val colNombre = it.getColumnIndexOrThrow(
                DatabaseHelper.COL_NOMBRE
            )

            val colPrimario = it.getColumnIndexOrThrow(
                DatabaseHelper.COL_COLOR_PRIMARIO
            )

            val colPrimarioClaro = it.getColumnIndexOrThrow(
                DatabaseHelper.COL_COLOR_PRIMARIO_CLARO
            )

            val colFondo = it.getColumnIndexOrThrow(
                DatabaseHelper.COL_COLOR_FONDO
            )

            val colSuperficie = it.getColumnIndexOrThrow(
                DatabaseHelper.COL_COLOR_SUPERFICIE
            )

            val colAcento = it.getColumnIndexOrThrow(
                DatabaseHelper.COL_COLOR_ACENTO
            )

            val colAcentoFuerte = it.getColumnIndexOrThrow(
                DatabaseHelper.COL_COLOR_ACENTO_FUERTE
            )

            val colTexto = it.getColumnIndexOrThrow(
                DatabaseHelper.COL_COLOR_TEXTO
            )

            val colTextoSecundario = it.getColumnIndexOrThrow(
                DatabaseHelper.COL_COLOR_TEXTO_SECUNDARIO
            )

            while (it.moveToNext()) {

                lista.add(
                    AppPalette(
                        id = it.getLong(colId),
                        name = it.getString(colNombre),
                        primary = parsearColor(
                            it.getString(colPrimario)
                        ),
                        primaryLight = parsearColor(
                            it.getString(colPrimarioClaro)
                        ),
                        background = parsearColor(
                            it.getString(colFondo)
                        ),
                        surface = parsearColor(
                            it.getString(colSuperficie)
                        ),
                        accent = parsearColor(
                            it.getString(colAcento)
                        ),
                        accentStrong = parsearColor(
                            it.getString(colAcentoFuerte)
                        ),
                        text = parsearColor(
                            it.getString(colTexto)
                        ),
                        textLight = parsearColor(
                            it.getString(colTextoSecundario)
                        )
                    )
                )
            }
        }

        db.close()

        return if (lista.isEmpty()) paletasRespaldo else lista
    }

    /*
     * Devuelve la paleta activa según CONFIGURACION.
     */

    fun getSelectedPalette(
        context: Context
    ): AppPalette {

        val paletas = getPalettes(context)

        val db = DatabaseHelper(context)

        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLA_CONFIGURACION,
            arrayOf(DatabaseHelper.COL_ID_PALETA),
            "${DatabaseHelper.COL_ID_CONFIGURACION} = 1",
            null,
            null,
            null,
            null
        )

        var idPaleta: Long? = null

        cursor.use {
            if (it.moveToFirst()) {
                idPaleta = it.getLong(0)
            }
        }

        db.close()

        return paletas.firstOrNull {
            it.id == idPaleta
        } ?: paletas.first()
    }

    /*
     * Guarda la paleta activa en CONFIGURACION.
     * La tabla admite una sola fila (id = 1).
     */

    fun savePalette(
        context: Context,
        palette: AppPalette
    ) {

        val db = DatabaseHelper(context)

        val valores = ContentValues()

        valores.put(
            DatabaseHelper.COL_ID_CONFIGURACION,
            1
        )

        valores.put(
            DatabaseHelper.COL_ID_PALETA,
            palette.id
        )

        db.writableDatabase.insertWithOnConflict(
            DatabaseHelper.TABLA_CONFIGURACION,
            null,
            valores,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )

        db.close()
    }

    /*
     * Convierte "#RRGGBB" a Int.
     */

    private fun parsearColor(texto: String?): Int {

        return try {
            Color.parseColor(texto)
        } catch (e: Exception) {
            Color.BLACK
        }
    }

    fun applyPalette(
        root: View,
        palette: AppPalette
    ) {
        aplicarVista(
            root,
            palette
        )
    }

    private fun aplicarVista(
        view: View,
        palette: AppPalette
    ) {

        val background =
            view.background

        if (background is ColorDrawable) {

            val color =
                background.color

            val nuevoColor =
                convertirColor(
                    color,
                    palette
                )

            if (nuevoColor != color) {
                view.setBackgroundColor(
                    nuevoColor
                )
            }
        }

        if (view is CardView) {

            val color =
                view.cardBackgroundColor
                    .defaultColor

            val nuevoColor =
                convertirColor(
                    color,
                    palette
                )

            if (nuevoColor != color) {

                view.setCardBackgroundColor(
                    nuevoColor
                )
            }
        }

        if (view is TextView) {

            val color =
                view.currentTextColor

            val nuevoColor =
                convertirColor(
                    color,
                    palette
                )

            if (nuevoColor != color) {

                view.setTextColor(
                    nuevoColor
                )
            }
        }

        if (view is NavigationView) {

            view.setBackgroundColor(
                palette.background
            )

            view.setItemTextColor(
                android.content.res.ColorStateList.valueOf(
                    palette.text
                )
            )

            view.itemIconTintList = null

            if (view.headerCount > 0) {

                val header =
                    view.getHeaderView(0)

                header.setBackgroundColor(
                    palette.primaryLight
                )

                if (header is ViewGroup) {

                    aplicarVista(
                        header,
                        palette
                    )
                }
            }
        }

        if (view is ViewGroup) {

            for (i in 0 until view.childCount) {

                aplicarVista(
                    view.getChildAt(i),
                    palette
                )
            }
        }
    }

    private fun convertirColor(
        color: Int,
        palette: AppPalette
    ): Int {

        return when (color) {

            Color.rgb(88, 138, 135) ->
                palette.primary

            Color.rgb(182, 198, 190) ->
                palette.primaryLight

            Color.rgb(250, 248, 243) ->
                palette.background

            Color.rgb(223, 217, 202) ->
                palette.surface

            Color.rgb(239, 202, 191) ->
                palette.accent

            Color.rgb(238, 169, 149) ->
                palette.accentStrong

            Color.rgb(69, 69, 69) ->
                palette.text

            Color.rgb(119, 115, 110) ->
                palette.textLight

            else ->
                color
        }
    }
}
