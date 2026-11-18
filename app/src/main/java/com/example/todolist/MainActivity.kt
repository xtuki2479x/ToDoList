package com.example.todolist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        val selectedPalette =
            PaletteManager.getSelectedPalette(this)

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            selectedPalette
        )

        // Conectamos el menú lateral
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        navigationView.itemIconTintList = null

        // Botón ☰
        val menuButton = findViewById<android.view.View>(R.id.menuButton)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        // Opciones del menú
        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_tasks -> {
                    drawerLayout.closeDrawers()

                    val intent = android.content.Intent(
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

                    val intent = Intent(
                        this,
                        NotesActivity::class.java
                    )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

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

        db = DatabaseHelper(this)

        // Contador de agua
        configurarAgua()

        // Selector de ánimo
        configurarMood()

    }

    override fun onResume() {
        super.onResume()

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            PaletteManager.getSelectedPalette(this)
        )

        configurarAgua()
        configurarMood()
    }

    override fun onDestroy() {
        super.onDestroy()

        db.close()
    }



    private lateinit var db: DatabaseHelper

    private fun configurarAgua() {

        val contador = findViewById<android.widget.TextView>(
            R.id.waterCounter
        )

        val vasos = listOf(
            findViewById<android.widget.CheckBox>(R.id.water1),
            findViewById<android.widget.CheckBox>(R.id.water2),
            findViewById<android.widget.CheckBox>(R.id.water3),
            findViewById<android.widget.CheckBox>(R.id.water4),
            findViewById<android.widget.CheckBox>(R.id.water5),
            findViewById<android.widget.CheckBox>(R.id.water6)
        )

        // Pinta lo que ya estaba guardado hoy
        val guardados = db.leerVasosHoy()

        vasos.forEachIndexed { index, vaso ->
            vaso.isChecked = index < guardados
        }

        contador.text = "$guardados de 6 vasos"

        vasos.forEach { vaso ->

            vaso.setOnCheckedChangeListener { _, _ ->

                val cantidad = vasos.count { it.isChecked }

                contador.text = "$cantidad de 6 vasos"

                db.guardarVasosHoy(cantidad)
            }
        }
    }

    private fun configurarMood() {

        val moods = listOf(
            "feliz" to findViewById<android.view.View>(R.id.moodHappyButton),
            "neutral" to findViewById<android.view.View>(R.id.moodNeutralButton),
            "cansado" to findViewById<android.view.View>(R.id.moodTiredButton),
            "relajado" to findViewById<android.view.View>(R.id.moodRelaxedButton),
            "inspirado" to findViewById<android.view.View>(R.id.moodInspiredButton)
        )

        val guardado = db.leerAnimoHoy()

        moods.forEach { (clave, vista) ->
            vista.alpha = if (clave == guardado) 1f else 0.5f
        }

        moods.forEach { (clave, mood) ->

            mood.setOnClickListener {

                moods.forEach { (_, vista) ->
                    vista.alpha = 0.5f
                }

                mood.alpha = 1f

                db.guardarAnimoHoy(clave)
            }
        }
    }

}