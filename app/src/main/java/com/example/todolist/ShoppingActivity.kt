package com.example.todolist

import android.content.ContentValues
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class ShoppingActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var shoppingInput: EditText
    private lateinit var shoppingItemsContainer: LinearLayout
    private lateinit var noShoppingItemsText: TextView

    // CAMBIA: lleva id, nombre, cantidad y estado
    private val items = mutableListOf<ShoppingItem>()

    // NUEVO
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping)

        val selectedPalette =
            PaletteManager.getSelectedPalette(this)

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            selectedPalette
        )

        drawerLayout =
            findViewById(R.id.shoppingDrawerLayout)

        navigationView =
            findViewById(R.id.shoppingNavigationView)

        shoppingInput =
            findViewById(R.id.shoppingInput)

        shoppingItemsContainer =
            findViewById(R.id.shoppingItemsContainer)

        noShoppingItemsText =
            findViewById(R.id.noShoppingItemsText)

        navigationView.itemIconTintList = null

        // NUEVO
        db = DatabaseHelper(this)

        cargarLista()
        actualizarLista()
        configurarMenu()

        findViewById<CardView>(R.id.addShoppingButton)
            .setOnClickListener {
                agregarProducto()
            }
    }

    override fun onResume() {
        super.onResume()

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            PaletteManager.getSelectedPalette(this)
        )

        cargarLista()
        actualizarLista()
    }

    override fun onDestroy() {
        super.onDestroy()

        db.close()
    }

    private fun configurarMenu() {

        val menuButton =
            findViewById<View>(R.id.menuButton)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(
                navigationView
            )
        }

        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    drawerLayout.closeDrawers()

                    val intent =
                        Intent(
                            this,
                            MainActivity::class.java
                        )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_tasks -> {
                    drawerLayout.closeDrawers()

                    val intent =
                        Intent(
                            this,
                            TasksActivity::class.java
                        )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_calendar -> {
                    drawerLayout.closeDrawers()

                    val intent =
                        Intent(
                            this,
                            CalendarActivity::class.java
                        )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_notes -> {
                    drawerLayout.closeDrawers()

                    val intent =
                        Intent(
                            this,
                            NotesActivity::class.java
                        )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_shopping -> {
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_wellness -> {
                    drawerLayout.closeDrawers()

                    val intent =
                        Intent(
                            this,
                            WellnessActivity::class.java
                        )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_customization -> {
                    drawerLayout.closeDrawers()

                    val intent =
                        Intent(
                            this,
                            CustomizationActivity::class.java
                        )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    true
                }

                else -> false
            }
        }
    }

    /*
     * CAMBIA: ahora el nombre se escribe en el
     * campo de siempre, y al pulsar + se abre
     * un diálogo para elegir la cantidad.
     */

    private fun agregarProducto() {

        val nombre =
            shoppingInput.text
                .toString()
                .trim()

        if (nombre.isEmpty()) {

            shoppingInput.error =
                "Escribe un producto"

            return
        }

        pedirCantidad(nombre)
    }

    private fun pedirCantidad(nombre: String) {

        val palette =
            PaletteManager.getSelectedPalette(this)

        val input =
            EditText(this)

        input.inputType =
            InputType.TYPE_CLASS_NUMBER

        input.setText("1")
        input.setSelection(1)

        input.textSize = 16f

        input.setTextColor(palette.text)
        input.setHintTextColor(palette.textLight)

        input.setPadding(
            18.dp(),
            14.dp(),
            18.dp(),
            14.dp()
        )

        val container =
            LinearLayout(this)

        container.orientation =
            LinearLayout.VERTICAL

        container.setPadding(
            20.dp(),
            4.dp(),
            20.dp(),
            4.dp()
        )

        container.addView(input)

        val dialog =
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("¿Cuántos?")
                .setView(container)
                .setNegativeButton("CANCELAR", null)
                .setPositiveButton("AGREGAR", null)
                .create()

        dialog.setOnShowListener {

            val fondo =
                GradientDrawable().apply {

                    setColor(palette.background)

                    cornerRadius =
                        24.dp().toFloat()
                }

            dialog.window?.setBackgroundDrawable(fondo)

            val titleId =
                resources.getIdentifier(
                    "alertTitle",
                    "id",
                    "android"
                )

            dialog.findViewById<TextView>(titleId)
                ?.setTextColor(palette.text)

            input.setTextColor(palette.text)
            input.setHintTextColor(palette.textLight)

            input.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    palette.primary
                )

            dialog.getButton(
                androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE
            ).setTextColor(palette.primary)

            dialog.getButton(
                androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE
            ).setTextColor(palette.textLight)

            dialog.getButton(
                androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val cantidad =
                    input.text
                        .toString()
                        .trim()
                        .toIntOrNull()

                if (cantidad == null || cantidad < 1) {

                    input.error = "Pon un número de 1 o más"

                    return@setOnClickListener
                }

                insertarProducto(nombre, cantidad)

                shoppingInput.text.clear()

                cargarLista()
                actualizarLista()

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun actualizarLista() {

        shoppingItemsContainer.removeAllViews()

        if (items.isEmpty()) {

            noShoppingItemsText.visibility =
                View.VISIBLE

            return
        }

        noShoppingItemsText.visibility =
            View.GONE

        items.forEach { item ->

            /*
             * CONTENEDOR DE LA FILA
             */

            val itemRow =
                LinearLayout(this)

            itemRow.orientation =
                LinearLayout.HORIZONTAL

            itemRow.gravity =
                Gravity.CENTER_VERTICAL

            val rowParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    64.dp()
                )

            rowParams.setMargins(
                0,
                0,
                0,
                10.dp()
            )

            itemRow.layoutParams =
                rowParams

            /*
             * FONDO REDONDEADO
             */

            val background =
                GradientDrawable()

            background.setColor(
                getColor(
                    R.color.white
                )
            )

            background.cornerRadius =
                18.dp().toFloat()

            itemRow.background =
                background

            itemRow.setPadding(
                8.dp(),
                4.dp(),
                6.dp(),
                4.dp()
            )

            /*
             * CHECKBOX
             */

            val checkBox =
                CheckBox(this)

            checkBox.layoutParams =
                LinearLayout.LayoutParams(
                    48.dp(),
                    48.dp()
                )

            checkBox.isChecked =
                item.comprado

            itemRow.addView(
                checkBox
            )

            /*
             * NOMBRE DEL PRODUCTO
             *
             * CAMBIA: muestra la cantidad delante
             * cuando es más de 1.
             */

            val itemText =
                TextView(this)

            val textParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            textParams.weight =
                1f

            textParams.marginStart =
                4.dp()

            itemText.layoutParams =
                textParams

            itemText.text =
                if (item.cantidad > 1)
                    "${item.cantidad} × ${item.nombre}"
                else
                    item.nombre

            itemText.textSize =
                16f

            itemText.setTextColor(
                getColor(
                    R.color.soft_text
                )
            )

            itemText.maxLines =
                2

            aplicarEstadoProducto(
                itemText,
                item.comprado
            )

            itemRow.addView(
                itemText
            )

            /*
             * BOTÓN BORRAR
             *
             * Solo aparece cuando
             * el producto está marcado.
             */

            val deleteButton =
                ImageButton(this)

            deleteButton.layoutParams =
                LinearLayout.LayoutParams(
                    48.dp(),
                    48.dp()
                )

            deleteButton.setImageResource(
                android.R.drawable.ic_menu_delete
            )

            deleteButton.setColorFilter(
                getColor(
                    R.color.soft_coral
                )
            )

            deleteButton.background =
                null

            deleteButton.contentDescription =
                "Eliminar producto"

            deleteButton.visibility =
                if (item.comprado)
                    View.VISIBLE
                else
                    View.GONE

            itemRow.addView(
                deleteButton
            )

            /*
             * AL MARCAR EL CHECKBOX
             *
             * CAMBIA: escribe el estado en SQLite.
             */

            checkBox.setOnCheckedChangeListener {
                    _, checked ->

                item.comprado =
                    checked

                actualizarComprado(
                    item.id,
                    checked
                )

                aplicarEstadoProducto(
                    itemText,
                    checked
                )

                deleteButton.visibility =
                    if (checked)
                        View.VISIBLE
                    else
                        View.GONE
            }

            /*
             * AL PULSAR BORRAR
             *
             * CAMBIA: borra por id.
             */

            deleteButton.setOnClickListener {

                eliminarProducto(
                    item.id,
                    item.nombre
                )
            }

            shoppingItemsContainer.addView(
                itemRow
            )
        }
    }

    private fun aplicarEstadoProducto(
        textView: TextView,
        completado: Boolean
    ) {

        if (completado) {

            textView.paintFlags =
                textView.paintFlags or
                        Paint.STRIKE_THRU_TEXT_FLAG

            textView.alpha =
                0.55f

        } else {

            textView.paintFlags =
                textView.paintFlags and
                        Paint.STRIKE_THRU_TEXT_FLAG.inv()

            textView.alpha =
                1f
        }
    }

    /*
     * CAMBIA: recibe id y nombre, no la posición.
     */

    private fun eliminarProducto(
        id: Long,
        nombre: String
    ) {

        SoftDialog.mostrarConfirmacion(
            context = this,
            titulo = "Eliminar producto",
            mensaje = "¿Quieres eliminar \"$nombre\" de la lista?",
            textoAceptar = "ELIMINAR",
            textoCancelar = "CANCELAR"
        ) {

            borrarProducto(id)

            cargarLista()
            actualizarLista()
        }
    }

    /*
     * NUEVO: operaciones sobre SQLite.
     * Reemplazan a guardarLista().
     */

    private fun insertarProducto(
        nombre: String,
        cantidad: Int
    ) {

        val valores = ContentValues()

        valores.put(
            DatabaseHelper.COL_NOMBRE,
            nombre
        )

        valores.put(
            DatabaseHelper.COL_CANTIDAD,
            cantidad
        )

        valores.put(
            DatabaseHelper.COL_COMPRADO,
            0
        )

        db.writableDatabase.insert(
            DatabaseHelper.TABLA_PRODUCTO,
            null,
            valores
        )
    }

    private fun actualizarComprado(
        id: Long,
        comprado: Boolean
    ) {

        val valores = ContentValues()

        valores.put(
            DatabaseHelper.COL_COMPRADO,
            if (comprado) 1 else 0
        )

        db.writableDatabase.update(
            DatabaseHelper.TABLA_PRODUCTO,
            valores,
            "${DatabaseHelper.COL_ID_PRODUCTO} = ?",
            arrayOf(id.toString())
        )
    }

    private fun borrarProducto(id: Long) {

        db.writableDatabase.delete(
            DatabaseHelper.TABLA_PRODUCTO,
            "${DatabaseHelper.COL_ID_PRODUCTO} = ?",
            arrayOf(id.toString())
        )
    }

    /*
     * CAMBIA: lee desde SQLite.
     */

    private fun cargarLista() {

        items.clear()

        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLA_PRODUCTO,
            arrayOf(
                DatabaseHelper.COL_ID_PRODUCTO,
                DatabaseHelper.COL_NOMBRE,
                DatabaseHelper.COL_CANTIDAD,
                DatabaseHelper.COL_COMPRADO
            ),
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COL_ID_PRODUCTO} ASC"
        )

        cursor.use {

            val colId =
                it.getColumnIndexOrThrow(
                    DatabaseHelper.COL_ID_PRODUCTO
                )

            val colNombre =
                it.getColumnIndexOrThrow(
                    DatabaseHelper.COL_NOMBRE
                )

            val colCantidad =
                it.getColumnIndexOrThrow(
                    DatabaseHelper.COL_CANTIDAD
                )

            val colComprado =
                it.getColumnIndexOrThrow(
                    DatabaseHelper.COL_COMPRADO
                )

            while (it.moveToNext()) {

                items.add(
                    ShoppingItem(
                        id = it.getLong(colId),
                        nombre = it.getString(colNombre),
                        cantidad = it.getInt(colCantidad),
                        comprado = it.getInt(colComprado) == 1
                    )
                )
            }
        }
    }

    private fun Int.dp(): Int {
        return (
                this *
                        resources.displayMetrics.density
                ).toInt()
    }
}

data class ShoppingItem(
    val id: Long,
    var nombre: String,
    var cantidad: Int,
    var comprado: Boolean
)
