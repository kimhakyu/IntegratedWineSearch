package com.example.integratedwinesearch

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.material.button.MaterialButton
import androidx.fragment.app.Fragment
import com.example.integratedwinesearch.ai.OcrRepository
import java.io.File
import java.io.FileOutputStream

class AiFragment : Fragment(R.layout.fragment_ai) {

    private lateinit var btnTakePhoto: MaterialButton
    private lateinit var btnPickFromGallery: MaterialButton
    private val ocrRepository = OcrRepository()
    private var cameraImageUri: Uri? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            Toast.makeText(requireContext(), "촬영이 취소되었거나 실패했습니다.", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }
        val uri = cameraImageUri ?: return@registerForActivityResult
        val imageFile = uriToTempFile(uri)
        if (imageFile == null) {
            Toast.makeText(requireContext(), "촬영 이미지 파일 처리에 실패했습니다.", Toast.LENGTH_LONG).show()
            return@registerForActivityResult
        }
        uploadImage(compressImageIfNeeded(imageFile))
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            Toast.makeText(requireContext(), "이미지 선택이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }
        val imageFile = uriToTempFile(uri)
        if (imageFile == null) {
            Toast.makeText(requireContext(), "선택한 이미지 파일 처리에 실패했습니다.", Toast.LENGTH_LONG).show()
            return@registerForActivityResult
        }
        uploadImage(compressImageIfNeeded(imageFile))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnTakePhoto = view.findViewById(R.id.btnTakePhoto)
        btnPickFromGallery = view.findViewById(R.id.btnPickFromGallery)

        btnTakePhoto.setOnClickListener {
            ensureCameraPermissionAndLaunch()
        }

        btnPickFromGallery.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun uploadImage(imageFile: File) {
        setLoading(true)
        ocrRepository.searchWineByImage(imageFile) { success, results, error ->
            if (!isAdded) return@searchWineByImage
            requireActivity().runOnUiThread {
                setLoading(false)
                if (success) {
                    val firstTitle = results.firstOrNull()?.title.orEmpty()
                    val keyword = buildSearchKeyword(firstTitle)
                    if (keyword.isBlank()) {
                        Toast.makeText(requireContext(), "OCR 결과가 없습니다.", Toast.LENGTH_LONG).show()
                    } else {
                        (activity as? MainActivity)?.openSearchWithQuery(keyword)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        error ?: "OCR 요청 실패(서버 주소/네트워크 확인 필요)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun compressImageIfNeeded(sourceFile: File): File {
        return try {
            if (sourceFile.length() <= 2_000_000L) return sourceFile

            val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath) ?: return sourceFile
            val resized = resizeBitmap(bitmap, 1600)
            val target = File.createTempFile("upload_compressed_", ".jpg", sourceFile.parentFile)
            FileOutputStream(target).use { out ->
                resized.compress(Bitmap.CompressFormat.JPEG, 82, out)
            }
            if (resized != bitmap) resized.recycle()
            bitmap.recycle()
            target
        } catch (_: Exception) {
            sourceFile
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSide: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val largest = maxOf(w, h)
        if (largest <= maxSide) return bitmap

        val scale = maxSide.toFloat() / largest.toFloat()
        val nw = (w * scale).toInt()
        val nh = (h * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, nw, nh, true)
    }

    private fun ensureCameraPermissionAndLaunch() {
        val granted = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        try {
            val imageFile = createCameraTempFile()
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                imageFile
            )
            cameraImageUri = uri
            takePictureLauncher.launch(uri)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "카메라 실행 실패: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun buildSearchKeyword(rawTitle: String): String {
        if (rawTitle.isBlank()) return ""
        val beforeDash = rawTitle.split(" - ").firstOrNull().orEmpty()
        val cleaned = beforeDash
            .replace("...", "")
            .replace("…", "")
            .replace(Regex("\\b(19|20)\\d{2}\\b"), "")
            .replace(Regex("[^\\p{L}\\p{N}\\s-]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        val tokens = cleaned.split(" ").filter { it.isNotBlank() }
        return tokens.take(3).joinToString(" ").trim()
    }

    private fun setLoading(loading: Boolean) {
        btnTakePhoto.isEnabled = !loading
        btnPickFromGallery.isEnabled = !loading
        btnTakePhoto.text = if (loading) "분석 중..." else "카메라로 촬영하기"
    }

    private fun createCameraTempFile(): File {
        val dir = File(requireContext().cacheDir, "images").apply { mkdirs() }
        return File.createTempFile("camera_", ".jpg", dir)
    }

    private fun uriToTempFile(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val dir = File(requireContext().cacheDir, "images").apply { mkdirs() }
            val target = File.createTempFile("upload_", ".jpg", dir)
            inputStream.use { input ->
                FileOutputStream(target).use { output ->
                    input.copyTo(output)
                }
            }
            target
        } catch (_: Exception) {
            null
        }
    }
}
