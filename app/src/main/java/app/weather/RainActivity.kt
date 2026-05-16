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
import androidx.appcompat.app.AppCompatActivity
import app.weather.WeatherUtils
import eightbitlab.com.blurview.BlurView

class RainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rain)
        val backView = findViewById<ImageView>(R.id.backView)
        backView.setImageResource(WeatherUtils.getBackgroundResource(this))

        val prefs = getSharedPreferences("app_cache", MODE_PRIVATE)
        val willRain = prefs.getBoolean("willRain", false)

        val nextBtn = findViewById<Button>(R.id.nextBtn)
        val rain = findViewById<LinearLayout>(R.id.pioggia)
        val rainTxt = findViewById<TextView>(R.id.raintTxt)
        val iconView = findViewById<ImageView>(R.id.iconView)
        val mainV = findViewById<LinearLayout>(R.id.main)

        if(willRain){rainTxt.text = "Oggi piove\n portati un ombrello!☂\uFE0F"; iconView.setImageResource(R.drawable.i09d)}
        else {rainTxt.text = "Oggi non piove quindi niente ombrello\uD83E\uDD73"}

        val windowMetrics = windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds
        val screenWidth = bounds.width().toFloat()
        val screenHeight = bounds.height().toFloat()
        rain.translationX = screenWidth
        rain.scaleX = 0f
        rain.scaleY = 0f
        rain.animate().scaleX(1f).scaleY(1f).setDuration(400).start()
        rain.animate().translationX(0f).setDuration(400).start()

        //blur
        val decorView = window.decorView
        val rootView = findViewById<ViewGroup>(R.id.main)
        val radius = 7f
        val blurView1 = findViewById<BlurView>(R.id.blurView1)
        val blurViewBtn = findViewById<BlurView>(R.id.blurView)
        val blurViewSkip = findViewById<BlurView>(R.id.blurViewSkip)
        val allBlurViews = listOf(blurView1, blurViewBtn, blurViewSkip)
        for (blurView in allBlurViews) {
            blurView.setupWith(rootView) // Or use RenderEffectBlur for API 31+
                .setBlurAutoUpdate(true)
                .setBlurRadius(radius)

            // This makes the blur respect the CardView's rounded corners
            blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
            blurView.clipToOutline = true
        }

        val extra = findViewById<androidx.cardview.widget.CardView>(R.id.extra)
        extra.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.8f).scaleY(0.9f).setDuration(50).start()
                }
                MotionEvent.ACTION_UP -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(250).start()
                }
            }
            false
        }
        extra.setOnClickListener {
            rain.animate().scaleX(0f).scaleY(0f).setDuration(400).start()
            rain.animate().translationX(-screenWidth).setDuration(250).withEndAction {
                val fadeOptions = ActivityOptions.makeCustomAnimation(this, R.anim.no_fade, R.anim.no_fade)
                val nextPage = Intent(this, WindActivity::class.java)
                startActivity(nextPage, fadeOptions.toBundle())
            }.start()
        }
        val skip = findViewById<androidx.cardview.widget.CardView>(R.id.skip)
        skip.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.8f).scaleY(0.9f).setDuration(50).start()
                }
                MotionEvent.ACTION_UP -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(250).start()
                }
            }
            false
        }
        skip.setOnClickListener {
            rain.animate().scaleX(0f).scaleY(0f).setDuration(400).start()
            rain.animate().translationX(-screenWidth).setDuration(250).withEndAction {
                val fadeOptions = ActivityOptions.makeCustomAnimation(this, R.anim.no_fade, R.anim.no_fade)
                val nextPage = Intent(this, FullActivity::class.java)
                startActivity(nextPage, fadeOptions.toBundle())
            }.start()
        }
    }
}
