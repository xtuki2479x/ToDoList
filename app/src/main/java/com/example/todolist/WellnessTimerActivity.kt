package com.example.todolist

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WellnessTimerActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var startTimerText: TextView

    private var countDownTimer: CountDownTimer? = null
    private var selectedMinutes = 5
    private var remainingMillis = 0L
    private var timerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wellness_timer)

        val selectedPalette =
            PaletteManager.getSelectedPalette(this)

        PaletteManager.applyPalette(
            findViewById(android.R.id.content),
            selectedPalette
        )

        timerText = findViewById(R.id.timerText)
        startTimerText = findViewById(R.id.startTimerText)

        findViewById<View>(R.id.backButton).setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        findViewById<View>(R.id.timer5).setOnClickListener {
            seleccionarTiempo(5)
        }

        findViewById<View>(R.id.timer10).setOnClickListener {
            seleccionarTiempo(10)
        }

        findViewById<View>(R.id.timer15).setOnClickListener {
            seleccionarTiempo(15)
        }

        findViewById<View>(R.id.timer30).setOnClickListener {
            seleccionarTiempo(30)
        }

        findViewById<View>(R.id.startTimerButton).setOnClickListener {
            if (timerRunning) {
                pausarTemporizador()
            } else {
                iniciarTemporizador()
            }
        }

        findViewById<View>(R.id.resetTimerButton).setOnClickListener {
            reiniciarTemporizador()
        }

        seleccionarTiempo(5)
    }

    private fun seleccionarTiempo(minutos: Int) {
        if (timerRunning) return

        selectedMinutes = minutos
        remainingMillis = minutos * 60 * 1000L
        actualizarTiempo()
    }

    private fun iniciarTemporizador() {

        if (remainingMillis <= 0L) {
            remainingMillis = selectedMinutes * 60 * 1000L
        }

        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(remainingMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                actualizarTiempo()
            }

            override fun onFinish() {
                remainingMillis = 0L
                timerRunning = false
                actualizarTiempo()
                startTimerText.text = "▶  Iniciar"
            }

        }.start()

        timerRunning = true
        startTimerText.text = "Ⅱ  Pausar"
    }

    private fun pausarTemporizador() {
        countDownTimer?.cancel()
        timerRunning = false
        startTimerText.text = "▶  Continuar"
    }

    private fun reiniciarTemporizador() {
        countDownTimer?.cancel()
        timerRunning = false
        remainingMillis = selectedMinutes * 60 * 1000L
        actualizarTiempo()
        startTimerText.text = "▶  Iniciar"
    }

    private fun actualizarTiempo() {
        val totalSeconds = remainingMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        timerText.text = String.format(
            "%02d:%02d",
            minutes,
            seconds
        )
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}