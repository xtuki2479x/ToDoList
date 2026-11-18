package com.example.todolist

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class CustomizationActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var palettesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customization)

        drawerLayout =
            findViewById(R.id.customizationDrawerLayout)

        navigationView =
            findViewById(R.id.customizationNavigationView)

        palettesContainer =
            findViewById(R.id.palettesContainer)

        navigationView.itemIconTintList = null

        /*
         * Aplicamos la paleta que estaba guardada.
         */

        val selectedPalette =
            PaletteManager.getSelectedPalette(this)

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            selectedPalette
        )

        configurarMenu()
        mostrarPaletas()
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

                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        )
                    )

                    overridePendingTransition(
                        0,
                        0
                    )

                    true
                }

                R.id.nav_tasks -> {

                    drawerLayout.closeDrawers()

                    startActivity(
                        Intent(
                            this,
                            TasksActivity::class.java
                        )
                    )

                    overridePendingTransition(
                        0,
                        0
                    )

                    true
                }

                R.id.nav_calendar -> {

                    drawerLayout.closeDrawers()

                    startActivity(
                        Intent(
                            this,
                            CalendarActivity::class.java
                        )
                    )

                    overridePendingTransition(
                        0,
                        0
                    )

                    true
                }

                R.id.nav_notes -> {

                    drawerLayout.closeDrawers()

                    startActivity(
                        Intent(
                            this,
                            NotesActivity::class.java
                        )
                    )

                    overridePendingTransition(
                        0,
                        0
                    )

                    true
                }

                R.id.nav_shopping -> {

                    drawerLayout.closeDrawers()

                    startActivity(
                        Intent(
                            this,
                            ShoppingActivity::class.java
                        )
                    )

                    overridePendingTransition(
                        0,
                        0
                    )

                    true
                }

                R.id.nav_wellness -> {

                    drawerLayout.closeDrawers()

                    startActivity(
                        Intent(
                            this,
                            WellnessActivity::class.java
                        )
                    )

                    overridePendingTransition(
                        0,
                        0
                    )

                    true
                }

                R.id.nav_customization -> {

                    drawerLayout.closeDrawers()

                    true
                }

                else -> false
            }
        }
    }

    private fun mostrarPaletas() {

        palettesContainer.removeAllViews()

        val selectedPalette =
            PaletteManager.getSelectedPalette(this)

        PaletteManager.getPalettes(this).forEach { palette ->

            val card =
                crearTarjetaPaleta(
                    palette,
                    palette.id == selectedPalette.id
                )

            palettesContainer.addView(
                card
            )
        }
    }


    private fun crearTarjetaPaleta(
        palette: AppPalette,
        seleccionada: Boolean
    ): CardView {

        val card =
            CardView(this)

        val parametros =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                82.dp()
            )

        parametros.bottomMargin =
            10.dp()

        card.layoutParams =
            parametros

        card.radius =
            18.dp().toFloat()

        card.cardElevation =
            0f

        /*
         * Las tarjetas de selección
         * permanecen blancas para que
         * podamos ver claramente
         * los colores de cada paleta.
         */

        card.setCardBackgroundColor(
            android.graphics.Color.WHITE
        )

        card.isClickable =
            true

        card.isFocusable =
            true

        val contenido =
            LinearLayout(this)

        contenido.orientation =
            LinearLayout.HORIZONTAL

        contenido.gravity =
            Gravity.CENTER_VERTICAL

        contenido.setPadding(
            14.dp(),
            10.dp(),
            14.dp(),
            10.dp()
        )

        /*
         * MUESTRAS DE COLOR
         */

        val colores =
            LinearLayout(this)

        colores.orientation =
            LinearLayout.HORIZONTAL

        colores.gravity =
            Gravity.CENTER_VERTICAL

        val coloresPaleta =
            listOf(
                palette.primary,
                palette.primaryLight,
                palette.surface
            )

        coloresPaleta.forEach { color ->

            val muestra =
                View(this)

            val parametrosMuestra =
                LinearLayout.LayoutParams(
                    34.dp(),
                    48.dp()
                )

            parametrosMuestra.marginEnd =
                4.dp()

            muestra.layoutParams =
                parametrosMuestra

            val drawable =
                GradientDrawable()

            drawable.setColor(
                color
            )

            drawable.cornerRadius =
                10.dp().toFloat()

            muestra.background =
                drawable

            colores.addView(
                muestra
            )
        }

        contenido.addView(
            colores
        )

        /*
         * NOMBRE DE LA PALETA
         */

        val nombre =
            TextView(this)

        val parametrosNombre =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        parametrosNombre.weight =
            1f

        parametrosNombre.marginStart =
            14.dp()

        nombre.layoutParams =
            parametrosNombre

        nombre.text =
            palette.name

        nombre.textSize =
            17f

        nombre.setTextColor(
            palette.text
        )

        contenido.addView(
            nombre
        )

        /*
         * SELECTOR
         */

        val selector =
            TextView(this)

        selector.layoutParams =
            LinearLayout.LayoutParams(
                36.dp(),
                36.dp()
            )

        selector.gravity =
            Gravity.CENTER

        selector.textSize =
            20f

        selector.text =
            if (seleccionada) {
                "●"
            } else {
                "○"
            }

        selector.setTextColor(
            palette.primary
        )

        contenido.addView(
            selector
        )

        card.addView(
            contenido
        )

        /*
         * AL ELEGIR UNA PALETA
         */

        card.setOnClickListener {

            PaletteManager.savePalette(
                this,
                palette
            )

            recreate()
        }


        return card
    }

    private fun Int.dp(): Int {

        return (
                this *
                        resources.displayMetrics.density
                ).toInt()
    }
}