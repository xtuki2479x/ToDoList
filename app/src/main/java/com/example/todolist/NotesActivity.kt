package com.example.todolist

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class NotesActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var notesContainer: LinearLayout
    private lateinit var noNotesText: TextView

    // CAMBIA: ahora guarda pares (id, texto)
    private val notes = mutableListOf<Pair<Long, String>>()

    // NUEVO
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val selectedPalette =
            PaletteManager.getSelectedPalette(this)

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            selectedPalette
        )

        drawerLayout = findViewById(R.id.notesDrawerLayout)
        navigationView = findViewById(R.id.notesNavigationView)
        notesContainer = findViewById(R.id.notesContainer)
        noNotesText = findViewById(R.id.noNotesText)
        navigationView.itemIconTintList = null

        // NUEVO
        db = DatabaseHelper(this)

        cargarNotas()
        actualizarNotas()
        configurarMenu()

        findViewById<CardView>(R.id.addNoteButton).setOnClickListener {
            mostrarNuevaNota()
        }
    }

    override fun onResume() {
        super.onResume()

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            PaletteManager.getSelectedPalette(this)
        )

        cargarNotas()
        actualizarNotas()
    }

    override fun onDestroy() {
        super.onDestroy()

        db.close()
    }



    private fun configurarMenu() {

        val menuButton = findViewById<View>(R.id.menuButton)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    drawerLayout.closeDrawers()

                    val intent = Intent(
                        this,
                        MainActivity::class.java
                    )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_tasks -> {
                    drawerLayout.closeDrawers()

                    val intent = Intent(
                        this,
                        TasksActivity::class.java
                    )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_calendar -> {
                    drawerLayout.closeDrawers()

                    val intent = Intent(
                        this,
                        CalendarActivity::class.java
                    )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_notes -> {
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_shopping -> {
                    drawerLayout.closeDrawers()

                    val intent = Intent(
                        this,
                        ShoppingActivity::class.java
                    )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_wellness -> {
                    drawerLayout.closeDrawers()
                    val intent = Intent(this, WellnessActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_customization -> {
                    drawerLayout.closeDrawers()
                    val intent = Intent(this, CustomizationActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }
    }


    private fun mostrarNuevaNota() {

        val density =
            resources.displayMetrics.density

        fun dp(value: Int): Int {
            return (value * density).toInt()
        }

        val palette =
            PaletteManager.getSelectedPalette(this)

        val input =
            EditText(this)

        input.hint =
            "Escribe tu nota..."

        input.textSize =
            16f

        input.setTextColor(
            palette.text
        )

        input.setHintTextColor(
            palette.textLight
        )

        input.setPadding(
            dp(18),
            dp(14),
            dp(18),
            dp(14)
        )

        val container =
            LinearLayout(this)

        container.orientation =
            LinearLayout.VERTICAL

        container.setPadding(
            dp(20),
            dp(4),
            dp(20),
            dp(4)
        )

        container.addView(
            input
        )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Nueva nota ♡"
                )
                .setView(
                    container
                )
                .setNegativeButton(
                    "CANCELAR",
                    null
                )
                .setPositiveButton(
                    "GUARDAR",
                    null
                )
                .create()

        dialog.setOnShowListener {

            /*
             * FONDO DEL DIÁLOGO
             */

            val fondo =
                android.graphics.drawable.GradientDrawable().apply {

                    setColor(
                        palette.background
                    )

                    cornerRadius =
                        dp(24).toFloat()
                }

            dialog.window?.setBackgroundDrawable(
                fondo
            )

            /*
             * TÍTULO
             */

            val titleId =
                resources.getIdentifier(
                    "alertTitle",
                    "id",
                    "android"
                )

            val title =
                dialog.findViewById<TextView>(
                    titleId
                )

            title?.setTextColor(
                palette.text
            )

            /*
             * CAMPO DE TEXTO
             */

            input.setTextColor(
                palette.text
            )

            input.setHintTextColor(
                palette.textLight
            )

            input.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    palette.primary
                )

            /*
             * BOTÓN GUARDAR
             */

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setTextColor(
                palette.primary
            )

            /*
             * BOTÓN CANCELAR
             */

            dialog.getButton(
                AlertDialog.BUTTON_NEGATIVE
            ).setTextColor(
                palette.textLight
            )

            /*
             * ACCIÓN GUARDAR
             *
             * CAMBIA: inserta en SQLite y
             * recarga la lista desde la base.
             */

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val text =
                    input.text
                        .toString()
                        .trim()

                if (text.isEmpty()) {

                    input.error =
                        "Escribe algo antes de guardar"

                    return@setOnClickListener
                }

                insertarNota(text)

                cargarNotas()
                actualizarNotas()

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun actualizarNotas() {

        notesContainer.removeAllViews()

        if (notes.isEmpty()) {

            notesContainer.addView(noNotesText)

            noNotesText.text =
                "Todavía no tienes notas ♡"

            return
        }

        notes.forEach { (id, note) ->

            val card = LayoutInflater
                .from(this)
                .inflate(
                    android.R.layout.simple_list_item_1,
                    notesContainer,
                    false
                ) as TextView

            card.text = note
            card.textSize = 16f
            card.setTextColor(
                getColor(R.color.soft_text)
            )

            card.setPadding(
                20,
                20,
                20,
                20
            )

            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(
                0,
                0,
                0,
                14
            )

            card.layoutParams = params

            card.setBackgroundColor(
                getColor(R.color.soft_mustard)
            )

            // CAMBIA: se pasa el id, no la posición
            card.setOnLongClickListener {

                mostrarEliminarNota(id)

                true
            }

            notesContainer.addView(card)
        }
    }

    // CAMBIA: recibe el id de la nota
    private fun mostrarEliminarNota(
        id: Long
    ) {

        val density =
            resources.displayMetrics.density

        fun dp(value: Int): Int {
            return (value * density).toInt()
        }

        val palette =
            PaletteManager.getSelectedPalette(this)

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Eliminar nota"
                )
                .setMessage(
                    "¿Quieres eliminar esta nota?"
                )
                .setNegativeButton(
                    "CANCELAR",
                    null
                )
                .setPositiveButton(
                    "ELIMINAR",
                    null
                )
                .create()

        dialog.setOnShowListener {

            /*
             * FONDO DEL DIÁLOGO
             */

            val fondo =
                android.graphics.drawable.GradientDrawable().apply {

                    setColor(
                        palette.background
                    )

                    cornerRadius =
                        dp(24).toFloat()
                }

            dialog.window?.setBackgroundDrawable(
                fondo
            )

            /*
             * TÍTULO
             */

            val titleId =
                resources.getIdentifier(
                    "alertTitle",
                    "id",
                    "android"
                )

            dialog.findViewById<TextView>(
                titleId
            )?.setTextColor(
                palette.text
            )

            /*
             * MENSAJE
             */

            dialog.findViewById<TextView>(
                android.R.id.message
            )?.setTextColor(
                palette.textLight
            )

            /*
             * BOTÓN CANCELAR
             */

            dialog.getButton(
                AlertDialog.BUTTON_NEGATIVE
            ).setTextColor(
                palette.textLight
            )

            /*
             * BOTÓN ELIMINAR
             */

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setTextColor(
                palette.accentStrong
            )

            /*
             * ACCIÓN ELIMINAR
             *
             * CAMBIA: borra por id en SQLite.
             */

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                eliminarNota(id)

                cargarNotas()
                actualizarNotas()

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    /*
     * NUEVO: reemplaza a guardarNotas().
     * El guardado ahora es inmediato en cada
     * operación, así que el método desaparece.
     */

    private fun insertarNota(texto: String) {

        val valores = ContentValues()

        valores.put(
            DatabaseHelper.COL_CONTENIDO,
            texto
        )

        db.writableDatabase.insert(
            DatabaseHelper.TABLA_NOTA,
            null,
            valores
        )
    }

    private fun eliminarNota(id: Long) {

        db.writableDatabase.delete(
            DatabaseHelper.TABLA_NOTA,
            "${DatabaseHelper.COL_ID_NOTA} = ?",
            arrayOf(id.toString())
        )
    }

    // CAMBIA: lee desde SQLite
    private fun cargarNotas() {

        notes.clear()

        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLA_NOTA,
            arrayOf(
                DatabaseHelper.COL_ID_NOTA,
                DatabaseHelper.COL_CONTENIDO
            ),
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COL_ID_NOTA} ASC"
        )

        cursor.use {

            val colId =
                it.getColumnIndexOrThrow(
                    DatabaseHelper.COL_ID_NOTA
                )

            val colTexto =
                it.getColumnIndexOrThrow(
                    DatabaseHelper.COL_CONTENIDO
                )

            while (it.moveToNext()) {

                notes.add(
                    it.getLong(colId) to
                            it.getString(colTexto)
                )
            }
        }
    }
}
