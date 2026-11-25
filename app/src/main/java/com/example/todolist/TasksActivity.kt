package com.example.todolist

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class TasksActivity : AppCompatActivity() {

    private data class TaskUi(
        val id: String,
        val name: String,
        val difficulty: String,
        val academic: Boolean,
        val dueDate: String,
        var completed: Boolean,
        val prerequisiteId: String? = null
    )

    private val tasks = mutableListOf<TaskUi>()

    private lateinit var easyPreview: TextView
    private lateinit var mediumPreview: TextView
    private lateinit var hardPreview: TextView

    private lateinit var repository: TareaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tasks)

        val selectedPalette =
            PaletteManager.getSelectedPalette(this)

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            selectedPalette
        )

        easyPreview = findViewById(R.id.easyPreview)
        mediumPreview = findViewById(R.id.mediumPreview)
        hardPreview = findViewById(R.id.hardPreview)

        repository = TareaRepository()
        cargarTareas()

        val drawerLayout =
            findViewById<androidx.drawerlayout.widget.DrawerLayout>(
                R.id.tasksDrawerLayout
            )

        val navigationView =
            findViewById<com.google.android.material.navigation.NavigationView>(
                R.id.tasksNavigationView
            )

        navigationView.itemIconTintList = null

        findViewById<View>(R.id.menuButton).setOnClickListener {
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

        findViewById<CardView>(R.id.easyCard).setOnClickListener {
            mostrarCategoria("Fácil")
        }

        findViewById<CardView>(R.id.mediumCard).setOnClickListener {
            mostrarCategoria("Media")
        }

        findViewById<CardView>(R.id.hardCard).setOnClickListener {
            mostrarCategoria("Difícil")
        }

        findViewById<CardView>(R.id.addTaskButton).setOnClickListener {
            mostrarNuevaTarea()
        }
    }

    override fun onResume() {
        super.onResume()

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            PaletteManager.getSelectedPalette(this)
        )

        cargarTareas()
    }

    private fun mostrarNuevaTarea() {

        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_new_task, null)

        val nameInput =
            view.findViewById<EditText>(R.id.taskNameInput)

        val difficultyGroup =
            view.findViewById<RadioGroup>(R.id.difficultyGroup)

        val academicCheck =
            view.findViewById<CheckBox>(R.id.academicCheck)

        val dueDateContainer =
            view.findViewById<LinearLayout>(R.id.dueDateContainer)

        val dueDateInput =
            view.findViewById<EditText>(R.id.dueDateInput)

        val prerequisiteCheck =
            view.findViewById<CheckBox>(R.id.prerequisiteCheck)

        val prerequisiteContainer =
            view.findViewById<LinearLayout>(R.id.prerequisiteContainer)

        val prerequisiteInput =
            view.findViewById<EditText>(R.id.prerequisiteInput)

        val suggestionsContainer =
            view.findViewById<LinearLayout>(
                R.id.prerequisiteSuggestions
            )

        var selectedPrerequisiteId: String? = null

        academicCheck.setOnCheckedChangeListener { _, checked ->

            dueDateContainer.visibility =
                if (checked) View.VISIBLE else View.GONE
        }

        prerequisiteCheck.setOnCheckedChangeListener { _, checked ->

            prerequisiteContainer.visibility =
                if (checked) View.VISIBLE else View.GONE

            if (!checked) {

                prerequisiteInput.setText("")
                suggestionsContainer.removeAllViews()
                suggestionsContainer.visibility = View.GONE
                selectedPrerequisiteId = null
            }
        }

        prerequisiteInput.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {

                    selectedPrerequisiteId = null

                    mostrarSugerencias(
                        s?.toString().orEmpty(),
                        suggestionsContainer
                    ) { id, nombre ->

                        selectedPrerequisiteId = id

                        prerequisiteInput.setText(nombre)
                        prerequisiteInput.setSelection(
                            nombre.length
                        )

                        suggestionsContainer.removeAllViews()
                        suggestionsContainer.visibility =
                            View.GONE
                    }
                }
            }
        )

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setNegativeButton("CANCELAR", null)
            .setPositiveButton("CREAR", null)
            .create()

        dialog.setOnShowListener {

            val palette =
                PaletteManager.getSelectedPalette(this)

            PaletteManager.applyPalette(
                view,
                palette
            )

            val fondo =
                GradientDrawable().apply {

                    setColor(palette.background)

                    cornerRadius =
                        24.dp().toFloat()

                    setStroke(
                        1.dp(),
                        palette.surface
                    )
                }

            dialog.window?.setBackgroundDrawable(fondo)

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setTextColor(
                palette.primary
            )

            dialog.getButton(
                AlertDialog.BUTTON_NEGATIVE
            ).setTextColor(
                palette.textLight
            )

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val name =
                    nameInput.text.toString().trim()

                if (name.isEmpty()) {

                    nameInput.error =
                        "Escribe el nombre de la tarea"

                    return@setOnClickListener
                }

                val fechaIso =
                    normalizarFecha(
                        dueDateInput.text.toString()
                    )

                if (
                    academicCheck.isChecked &&
                    fechaIso.isEmpty()
                ) {

                    dueDateInput.error =
                        "Escribe la fecha como 11/11/2026"

                    return@setOnClickListener
                }

                // CAMBIA: resuelve el requisito aunque
                // el usuario no haya tocado la sugerencia
                val textoRequisito =
                    prerequisiteInput.text.toString().trim()

                val requisitoId =
                    if (prerequisiteCheck.isChecked) {

                        selectedPrerequisiteId
                            ?: tasks.firstOrNull {
                                normalizar(it.name) ==
                                        normalizar(textoRequisito)
                            }?.id

                    } else {
                        null
                    }

                if (
                    prerequisiteCheck.isChecked &&
                    requisitoId == null
                ) {

                    prerequisiteInput.error =
                        "Elige un requisito de la lista"

                    return@setOnClickListener
                }

                val difficulty =
                    when (difficultyGroup.checkedRadioButtonId) {

                        R.id.easyRadio -> "Fácil"

                        R.id.mediumRadio -> "Media"

                        R.id.hardRadio -> "Difícil"

                        else -> "Fácil"
                    }

                val tarea = Tarea(
                    nombre = name,
                    dificultad = difficulty,
                    academica = academicCheck.isChecked,
                    fechaVencimiento = if (academicCheck.isChecked) fechaIso else null,
                    completada = false,
                    prerequisiteId = requisitoId
                )

                lifecycleScope.launch {
                    try {
                        repository.guardar(tarea)
                        cargarTareas()
                        dialog.dismiss()
                    } catch (e: Exception) {
                        android.util.Log.e("TasksActivity", "Error al guardar tarea", e)
                        android.widget.Toast.makeText(
                            this@TasksActivity,
                            "No se pudo guardar la tarea",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun mostrarSugerencias(
        texto: String,
        contenedor: LinearLayout,
        onElegir: (String, String) -> Unit
    ) {

        contenedor.removeAllViews()

        if (texto.isBlank()) {
            contenedor.visibility = View.GONE
            return
        }

        val buscado = normalizar(texto)

        val coincidencias = tasks
            .filter { normalizar(it.name).contains(buscado) }
            .take(4)

        if (coincidencias.isEmpty()) {
            contenedor.visibility = View.GONE
            return
        }

        val palette =
            PaletteManager.getSelectedPalette(this)

        for (tarea in coincidencias) {

            val fila = TextView(this)

            fila.text = tarea.name
            fila.textSize = 14f
            fila.setTextColor(palette.text)
            fila.setPadding(16, 20, 16, 20)
            fila.setBackgroundColor(palette.surface)

            fila.setOnClickListener {
                onElegir(tarea.id, tarea.name)
            }

            contenedor.addView(fila)
        }

        contenedor.visibility = View.VISIBLE
    }

    private fun normalizar(s: String): String =
        java.text.Normalizer
            .normalize(
                s,
                java.text.Normalizer.Form.NFD
            )
            .replace(Regex("\\p{M}"), "")
            .lowercase()
            .trim()

    private fun normalizarFecha(texto: String): String {

        val limpio = texto.trim()

        if (Regex("""^\d{4}-\d{2}-\d{2}$""").matches(limpio)) {
            return limpio
        }

        val partes = limpio.split("/", "-", ".")

        if (partes.size != 3) return ""

        val numeros = mutableListOf<Int>()

        for (p in partes) {
            val n = p.trim().toIntOrNull() ?: return ""
            numeros.add(n)
        }

        val (dia, mes, anio) = when {
            partes[0].trim().length == 4 ->
                Triple(numeros[2], numeros[1], numeros[0])

            partes[2].trim().length == 4 ->
                Triple(numeros[0], numeros[1], numeros[2])

            else -> return ""
        }

        if (mes !in 1..12 || dia !in 1..31) return ""

        val a = anio.toString().padStart(4, '0')
        val m = mes.toString().padStart(2, '0')
        val d = dia.toString().padStart(2, '0')

        return "$a-$m-$d"
    }

    /*
 * Convierte "aaaa-mm-dd" en "dd/mm/aaaa"
 * para mostrarla. Si no es ISO, la deja igual.
 */

    private fun formatearFechaParaMostrar(iso: String): String {

        if (!Regex("""^\d{4}-\d{2}-\d{2}$""").matches(iso)) {
            return iso
        }

        val partes = iso.split("-")

        return "${partes[2]}/${partes[1]}/${partes[0]}"
    }


    private fun mostrarCategoria(difficulty: String) {

        val categoryTasks =
            tasks.filter {
                it.difficulty == difficulty
            }

        val dialog = android.app.Dialog(this)

        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_category, null)

        val title =
            view.findViewById<TextView>(
                R.id.categoryTitle
            )

        val close =
            view.findViewById<TextView>(
                R.id.categoryClose
            )

        val container =
            view.findViewById<LinearLayout>(
                R.id.categoryTaskContainer
            )

        title.text =
            when (difficulty) {
                "Fácil" -> "♡  FÁCILES"
                "Media" -> "♡  MEDIAS"
                else -> "×  DIFÍCILES  ×"
            }

        if (categoryTasks.isEmpty()) {

            val emptyText = TextView(this)

            emptyText.text =
                "Todavía no tienes tareas aquí ♡"

            emptyText.textSize = 16f

            emptyText.setTextColor(
                getColor(R.color.soft_text_light)
            )

            emptyText.setPadding(
                10.dp(),
                30.dp(),
                10.dp(),
                30.dp()
            )

            container.addView(emptyText)

        } else {

            categoryTasks.forEach { task ->

                // CAMBIA: el requisito se resuelve una vez
                val requisito =
                    task.prerequisiteId?.let { id ->
                        tasks.firstOrNull { it.id == id }
                    }

                val taskContainer =
                    LinearLayout(this)

                taskContainer.orientation =
                    LinearLayout.VERTICAL

                taskContainer.layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                val taskRow =
                    LinearLayout(this)

                taskRow.orientation =
                    LinearLayout.HORIZONTAL

                taskRow.gravity =
                    Gravity.CENTER_VERTICAL

                val rowParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        64.dp()
                    )

                rowParams.setMargins(
                    0,
                    6.dp(),
                    0,
                    2.dp()
                )

                taskRow.layoutParams = rowParams

                val rowBackground =
                    GradientDrawable()

                rowBackground.setColor(
                    getColor(R.color.white)
                )

                rowBackground.cornerRadius =
                    18.dp().toFloat()

                taskRow.background =
                    rowBackground

                taskRow.setPadding(
                    8.dp(),
                    4.dp(),
                    6.dp(),
                    4.dp()
                )

                val checkBox =
                    CheckBox(this)

                checkBox.layoutParams =
                    LinearLayout.LayoutParams(
                        48.dp(),
                        48.dp()
                    )

                checkBox.isChecked =
                    task.completed

                taskRow.addView(checkBox)

                val taskName =
                    TextView(this)

                val nameParams =
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                nameParams.weight = 1f
                nameParams.marginStart = 4.dp()

                taskName.layoutParams =
                    nameParams

                // CAMBIA: requisito entre paréntesis
                taskName.text =
                    if (requisito != null)
                        "${task.name} (requiere: ${requisito.name})"
                    else
                        task.name

                taskName.textSize =
                    16f

                taskName.setTextColor(
                    getColor(R.color.soft_text)
                )

                taskName.maxLines = 3

                aplicarEstadoTarea(
                    taskName,
                    task.completed
                )

                taskRow.addView(taskName)

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
                    getColor(R.color.soft_coral)
                )

                deleteButton.background =
                    null

                deleteButton.contentDescription =
                    "Eliminar tarea"

                deleteButton.visibility =
                    if (task.completed)
                        View.VISIBLE
                    else
                        View.GONE

                taskRow.addView(deleteButton)

                checkBox.setOnClickListener {

                    if (task.completed) {

                        cambiarEstadoTarea(task, false) {
                            aplicarEstadoTarea(
                                taskName,
                                false
                            )
                            deleteButton.visibility = View.GONE
                        }
                        return@setOnClickListener
                    }

                    val requisitoCompletado = task.prerequisiteId?.let { id ->
                        tasks.firstOrNull { it.id == id }?.completed ?: false
                    } ?: true

                    if (task.prerequisiteId != null && !requisitoCompletado) {
                        checkBox.isChecked = false
                        AlertDialog.Builder(this)
                            .setTitle("Requisito pendiente")
                            .setMessage(
                                "\"${requisito?.name ?: "La tarea previa"}\" aún no está completa.\n\n" +
                                        "¿Seguro que quieres completar \"${task.name}\"?"
                            )
                            .setPositiveButton("Sí") { _, _ ->
                                cambiarEstadoTarea(task, true) {
                                    checkBox.isChecked = true
                                    aplicarEstadoTarea(taskName, true)
                                    deleteButton.visibility = View.VISIBLE
                                }
                            }
                            .setNegativeButton("No") { _, _ -> checkBox.isChecked = false }
                            .show()
                    } else {
                        cambiarEstadoTarea(task, true) {
                            checkBox.isChecked = true
                            aplicarEstadoTarea(taskName, true)
                            deleteButton.visibility = View.VISIBLE
                        }
                    }
                }

                deleteButton.setOnClickListener {

                    lifecycleScope.launch {
                        try {
                            repository.eliminar(task.id)
                            dialog.dismiss()
                            cargarTareas {
                                mostrarCategoria(difficulty)
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("TasksActivity", "Error al eliminar tarea", e)
                            android.widget.Toast.makeText(
                                this@TasksActivity,
                                "No se pudo eliminar la tarea",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                taskContainer.addView(
                    taskRow
                )

                if (task.academic) {

                    val academicText =
                        TextView(this)

                    academicText.text =
                        if (task.dueDate?.isNotEmpty() ?: false) {
                            "Trabajo académico · Entrega: " +
                                    formatearFechaParaMostrar(task.dueDate!!)
                        } else {
                            "Trabajo académico"
                        }


                    academicText.textSize =
                        12f

                    academicText.setTextColor(
                        getColor(R.color.soft_text_light)
                    )

                    academicText.setPadding(
                        56.dp(),
                        2.dp(),
                        10.dp(),
                        4.dp()
                    )

                    taskContainer.addView(
                        academicText
                    )
                }

                container.addView(
                    taskContainer
                )
            }
        }

        close.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)

        val window =
            dialog.window

        window?.setBackgroundDrawable(
            ColorDrawable(
                android.graphics.Color.TRANSPARENT
            )
        )

        window?.addFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND
        )

        val params =
            window?.attributes

        params?.dimAmount =
            0.55f

        window?.attributes =
            params

        dialog.setCancelable(true)

        dialog.show()

        window?.setLayout(
            (
                    resources.displayMetrics.widthPixels *
                            0.90
                    ).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun aplicarEstadoTarea(
        textView: TextView,
        completada: Boolean
    ) {

        if (completada) {

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

    private fun actualizarVista() {

        actualizarCategoria(
            "Fácil",
            easyPreview
        )

        actualizarCategoria(
            "Media",
            mediumPreview
        )

        actualizarCategoria(
            "Difícil",
            hardPreview
        )
    }

    private fun actualizarCategoria(
        difficulty: String,
        preview: TextView
    ) {

        val categoryTasks =
            tasks.filter {
                it.difficulty == difficulty
            }

        if (categoryTasks.isEmpty()) {

            preview.text =
                "Todavía no tienes tareas"

            return
        }

        val text =
            categoryTasks.joinToString("\n") { task ->

                val symbol =
                    if (task.completed)
                        "☑"
                    else
                        "□"

                val requisito =
                    task.prerequisiteId?.let { id ->
                        tasks.firstOrNull { it.id == id }
                    }

                val sufijo =
                    if (requisito != null)
                        " (requiere: ${requisito.name})"
                    else
                        ""

                "$symbol  ${task.name}$sufijo"
            }

        preview.text =
            text
    }

    private fun cambiarEstadoTarea(
        task: TaskUi,
        completada: Boolean,
        onSuccess: () -> Unit = {}
    ) {
        lifecycleScope.launch {
            try {
                repository.actualizarCompletada(task.id, completada)
                task.completed = completada
                onSuccess()
                actualizarVista()
            } catch (e: Exception) {
                android.util.Log.e("TasksActivity", "Error al actualizar tarea", e)
                android.widget.Toast.makeText(
                    this@TasksActivity,
                    "No se pudo actualizar la tarea",
                    android.widget.Toast.LENGTH_LONG
                ).show()
                cargarTareas()
            }
        }
    }

    private fun cargarTareas(onLoaded: (() -> Unit)? = null) {
        lifecycleScope.launch {
            try {
                val tareasFirestore = repository.listar()
                tasks.clear()
                tasks.addAll(tareasFirestore.map { tarea ->
                    TaskUi(
                        id = tarea.id,
                        name = tarea.nombre,
                        difficulty = tarea.dificultad,
                        academic = tarea.academica,
                        dueDate = tarea.fechaVencimiento.orEmpty(),
                        completed = tarea.completada,
                        prerequisiteId = tarea.prerequisiteId
                    )
                })
                actualizarVista()
                onLoaded?.invoke()
            } catch (e: Exception) {
                android.util.Log.e("TasksActivity", "Error al cargar tareas", e)
                android.widget.Toast.makeText(
                    this@TasksActivity,
                    "No se pudieron cargar las tareas",
                    android.widget.Toast.LENGTH_LONG
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
