package app.weather

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import app.weather.api.Daily
import app.weather.WeatherUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eightbitlab.com.blurview.BlurView

class SettingsActivity: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val rootView = findViewById<ViewGroup>(R.id.main)

        val backView = findViewById<ImageView>(R.id.backView)
        backView.setImageResource(WeatherUtils.getBackgroundResource(this))


        val decorView = window.decorView
        val blurViewDays = findViewById<BlurView>(R.id.blurViewDays)
        val blurViewBack = findViewById<BlurView>(R.id.blurViewBack)
        val radius = 10f

        val allBlurViews = listOf(
            blurViewDays,
            blurViewBack
        )
        for (blurView in allBlurViews) {
            blurView.setupWith(rootView) // Or use RenderEffectBlur for API 31+
                .setBlurAutoUpdate(true)
                .setBlurRadius(radius)

            // This makes the blur respect the CardView's rounded corners
            blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
            blurView.clipToOutline = true
        }
        val extra = findViewById<CardView>(R.id.extra)

        extra.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.9f).scaleY(0.8f).setDuration(50).start()
                }
                MotionEvent.ACTION_UP -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(250).start()
                }
            }
            false
        }
        extra.setOnClickListener {
            val fadeOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            val nextPage = Intent(this, FullActivity::class.java)
            startActivity(nextPage, fadeOptions.toBundle())
        }
    }
}