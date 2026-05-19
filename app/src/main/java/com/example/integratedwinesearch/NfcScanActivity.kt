package com.example.integratedwinesearch

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.NfcA
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.nfc.tech.Ndef
import com.google.android.material.button.MaterialButton

class NfcScanActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_RETURN_RESULT = "extra_return_result"
        const val EXTRA_SCAN_COMPLETED = "extra_scan_completed"

        fun createIntent(context: Context, returnResult: Boolean = false): Intent {
            return Intent(context, NfcScanActivity::class.java).apply {
                putExtra(EXTRA_RETURN_RESULT, returnResult)
            }
        }
    }

    private enum class ScanState { READY, SCANNING, SUCCESS }

    private val handler = Handler(Looper.getMainLooper())
    private var pulseAnimator: ObjectAnimator? = null
    private var state: ScanState = ScanState.READY
    private var isReaderEnabled = false
    private var nfcAdapter: NfcAdapter? = null

    private lateinit var topBar: View
    private lateinit var scanIconCircle: View
    private lateinit var ivScanIcon: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnStart: MaterialButton
    private lateinit var tipsContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_scan)

        topBar = findViewById(R.id.topBar)
        scanIconCircle = findViewById(R.id.scanIconCircle)
        ivScanIcon = findViewById(R.id.ivScanIcon)
        tvTitle = findViewById(R.id.tvNfcMainTitle)
        tvDescription = findViewById(R.id.tvNfcDescription)
        tvStatus = findViewById(R.id.tvNfcStatus)
        btnStart = findViewById(R.id.btnStartScan)
        tipsContainer = findViewById(R.id.layoutTips)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        applyStatusBarInset()
        renderState()

        btnStart.setOnClickListener {
            if (state == ScanState.READY) {
                if (nfcAdapter == null) {
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = "이 기기는 NFC를 지원하지 않습니다."
                    return@setOnClickListener
                }
                if (nfcAdapter?.isEnabled != true) {
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = "NFC를 켠 뒤 다시 시도해주세요."
                    return@setOnClickListener
                }
                state = ScanState.SCANNING
                renderState()
                enableReaderModeIfNeeded()
            }
        }
    }

    private fun applyStatusBarInset() {
        val baseTop = topBar.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(topBar) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(view.paddingLeft, baseTop + topInset, view.paddingRight, view.paddingBottom)
            insets
        }
    }

    private fun startScanningAnimation() {
        stopPulse()
        pulseAnimator = ObjectAnimator.ofFloat(scanIconCircle, View.ALPHA, 1f, 0.5f, 1f).apply {
            duration = 800L
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    private fun maybeFinishAfterSuccess() {
        val shouldReturn = intent.getBooleanExtra(EXTRA_RETURN_RESULT, false)
        if (shouldReturn) {
            handler.postDelayed({ finish() }, 900L)
        }
    }

    private fun renderState() {
        when (state) {
            ScanState.READY -> {
                stopPulse()
                scanIconCircle.setBackgroundResource(R.drawable.bg_circle_red)
                ivScanIcon.setImageResource(R.drawable.ic_smartphone_red)
                ivScanIcon.imageTintList = getColorStateList(android.R.color.white)
                tvTitle.text = "와인에 스마트폰을 가까이 대세요"
                tvDescription.text = "NFC 태그가 부착된 와인 병에 스마트폰 뒷면을 가까이 대면 자동으로 정보를 읽어옵니다."
                tvStatus.visibility = View.GONE
                btnStart.visibility = View.VISIBLE
                btnStart.text = "스캔 시작"
                tipsContainer.visibility = View.VISIBLE
            }

            ScanState.SCANNING -> {
                startScanningAnimation()
                scanIconCircle.setBackgroundResource(R.drawable.bg_circle_red)
                ivScanIcon.setImageResource(R.drawable.ic_smartphone_red)
                ivScanIcon.imageTintList = getColorStateList(android.R.color.white)
                tvTitle.text = "스캔 중..."
                tvDescription.text = "NFC 태그를 읽는 중입니다. 잠시만 기다려주세요."
                tvStatus.visibility = View.VISIBLE
                tvStatus.text = "≋ NFC 태그 감지 중..."
                btnStart.visibility = View.GONE
                tipsContainer.visibility = View.GONE
            }

            ScanState.SUCCESS -> {
                stopPulse()
                scanIconCircle.setBackgroundResource(R.drawable.bg_nfc_success_circle)
                ivScanIcon.setImageResource(R.drawable.ic_nfc_check)
                ivScanIcon.imageTintList = getColorStateList(android.R.color.white)
                tvTitle.text = "스캔 완료!"
                tvDescription.text = "와인 정보를 불러오는 중..."
                tvStatus.visibility = View.VISIBLE
                tvStatus.text = "● ● ●"
                btnStart.visibility = View.GONE
                tipsContainer.visibility = View.GONE
            }
        }
    }

    private fun stopPulse() {
        pulseAnimator?.cancel()
        pulseAnimator = null
        scanIconCircle.alpha = 1f
    }

    private fun enableReaderModeIfNeeded() {
        if (isReaderEnabled) return
        val adapter = nfcAdapter ?: return
        val flags = NfcAdapter.FLAG_READER_NFC_A or
            NfcAdapter.FLAG_READER_NFC_B or
            NfcAdapter.FLAG_READER_NFC_F or
            NfcAdapter.FLAG_READER_NFC_V or
            NfcAdapter.FLAG_READER_NFC_BARCODE or
            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
        adapter.enableReaderMode(this, readerCallback, flags, null)
        isReaderEnabled = true
    }

    private fun disableReaderModeIfNeeded() {
        if (!isReaderEnabled) return
        nfcAdapter?.disableReaderMode(this)
        isReaderEnabled = false
    }

    private val readerCallback = NfcAdapter.ReaderCallback { tag ->
        val tagIdHex = tag.id.toHexString()
        val reversedHex = tag.id.reversedArray().toHexString()
        val tagIdDec = tag.id.toUnsignedLongDecimal()
        Log.d(
            "NfcScan",
            "NFC tag detected, id_hex=$tagIdHex, id_hex_reversed=$reversedHex, id_dec=$tagIdDec, tech=${tag.techList.joinToString()}"
        )
        logTechDetails(tag)

        val ndefPayload = readNdefPayload(tag)
        if (ndefPayload != null) {
            Log.d("NfcScan", "NFC NDEF payload=$ndefPayload")
        } else {
            Log.d("NfcScan", "NFC NDEF payload not found (현재 태그는 NDEF 미기록 가능성)")
        }

        runOnUiThread {
            disableReaderModeIfNeeded()
            state = ScanState.SUCCESS
            renderState()
            Log.d("NfcScan", "NFC scan completed (real tag)")
            setResult(RESULT_OK, Intent().putExtra(EXTRA_SCAN_COMPLETED, true))
            maybeFinishAfterSuccess()
        }
    }

    private fun readNdefPayload(tag: Tag): String? {
        val ndef = Ndef.get(tag) ?: return null
        return try {
            ndef.connect()
            val msg: NdefMessage = ndef.ndefMessage ?: return null
            val record = msg.records.firstOrNull() ?: return null
            parseRecord(record)
        } catch (e: Exception) {
            Log.e("NfcScan", "readNdefPayload failed: ${e.message}", e)
            null
        } finally {
            try {
                if (ndef.isConnected) ndef.close()
            } catch (_: Exception) {
            }
        }
    }

    private fun parseRecord(record: NdefRecord): String {
        val payload = record.payload ?: return ""
        return try {
            String(payload, Charsets.UTF_8)
        } catch (_: Exception) {
            payload.toHexString()
        }
    }

    private fun ByteArray.toHexString(): String = joinToString("") { "%02X".format(it) }

    private fun ByteArray.toUnsignedLongDecimal(): String {
        var value = 0UL
        forEachIndexed { index, byte ->
            value = value or ((byte.toUByte().toULong()) shl (8 * index))
        }
        return value.toString()
    }

    private fun logTechDetails(tag: Tag) {
        try {
            NfcA.get(tag)?.let { nfcA ->
                val atqa = nfcA.atqa?.toHexString().orEmpty()
                val sak = nfcA.sak
                Log.d("NfcScan", "NfcA detail: atqa=$atqa, sak=$sak, maxTransceive=${nfcA.maxTransceiveLength}")
            }
        } catch (e: Exception) {
            Log.e("NfcScan", "NfcA detail read failed: ${e.message}")
        }

        try {
            MifareClassic.get(tag)?.let { mfc ->
                Log.d(
                    "NfcScan",
                    "MifareClassic detail: type=${mfc.type}, size=${mfc.size}, sectorCount=${mfc.sectorCount}, blockCount=${mfc.blockCount}"
                )
            }
        } catch (e: Exception) {
            Log.e("NfcScan", "MifareClassic detail read failed: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        if (state == ScanState.SCANNING) {
            enableReaderModeIfNeeded()
        }
    }

    override fun onPause() {
        super.onPause()
        disableReaderModeIfNeeded()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPulse()
        disableReaderModeIfNeeded()
        handler.removeCallbacksAndMessages(null)
    }
}
