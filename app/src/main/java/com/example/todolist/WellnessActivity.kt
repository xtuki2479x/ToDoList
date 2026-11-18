package com.example.todolist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class WellnessActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wellness)

        val selectedPalette =
            PaletteManager.getSelectedPalette(this)

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            selectedPalette
        )

        drawerLayout = findViewById(R.id.wellnessDrawerLayout)
        navigationView = findViewById(R.id.wellnessNavigationView)
        navigationView.itemIconTintList = null

        configurarMenu()
        configurarTarjetas()
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
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_tasks -> {
                    drawerLayout.closeDrawers()
                    val intent = Intent(this, TasksActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_calendar -> {
                    drawerLayout.closeDrawers()
                    val intent = Intent(this, CalendarActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_notes -> {
                    drawerLayout.closeDrawers()
                    val intent = Intent(this, NotesActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_shopping -> {
                    drawerLayout.closeDrawers()
                    val intent = Intent(this, ShoppingActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_wellness -> {
                    drawerLayout.closeDrawers()
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

    private fun configurarTarjetas() {

        findViewById<View>(R.id.stretchCard).setOnClickListener {
            abrirYouTube("estiramientos suaves para principiantes")
        }

        findViewById<View>(R.id.meditationCard).setOnClickListener {
            abrirYouTube("meditación guiada para principiantes")
        }

        findViewById<View>(R.id.youtubeCard).setOnClickListener {
            abrirYouTube("estiramientos meditación relajación")
        }

        findViewById<View>(R.id.musicCard).setOnClickListener {
            abrirAplicacionMusica()
        }

        findViewById<View>(R.id.timerCard).setOnClickListener {
            abrirTemporizador()
        }
    }

    private fun abrirYouTube(busqueda: String) {
        val url = "https://www.youtube.com/results?search_query=" +
                Uri.encode(busqueda)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun abrirAplicacionMusica() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:"))

        try {
            startActivity(intent)
        } catch (e: Exception) {
            val fallback = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://open.spotify.com/")
            )
            startActivity(fallback)
        }
    }

    private fun abrirTemporizador() {
        val intent = Intent(this, WellnessTimerActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}