package io.github.g00fy2.quickie

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by ily on 06-Jul-21.
 */
public class TestActivity:AppCompatActivity() {

  private lateinit var qrScannerView: QRScannerView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    qrScannerView = QRScannerView(this)
    setContentView(qrScannerView)
    qrScannerView.startCamera(this)
  }

}