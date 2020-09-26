package dev.fukata.stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentTimeView = findViewById<TextView>(R.id.currentTime)
        var timer: Timer? = null
        var startTimestamp: Long = 0

        findViewById<Button>(R.id.start_stop).setOnClickListener(View.OnClickListener { v ->
            Log.d("MainActivity", "START / STOP")
            if (timer != null) {
                timer!!.cancel()
                timer = null
            } else {
                timer = Timer()
                startTimestamp = Date().time
                timer!!.scheduleAtFixedRate(object: TimerTask() {
                    override fun run() {
                        val progressTimestamp = Date().time - startTimestamp
                        Handler(Looper.getMainLooper()).postDelayed({
                            currentTimeView.setText(progressTimestamp.toString())
                        }, 0)
                    }
                }, 0, 1)
            }
        })
    }
}