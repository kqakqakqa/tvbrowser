package io.github.kqakqakqa.tvbrowser

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var readyToExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun WebView.initialize() {
            settings.javaScriptEnabled = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.userAgentString =
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36"
            settings.mediaPlaybackRequiresUserGesture = false
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
        }

        fun WebView.applyScript(script: String) {
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    evaluateJavascript(script, null)
                    super.onPageStarted(view, url, favicon)
                }
            }
        }

        fun getScript(): String {
            val assetManager = this.assets
            val inputStream = assetManager.open("script.js")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val script = String(buffer, Charsets.UTF_8)
            return script
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (readyToExit) {
                    finish()
                } else {
                    readyToExit = true
                    Toast.makeText(this@MainActivity, "再按一次退出", Toast.LENGTH_SHORT).show()

                    // 延迟重置标志
                    Handler(Looper.getMainLooper()).postDelayed({
                        readyToExit = false
                    }, 2000)
                }
            }
        })

        val script = getScript()

        val webViewBackground = findViewById<WebView>(R.id.webViewBackground)
        webViewBackground.initialize()
        webViewBackground.loadUrl("file:///android_asset/background.html")

        val webViewTv1 = findViewById<WebView>(R.id.webViewTv1)
        webViewTv1.initialize()
        webViewTv1.setBackgroundColor(Color.TRANSPARENT)
        webViewTv1.applyScript(script)
        webViewTv1.loadUrl("https://tv.cctv.com/live/cctv13")

        val webViewMenu = findViewById<WebView>(R.id.webViewMenu)
        webViewMenu.initialize()
        webViewMenu.setBackgroundColor(Color.TRANSPARENT)
//        webViewMenu.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
        webViewMenu.loadUrl("file:///android_asset/menu.html")
    }
}

