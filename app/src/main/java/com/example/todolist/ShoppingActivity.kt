
package com.example.todolist

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class ShoppingActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var shoppingInput: EditText
    private lateinit var shoppingItemsContainer: LinearLayout
    private lateinit var noShoppingItemsText: TextView

    private val items = mutableListOf<Producto>()
    private val productoRepository = ProductoRepository()

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
    }

    private fun configurarMenu() {

        val menuButton =
            findViewById<View>(R.id.menuButton)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    drawerLayout.closeDrawers()

                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_tasks -> {
                    drawerLayout.closeDrawers()

                    startActivity(
                        Intent(this, TasksActivity::class.java)
                    )
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_calendar -> {
                    drawerLayout.closeDrawers()

                    startActivity(
                        Intent(this, CalendarActivity::class.java)
                    )
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_notes -> {
                    drawerLayout.closeDrawers()

                    startActivity(
                        Intent(this, NotesActivity::class.java)
                    )
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_shopping -> {
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_wellness -> {
                    drawerLayout.closeDrawers()

                    startActivity(
                        Intent(this, WellnessActivity::class.java)
                    )
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_customization -> {
                    drawerLayout.closeDrawers()

                    startActivity(
                        Intent(this, CustomizationActivity::class.java)
                    )
                    overridePendingTransition(0, 0)

                    true
                }

                else -> false
            }
        }
    }

    private fun agregarProducto() {

        val nombre =
            shoppingInput.text.toString().trim()

        if (nombre.isEmpty()) {
            shoppingInput.error = "Escribe un producto"
            return
        }

        pedirCantidad(nombre)
    }

    private fun pedirCantidad(nombre: String) {

        val palette =
            PaletteManager.getSelectedPalette(this)

        val input = EditText(this)

        input.inputType = InputType.TYPE_CLASS_NUMBER
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

        val container = LinearLayout(this)

        container.orientation = LinearLayout.VERTICAL

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

            val fondo = GradientDrawable().apply {
                setColor(palette.background)
                cornerRadius = 24.dp().toFloat()
            }

            dialog.window?.setBackgroundDrawable(fondo)

            val titleId = resources.getIdentifier(
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
                    input.text.toString().trim().toIntOrNull()

                if (cantidad == null || cantidad < 1) {
                    input.error = "Pon un número de 1 o más"
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    try {
                        productoRepository.guardar(
                            Producto(
                                nombre = nombre,
                                cantidad = cantidad
                            )
                        )

                        shoppingInput.text.clear()
                        cargarLista()

                        dialog.dismiss()

                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ShoppingActivity,
                            "No se pudo guardar el producto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun actualizarLista() {

        shoppingItemsContainer.removeAllViews()

        if (items.isEmpty()) {
            noShoppingItemsText.visibility = View.VISIBLE
            return
        }

        noShoppingItemsText.visibility = View.GONE

        items.forEach { item ->

            // CONTENEDOR DE LA FILA

            val itemRow = LinearLayout(this)

            itemRow.orientation = LinearLayout.HORIZONTAL
            itemRow.gravity = Gravity.CENTER_VERTICAL

            val rowParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                64.dp()
            )

            rowParams.setMargins(
                0,
                0,
                0,
                10.dp()
            )

            itemRow.layoutParams = rowParams

            // FONDO REDONDEADO

            val background = GradientDrawable()

            background.setColor(getColor(R.color.white))
            background.cornerRadius = 18.dp().toFloat()

            itemRow.background = background

            itemRow.setPadding(
                8.dp(),
                4.dp(),
                6.dp(),
                4.dp()
            )

            // CHECKBOX

            val checkBox = CheckBox(this)

            checkBox.layoutParams =
                LinearLayout.LayoutParams(
                    48.dp(),
                    48.dp()
                )

            checkBox.isChecked = item.comprado

            itemRow.addView(checkBox)

            // NOMBRE DEL PRODUCTO

            val itemText = TextView(this)

            val textParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            textParams.weight = 1f
            textParams.marginStart = 4.dp()

            itemText.layoutParams = textParams

            itemText.text =
                if (item.cantidad > 1) {
                    "${item.cantidad} × ${item.nombre}"
                } else {
                    item.nombre
                }

            itemText.textSize = 16f

            itemText.setTextColor(
                getColor(R.color.soft_text)
            )

            itemText.maxLines = 2

            aplicarEstadoProducto(
                itemText,
                item.comprado
            )

            itemRow.addView(itemText)

            // BOTÓN BORRAR

            val deleteButton = ImageButton(this)

            deleteButton.layoutParams =
                LinearLayout.LayoutParams(
                    48.dp(),
                    48.dp()
                )

            deleteButton.setImageResource(
                android.R.drawable.ic_menu_delete
            )

            deleteButton.setColorFilter(
                getColor(R.color.soft_coral)
            )

            deleteButton.background = null

            deleteButton.contentDescription =
                "Eliminar producto"

            deleteButton.visibility =
                if (item.comprado) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            itemRow.addView(deleteButton)

            // ACTUALIZAR ESTADO EN FIRESTORE

            checkBox.setOnCheckedChangeListener { _, checked ->

                val estadoAnterior = item.comprado

                item.comprado = checked

                aplicarEstadoProducto(itemText, checked)

                deleteButton.visibility =
                    if (checked) View.VISIBLE else View.GONE

                checkBox.isEnabled = false

                lifecycleScope.launch {
                    try {
                        productoRepository.actualizarComprado(
                            item.id,
                            checked
                        )

                    } catch (e: Exception) {

                        item.comprado = estadoAnterior
                        checkBox.isChecked = estadoAnterior

                        aplicarEstadoProducto(
                            itemText,
                            estadoAnterior
                        )

                        deleteButton.visibility =
                            if (estadoAnterior) {
                                View.VISIBLE
                            } else {
                                View.GONE
                            }

                        Toast.makeText(
                            this@ShoppingActivity,
                            "No se pudo actualizar el producto",
                            Toast.LENGTH_SHORT
                        ).show()

                    } finally {
                        checkBox.isEnabled = true
                    }
                }
            }

            // ELIMINAR PRODUCTO

            deleteButton.setOnClickListener {
                eliminarProducto(
                    item.id,
                    item.nombre
                )
            }

            shoppingItemsContainer.addView(itemRow)
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

            textView.alpha = 0.55f

        } else {

            textView.paintFlags =
                textView.paintFlags and
                        Paint.STRIKE_THRU_TEXT_FLAG.inv()

            textView.alpha = 1f
        }
    }

    private fun eliminarProducto(
        id: String,
        nombre: String
    ) {

        SoftDialog.mostrarConfirmacion(
            context = this,
            titulo = "Eliminar producto",
            mensaje = "¿Quieres eliminar \"$nombre\" de la lista?",
            textoAceptar = "ELIMINAR",
            textoCancelar = "CANCELAR"
        ) {

            lifecycleScope.launch {
                try {
                    productoRepository.eliminar(id)

                    cargarLista()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@ShoppingActivity,
                        "No se pudo eliminar el producto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // CARGAR PRODUCTOS DESDE FIRESTORE

    private fun cargarLista() {

        lifecycleScope.launch {
            try {

                val productosFirestore =
                    productoRepository.listar()

                items.clear()
                items.addAll(productosFirestore)

                actualizarLista()

            } catch (e: Exception) {

                Toast.makeText(
                    this@ShoppingActivity,
                    "No se pudieron cargar los productos",
                    Toast.LENGTH_SHORT
                ).show()
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
