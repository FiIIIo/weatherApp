package app.weather

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import app.weather.WeatherUtils
import eightbitlab.com.blurview.BlurView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainV = findViewById<ConstraintLayout>(R.id.main)
        val txt = findViewById<TextView>(R.id.txt)
        val prefs = getSharedPreferences("app_cache", MODE_PRIVATE)

        val backView = findViewById<ImageView>(R.id.backView)
        backView.setImageResource(WeatherUtils.getBackgroundResource(this))

        val extra = findViewById<androidx.cardview.widget.CardView>(R.id.extra) // Potential crash or lag        val nextBtn = findViewById<Button>(R.id.nextBtn)

        // Entrance animation
        txt.animate().alpha(.8f).setDuration(500).start()
        extra.animate().alpha(.8f).setDuration(500).start()

        // Blur setup
        val rootView = mainV
        val blurView = findViewById<BlurView>(R.id.blurView)
        val blurViewSkip = findViewById<BlurView>(R.id.blurViewSkip)

        val allBlurViews = listOf(blurView, blurViewSkip)

        for (bv in allBlurViews) {
            bv.setupWith(rootView)
                .setBlurAutoUpdate(false)
                .setBlurRadius(7f)

            bv.outlineProvider = ViewOutlineProvider.BACKGROUND
            bv.clipToOutline = true
        }

        val skip = findViewById<androidx.cardview.widget.CardView>(R.id.skip)

        val lat = prefs.getFloat("lat", 0f)
        val lon = prefs.getFloat("lon", 0f)

        // Touch animations
        extra.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    v.animate().scaleX(0.8f).scaleY(0.9f).setDuration(50).start()

                MotionEvent.ACTION_UP ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(250).start()
            }
            false
        }

        skip.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    v.animate().scaleX(0.8f).scaleY(0.9f).setDuration(50).start()

                MotionEvent.ACTION_UP ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(250).start()
            }
            false
        }

        skip.setOnClickListener {
            fadeAndOpen(FullActivity::class.java)
        }

        extra.setOnClickListener {
            fadeAndOpen(RainActivity::class.java)
        }
    }

    private fun fadeAndOpen(activity: Class<*>) {
        val txt = findViewById<TextView>(R.id.txt)
        txt.animate().translationY(-5000f).setDuration(400).withEndAction {
            val fadeOptions =
                ActivityOptions.makeCustomAnimation(this, R.anim.no_fade, R.anim.no_fade)
            val nextPage = Intent(this, activity)
            startActivity(nextPage, fadeOptions.toBundle())
        }.start()
    }
}