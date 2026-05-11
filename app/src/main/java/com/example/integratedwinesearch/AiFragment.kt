package com.example.integratedwinesearch

import android.os.Bundle
import android.view.View
import com.google.android.material.button.MaterialButton
import androidx.fragment.app.Fragment

class AiFragment : Fragment(R.layout.fragment_ai) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnTakePhoto: MaterialButton = view.findViewById(R.id.btnTakePhoto)
        btnTakePhoto.setOnClickListener {
            // NFC 실제 연동 전까지는 샘플 값으로 상세 화면 모드 확인
            val intent = WineDetailActivity.createIntent(
                context = requireContext(),
                mode = WineDetailActivity.MODE_NFC,
                wineName = "샤토 마고 2015",
                wineType = "레드 와인",
                wineRegion = "프랑스 보르도",
                wineGrade = "A+",
                winePrice = 850000,
                wineImageUrl = null,
                temperature = 16.0f,
                humidity = 65
            )
            startActivity(intent)
        }
    }
}
