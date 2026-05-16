package app.weather

import android.app.ActivityOptions
import android.content.Intent
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

class WindActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wind)
        val prefs = getSharedPreferences("app_cache", MODE_PRIVATE)
        val windMetrics = prefs.getFloat("wind", 0f)

        val backView = findViewById<ImageView>(R.id.backView)
        backView.setImageResource(WeatherUtils.getBackgroundResource(this))


        val nextBtn = findViewById<Button>(R.id.nextBtn)
        val wind = findViewById<LinearLayout>(R.id.wind)
        val windTxt = findViewById<TextView>(R.id.windTxt)

        val mainV = findViewById<LinearLayout>(R.id.main)

        val windowMetrics = windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds
        val screenWidth = bounds.width().toFloat()
        val screenHeight = bounds.height().toFloat()

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

        when {
            windMetrics < 1.6 -> windTxt.text = "Niente vento oggi\uD83E\uDD29"
            windMetrics < 4.2 -> windTxt.text = "Oggi c'è un po' di vento ma niente di esagerato\uD83D\uDE11"
            windMetrics < 9.7 -> windTxt.text = "Oggi ci sarà parecchio vento quindi copriti\uD83D\uDE20"
            windMetrics < 25 -> windTxt.text = "è previsto parecchio vento oggi\n buona fortuna\uD83D\uDE0A"
            else -> windTxt.text = "tempesta in arrivo..."
        }

        wind.translationX = screenWidth
        wind.scaleX = 0f
        wind.scaleY = 0f
        wind.animate().scaleX(1f).scaleY(1f).setDuration(400).start()
        wind.animate().translationX(0f).setDuration(350).start()

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
            wind.animate().scaleX(0f).scaleY(0f).setDuration(400).start()
            wind.animate().translationX(-screenWidth).setDuration(250).withEndAction {
                val fadeOptions = ActivityOptions.makeCustomAnimation(this, R.anim.no_fade, R.anim.no_fade)
                val nextPage = Intent(this, FullActivity::class.java)
                startActivity(nextPage, fadeOptions.toBundle())
            }.start()
        }

        extra.setOnClickListener {
            wind.animate().scaleX(0f).scaleY(0f).setDuration(400).start()
            wind.animate().translationX(-screenWidth).setDuration(250).withEndAction {
                val fadeOptions = ActivityOptions.makeCustomAnimation(this, R.anim.no_fade, R.anim.no_fade)
                val nextPage = Intent(this, ThirdActivity::class.java)
                startActivity(nextPage, fadeOptions.toBundle())
            }.start()
        }
    }
}
