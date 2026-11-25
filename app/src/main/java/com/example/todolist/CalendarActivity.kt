
package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var calendarView: CalendarView
    private lateinit var monthTitle: TextView
    private lateinit var selectedDateTitle: TextView
    private lateinit var dayTasksContainer: LinearLayout
    private lateinit var noDayTasksText: TextView

    private val calendar = Calendar.getInstance()
    private val tasks = mutableListOf<Tarea>()

    private val tareaRepository = TareaRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val selectedPalette =
            PaletteManager.getSelectedPalette(this)

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            selectedPalette
        )

        drawerLayout = findViewById(R.id.calendarDrawerLayout)
        navigationView = findViewById(R.id.calendarNavigationView)
        navigationView.itemIconTintList = null

        calendarView = findViewById(R.id.calendarView)
        monthTitle = findViewById(R.id.monthTitle)
        selectedDateTitle = findViewById(R.id.selectedDateTitle)
        dayTasksContainer = findViewById(R.id.dayTasksContainer)
        noDayTasksText = findViewById(R.id.noDayTasksText)

        configurarMenu()
        configurarCalendario()
        configurarCambioDeMes()

        actualizarTituloMes()
    }

    override fun onResume() {
        super.onResume()

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            PaletteManager.getSelectedPalette(this)
        )

        cargarTareas()
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

                    val intent = Intent(
                        this,
                        WellnessActivity::class.java
                    )

                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    true
                }

                R.id.nav_customization -> {
                    drawerLayout.closeDrawers()

                    val intent = Intent(
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

    private fun configurarCalendario() {

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            calendar.set(
                year,
                month,
                dayOfMonth
            )

            actualizarTituloMes()
            mostrarTareasDelDia(calendar.timeInMillis)
        }
    }

    private fun configurarCambioDeMes() {

        val previousMonthButton =
            findViewById<TextView>(R.id.previousMonthButton)

        val nextMonthButton =
            findViewById<TextView>(R.id.nextMonthButton)

        previousMonthButton.setOnClickListener {

            calendar.add(
                Calendar.MONTH,
                -1
            )

            calendarView.date = calendar.timeInMillis

            actualizarTituloMes()
            mostrarTareasDelDia(calendar.timeInMillis)
        }

        nextMonthButton.setOnClickListener {

            calendar.add(
                Calendar.MONTH,
                1
            )

            calendarView.date = calendar.timeInMillis

            actualizarTituloMes()
            mostrarTareasDelDia(calendar.timeInMillis)
        }
    }

    private fun actualizarTituloMes() {

        val formato = SimpleDateFormat(
            "MMMM yyyy",
            Locale("es", "ES")
        )

        val texto = formato.format(calendar.time)

        monthTitle.text =
            texto.replaceFirstChar {
                it.uppercase()
            }
    }

    private fun mostrarTareasDelDia(timeInMillis: Long) {

        dayTasksContainer.removeAllViews()

        val formatoFecha = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        val fechaSeleccionada =
            formatoFecha.format(timeInMillis)

        val formatoVisible = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        selectedDateTitle.text =
            "Tareas del ${formatoVisible.format(timeInMillis)}"

        val tareasDelDia = tasks.filter { task ->

            task.academica &&
                    !task.fechaVencimiento.isNullOrEmpty() &&
                    task.fechaVencimiento == fechaSeleccionada
        }

        if (tareasDelDia.isEmpty()) {

            dayTasksContainer.addView(noDayTasksText)

            noDayTasksText.text =
                "No tienes tareas para este día ♡"

            return
        }

        tareasDelDia.forEach { task ->

            val taskText = TextView(this)

            taskText.text =
                if (task.completada) {
                    "☑  ${task.nombre}"
                } else {
                    "□  ${task.nombre}"
                }

            taskText.textSize = 16f

            taskText.setTextColor(
                getColor(R.color.soft_text)
            )

            taskText.setPadding(
                0,
                8,
                0,
                8
            )

            dayTasksContainer.addView(taskText)
        }
    }

    private fun cargarTareas() {

        lifecycleScope.launch {

            try {

                val tareasFirestore = tareaRepository.listar()

                tasks.clear()
                tasks.addAll(tareasFirestore)

                mostrarTareasDelDia(calendar.timeInMillis)

            } catch (e: Exception) {

                Toast.makeText(
                    this@CalendarActivity,
                    "No se pudieron cargar las tareas",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}