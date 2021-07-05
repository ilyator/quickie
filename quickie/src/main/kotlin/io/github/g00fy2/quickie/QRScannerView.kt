package io.github.g00fy2.quickie

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.widget.FrameLayout
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.Barcode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

public class QRScannerView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

  private val previewView = PreviewView(context).apply {
    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
  }
/*  private val overlayView: QROverlayView = QROverlayView(context).apply {
    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
  }*/

  private lateinit var analysisExecutor: ExecutorService

  private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(context) }
  private val imageAnalysis by lazy {
    ImageAnalysis.Builder()
      .setTargetResolution(Size(1280, 720))//TODO: check on other project
      .build()
      .also {
        it.setAnalyzer(analysisExecutor,
          QRCodeAnalyzer(
            barcodeFormats = intArrayOf(Barcode.FORMAT_ALL_FORMATS),
            onSuccess = { barcode ->
              //it.clearAnalyzer()
              Toast.makeText(context, barcode.rawValue, Toast.LENGTH_SHORT).show()
              //onSuccess(barcode)
            },
            onFailure = { exception ->
              Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show()/*onFailure(exception)*/
            },
            onPassCompleted = { _ ->
              //Log.d("failure", failureOccurred)
              //Toast.makeText(context, failureOccurred.toString(), Toast.LENGTH_LONG).show()
            /*onPassCompleted(failureOccurred)*/
            }
          )
        )
      }
  }
  private var cameraProvider: ProcessCameraProvider? = null

  init {
    addView(previewView)
    //addView(overlayView)
    analysisExecutor = Executors.newSingleThreadExecutor()
  }

  public fun startCamera(lifecycleOwner: LifecycleOwner) {
    if (cameraProvider == null) {
      cameraProviderFuture.addListener({
        cameraProvider = cameraProviderFuture.get()
        assignUseCases(lifecycleOwner)
      }, ContextCompat.getMainExecutor(context))
    }
  }

  private fun assignUseCases(lifecycleOwner: LifecycleOwner) {
    val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

    cameraProvider?.unbindAll()
    try {
      cameraProvider?.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
    } catch (e: Exception) {
      //onFailure(e)
    }
  }

  public fun stopCamera() {
    cameraProvider?.unbindAll()
  }

}