package io.github.kqakqakqa.tvbrowser

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    private var readyToQuit = false
    private fun doubleBackToQuit() {
        if (readyToQuit) {
            finish()
            return
        }
        Toast.makeText(this@MainActivity, "再按一次退出", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            readyToQuit = true
        }, 2000)
    }

    private fun dontQuit() {
        readyToQuit = false
    }

    private fun getScript(): String {
        val assetManager = this.assets
        val inputStream = assetManager.open("script.js")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val script = String(buffer, Charsets.UTF_8)
        return script
    }

    private fun WebView.initialize() {
        settings.javaScriptEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.userAgentString =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36"
        settings.mediaPlaybackRequiresUserGesture = false
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
    }

    private fun WebView.applyScript(script: String) {
        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                evaluateJavascript(script, null)
                super.onPageStarted(view, url, favicon)
            }
        }
    }

//    fun messageFromWebview(string: String) {
//        val webviewMessage = JSONObject(string)
//        when (webviewMessage.getString("name")) {
//            "dontQuit" -> {
//                dontQuit = true
//            }
//
//            "gotoChannel" -> {}
//        }
//    }

//    class WebAppInterface(private val activity: MainActivity) {
//        @JavascriptInterface
//        fun sendWebviewMessage(string: String) {
//            activity.messageFromWebview(string)
//        }
//    }

    private lateinit var webViewBackground: WebView
    private lateinit var webViewTv1: WebView
    private lateinit var webViewMenu: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val script = getScript()

        webViewBackground = findViewById(R.id.webViewBackground)
        webViewBackground.initialize()
        webViewBackground.loadUrl("file:///android_asset/background.html")

        webViewTv1 = findViewById(R.id.webViewTv1)
        webViewTv1.initialize()
        webViewTv1.setBackgroundColor(Color.TRANSPARENT)
        webViewTv1.applyScript(script)
        webViewTv1.loadUrl("https://tv.cctv.com/live/cctv13")

        webViewMenu = findViewById(R.id.webViewMenu)
        webViewMenu.initialize()
        webViewMenu.setBackgroundColor(Color.TRANSPARENT)
        webViewMenu.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
//        val webAppInterface = WebAppInterface(this)
//        webViewMenu.addJavascriptInterface(webAppInterface, "android")
        webViewMenu.loadUrl("file:///android_asset/menu.html")

//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                webViewMenu.evaluateJavascript("javascript:pressBack()") { string ->
//                    when (string.substring(1, string.length - 1)) {
//                        "quit" -> doubleBackToQuit()
//                    }
//                }
//            }
//        })

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                webViewMenu.evaluateJavascript("javascript:pressArrowUp()", null)
                return true
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                webViewMenu.evaluateJavascript("javascript:pressArrowDown()", null)
                return true
            }

            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                webViewMenu.evaluateJavascript("javascript:pressOK()") { returnValue ->
                    when (val string = returnValue.substring(1, returnValue.length - 1)) {
                        "" -> return@evaluateJavascript
                        else -> {
                            webViewTv1.loadUrl(string)
                        }
                    }
                }
                return true
            }

            KeyEvent.KEYCODE_BACK -> {
                webViewMenu.evaluateJavascript("javascript:pressBack()") { returnValue ->
                    when (returnValue.substring(1, returnValue.length - 1)) {
                        "quit" -> doubleBackToQuit()
                        "dontQuit" -> dontQuit()
                    }
                }
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}

