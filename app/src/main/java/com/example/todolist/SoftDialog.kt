package com.example.todolist

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

object SoftDialog {

    fun mostrarConfirmacion(
        context: Context,
        titulo: String,
        mensaje: String,
        textoAceptar: String,
        textoCancelar: String = "Cancelar",
        alAceptar: () -> Unit
    ) {

        val palette = PaletteManager.getSelectedPalette(context)

        val contenedor = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24.dp(), 22.dp(), 24.dp(), 18.dp())
        }

        val tituloView = TextView(context).apply {
            text = titulo
            textSize = 20f
            setTextColor(palette.text)
        }

        val mensajeView = TextView(context).apply {
            text = mensaje
            textSize = 15f
            setTextColor(palette.textLight)
            setPadding(0, 10.dp(), 0, 4.dp())
        }

        contenedor.addView(tituloView)
        contenedor.addView(mensajeView)

        val dialog = AlertDialog.Builder(context)
            .setView(contenedor)
            .setNegativeButton(textoCancelar, null)
            .setPositiveButton(textoAceptar) { _, _ ->
                alAceptar()
            }
            .create()

        dialog.setOnShowListener {

            val fondo = GradientDrawable().apply {
                setColor(palette.background)
                cornerRadius = 24.dp().toFloat()
                setStroke(1.dp(), palette.surface)
            }

            dialog.window?.setBackgroundDrawable(fondo)

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(palette.primary)
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setTextColor(palette.textLight)
            }
        }

        dialog.show()
    }

    private fun Int.dp(): Int {
        return (this * android.content.res.Resources.getSystem().displayMetrics.density).toInt()
    }
}