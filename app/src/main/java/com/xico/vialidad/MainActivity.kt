package com.xico.vialidad
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.webkit.*
import android.widget.Toast
import android.view.View
import android.widget.Button
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
  private lateinit var webView: WebView
  @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val splash = findViewById<View>(R.id.splashView)
    val btnEntrar = findViewById<Button>(R.id.btnEntrar)
    val entrar = { splash.visibility = View.GONE }
    splash.setOnClickListener { entrar() }
    btnEntrar.setOnClickListener { entrar() }
    Handler(Looper.getMainLooper()).postDelayed({ if (splash.visibility == View.VISIBLE) entrar() }, 4000)
    webView = findViewById(R.id.webView)
    webView.settings.apply {
      javaScriptEnabled = true
      domStorageEnabled = true
      databaseEnabled = true
      allowFileAccess = true
      allowContentAccess = true
      loadWithOverviewMode = true
      useWideViewPort = true
      builtInZoomControls = false
      cacheMode = WebSettings.LOAD_DEFAULT
    }
    // Guardar localStorage aunque se cierre la app: WebView lo hace solo con domStorageEnabled
    webView.webViewClient = WebViewClient()
    webView.webChromeClient = WebChromeClient()
    // Puente para descargar Excel / CSV / JSON desde el HTML sin navegador
    webView.addJavascriptInterface(AndroidBridge(), "AndroidBridge")
    // Interceptar descargas de blob: el HTML genera blob, lo convertimos a base64 vía JS
    webView.setDownloadListener { url, _, _, _, _ ->
      if (url.startsWith("blob:")) {
        // Pedir al JS que lea el blob y lo pase al puente - el HTML ya expone window.descargarAPK si se usa
        Toast.makeText(this, "Descargando archivo...", Toast.LENGTH_SHORT).show()
      } else {
        try { startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))) } catch(e: Exception) {}
      }
    }
    if (Build.VERSION.SDK_INT <= 32) {
      ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
    }
    // Cargar tu sistema desde assets
    webView.loadUrl("file:///android_asset/index.html")
  }
  override fun onBackPressed() {
    if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
  }
  inner class AndroidBridge {
    @JavascriptInterface
    fun saveFile(fileName: String, base64: String, mime: String) {
      try {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use { it.write(bytes) }
        runOnUiThread { Toast.makeText(this@MainActivity, "Guardado en Descargas: $fileName", Toast.LENGTH_LONG).show() }
        // Avisar al sistema para que aparezca en Archivos
        sendBroadcast(android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
      } catch(e: Exception) {
        runOnUiThread { Toast.makeText(this@MainActivity, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show() }
      }
    }
    @JavascriptInterface
    fun showToast(msg: String) { runOnUiThread { Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show() } }
  }
}
