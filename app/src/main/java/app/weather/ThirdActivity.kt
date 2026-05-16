package app.weather

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.weather.WeatherUtils
import eightbitlab.com.blurview.BlurView

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val prefs = getSharedPreferences("app_cache", MODE_PRIVATE)

        val nextBtn = findViewById<Button>(R.id.nextBtn)
        val tempMedia = findViewById<LinearLayout>(R.id.tempMedia)
        val outfit = findViewById<androidx.cardview.widget.CardView>(R.id.outfit)
        val topTxt = findViewById<TextView>(R.id.textView)
        val tempTxt = findViewById<TextView>(R.id.windTitleTxt)

        val noShirt = findViewById<ImageView>(R.id.noShirt)
        val jacket = findViewById<ImageView>(R.id.jacket)
        val hoodie = findViewById<ImageView>(R.id.hoodie)
        val tshirt = findViewById<ImageView>(R.id.tshirt)
        val pants = findViewById<ImageView>(R.id.pants)

        val jTxt = findViewById<TextView>(R.id.jTxt)
        val hTxt = findViewById<TextView>(R.id.hTxt)
        val tTxt = findViewById<TextView>(R.id.tTxt)

        val mainV = findViewById<LinearLayout>(R.id.main)
        val backView = findViewById<ImageView>(R.id.backView)
        backView.setImageResource(WeatherUtils.getBackgroundResource(this))


        val windowMetrics = windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds
        val screenWidth = bounds.width().toFloat()
        val screenHeight = bounds.height().toFloat()

        //blur
        val decorView = window.decorView
        val rootView = findViewById<ViewGroup>(R.id.main)

        val radius = 7f

        val blurViewOutfit = findViewById<BlurView>(R.id.blurViewOutfit)
        val blurViewBtn = findViewById<BlurView>(R.id.blurView)
        val blurViewSkip = findViewById<BlurView>(R.id.blurViewSkip)

        val allBlurViews = listOf(blurViewOutfit, blurViewBtn, blurViewSkip)

        for (blurView in allBlurViews) {
            blurView.setupWith(rootView) // Or use RenderEffectBlur for API 31+
                .setBlurAutoUpdate(true)
                .setBlurRadius(radius)

            // This makes the blur respect the CardView's rounded corners
            blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
            blurView.clipToOutline = true
        }

        outfit.translationX = screenWidth
        outfit.scaleX = 0f
        outfit.scaleY = 0f
        outfit.animate().scaleX(1f).scaleY(1f).setDuration(350).start()
        outfit.animate().translationX(0f).setDuration(350).start()
        tempMedia.translationX = screenWidth
        tempMedia.scaleX = 0f
        tempMedia.scaleY = 0f
        tempMedia.animate().scaleX(1f).scaleY(1f).setDuration(350).start()
        tempMedia.animate().translationX(0f).setDuration(350).start()

        var temp = prefs.getFloat("currentTemp", 0f)
        var temptxt = prefs.getFloat("currentTemp", 0f).toInt().toString()
        temptxt = temptxt + "°"
        tempTxt.text = temptxt

        noShirt.visibility = View.GONE
        when{
            temp > 31f -> {jacket.visibility = View.GONE ; jTxt.visibility = View.GONE ; hoodie.visibility = View.GONE ; hTxt.visibility = View.GONE ; tshirt.visibility = View.GONE ; noShirt.visibility = View.VISIBLE}
            temp > 24f -> {jacket.visibility = View.GONE ; jTxt.visibility = View.GONE ; hoodie.visibility = View.GONE ; hTxt.visibility = View.GONE ;}
            temp > 16f -> {jacket.visibility = View.GONE ; jTxt.visibility = View.GONE}
            else -> noShirt.visibility = View.GONE
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
            outfit.animate().scaleX(0f).scaleY(0f).setDuration(350).start()
            tempMedia.animate().scaleX(0f).scaleY(0f).setDuration(350).start()
            outfit.animate().alpha(0f).setDuration(250).start()
            tempMedia.animate().alpha(0f).setDuration(250).withEndAction {
                val fadeOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
                val nextPage = Intent(this, FullActivity::class.java)
                startActivity(nextPage, fadeOptions.toBundle())
            }.start()
        }

        extra.setOnClickListener {
            outfit.animate().scaleX(0f).scaleY(0f).setDuration(350).start()
            tempMedia.animate().scaleX(0f).scaleY(0f).setDuration(350).start()
            outfit.animate().alpha(0f).setDuration(250).start()
            tempMedia.animate().alpha(0f).setDuration(250).withEndAction {
                val fadeOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
                val nextPage = Intent(this, FullActivity::class.java)
                startActivity(nextPage, fadeOptions.toBundle())
            }.start()
        }
    }
}
