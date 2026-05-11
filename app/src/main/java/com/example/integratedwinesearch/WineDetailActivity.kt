package com.example.integratedwinesearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.text.DecimalFormat

class WineDetailActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_MODE = "extra_mode"
        private const val EXTRA_WINE_NAME = "extra_wine_name"
        private const val EXTRA_WINE_TYPE = "extra_wine_type"
        private const val EXTRA_WINE_REGION = "extra_wine_region"
        private const val EXTRA_WINE_GRADE = "extra_wine_grade"
        private const val EXTRA_WINE_PRICE = "extra_wine_price"
        private const val EXTRA_WINE_IMAGE_URL = "extra_wine_image_url"
        private const val EXTRA_TEMPERATURE = "extra_temperature"
        private const val EXTRA_HUMIDITY = "extra_humidity"

        const val MODE_SEARCH = "SEARCH"
        const val MODE_NFC = "NFC"

        fun createIntent(
            context: Context,
            mode: String,
            wineName: String,
            wineType: String,
            wineRegion: String,
            wineGrade: String,
            winePrice: Int,
            wineImageUrl: String? = null,
            temperature: Float = 16.0f,
            humidity: Int = 65
        ): Intent {
            return Intent(context, WineDetailActivity::class.java).apply {
                putExtra(EXTRA_MODE, mode)
                putExtra(EXTRA_WINE_NAME, wineName)
                putExtra(EXTRA_WINE_TYPE, wineType)
                putExtra(EXTRA_WINE_REGION, wineRegion)
                putExtra(EXTRA_WINE_GRADE, wineGrade)
                putExtra(EXTRA_WINE_PRICE, winePrice)
                putExtra(EXTRA_WINE_IMAGE_URL, wineImageUrl)
                putExtra(EXTRA_TEMPERATURE, temperature)
                putExtra(EXTRA_HUMIDITY, humidity)
            }
        }
    }

    private val decimalFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wine_detail)
        applyStatusBarInset()

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val mode = intent.getStringExtra(EXTRA_MODE) ?: MODE_SEARCH
        val name = intent.getStringExtra(EXTRA_WINE_NAME).orEmpty()
        val type = intent.getStringExtra(EXTRA_WINE_TYPE).orEmpty()
        val region = intent.getStringExtra(EXTRA_WINE_REGION).orEmpty()
        val grade = intent.getStringExtra(EXTRA_WINE_GRADE).orEmpty()
        val price = intent.getIntExtra(EXTRA_WINE_PRICE, 0)
        val imageUrl = intent.getStringExtra(EXTRA_WINE_IMAGE_URL)
        val temperature = intent.getFloatExtra(EXTRA_TEMPERATURE, 16.0f)
        val humidity = intent.getIntExtra(EXTRA_HUMIDITY, 65)

        bindWineSummary(name, type, region, grade, price, imageUrl)
        bindModeSections(mode, temperature, humidity)
    }

    private fun applyStatusBarInset() {
        val topBar = findViewById<View>(R.id.topBar)
        val baseTop = topBar.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(topBar) { view, insets ->
            val statusBarTop = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(
                view.paddingLeft,
                baseTop + statusBarTop,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }
    }

    private fun bindWineSummary(
        name: String,
        type: String,
        region: String,
        grade: String,
        price: Int,
        imageUrl: String?
    ) {
        findViewById<TextView>(R.id.tvWineName).text = name
        findViewById<TextView>(R.id.tvWineType).text = type
        findViewById<TextView>(R.id.tvWineRegion).text = region
        findViewById<TextView>(R.id.tvWineGrade).text = grade
        findViewById<TextView>(R.id.tvWinePrice).text = "₩${decimalFormat.format(price)}"

        val ivWine = findViewById<ImageView>(R.id.ivWine)
        if (!imageUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.sample_wine_red)
                .error(R.drawable.sample_wine_red)
                .into(ivWine)
        } else {
            ivWine.setImageResource(R.drawable.sample_wine_red)
        }
    }

    private fun bindModeSections(mode: String, temperature: Float, humidity: Int) {
        val nfcPromptCard = findViewById<View>(R.id.cardNfcPrompt)
        val tempCard = findViewById<View>(R.id.cardTemp)
        val storageCard = findViewById<View>(R.id.cardStorageGrade)
        val historyCard = findViewById<View>(R.id.cardHistory)

        val isNfcMode = mode == MODE_NFC
        nfcPromptCard.visibility = if (isNfcMode) View.GONE else View.VISIBLE
        tempCard.visibility = if (isNfcMode) View.VISIBLE else View.GONE
        storageCard.visibility = if (isNfcMode) View.VISIBLE else View.GONE
        historyCard.visibility = if (isNfcMode) View.VISIBLE else View.GONE

        findViewById<TextView>(R.id.tvCurrentTemp).text = "현재 온도: ${"%.1f".format(temperature)}°C"
        findViewById<TextView>(R.id.tvCurrentHumidity).text = "습도: ${humidity}%"
    }
}
