//package kz.aura.merp.employee.ui.activity
//
//import android.os.Build
//import android.os.Bundle
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import android.webkit.PermissionRequest
//import android.webkit.WebChromeClient
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.lifecycle.Observer
//import kz.aura.merp.employee.R
//import kz.aura.merp.employee.viewmodel.AuthViewModel
//import kz.aura.merp.employee.util.Constants.OCR_URL
//import kz.aura.merp.employee.util.Helpers.openActivityByPositionId
//import kz.aura.merp.employee.util.ProgressDialog
//import kotlinx.android.synthetic.main.activity_ocr_web.*
//
//class OcrWebActivity : AppCompatActivity() {
//
//    private val mAuthViewModel: AuthViewModel by viewModels()
//    private var transactionId: String? = null
//    private lateinit var progressDialog: ProgressDialog
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_ocr_web)
//        setSupportActionBar(toolbar as Toolbar)
//        supportActionBar?.title = "OCR"
//
//        // Get transactionId
//        transactionId = intent.getStringExtra("transactionId")
//
//        // Initialize Loading Dialog
//        progressDialog = ProgressDialog(this)
//
//        // Initialize OCR web view
//        ocr_web.settings.javaScriptEnabled = true
//        ocr_web.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView, url: String) {
//                ocr_progress_bar.visibility = View.INVISIBLE
//            }
//        }
//        ocr_web.webChromeClient = object : WebChromeClient() {
//            override fun onPermissionRequest(request: PermissionRequest) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    request.grant(request.resources)
//                }
//            }
//        }
//
//
//        ocr_web.loadUrl(OCR_URL + transactionId)
//
////        test.settings.javaScriptEnabled = true
////        test.addJavascriptInterface(object : Any() {
////            @JavascriptInterface // For API 17+
////            fun close() {
////                println("DWDWDWD")
////            }
////        }, "ocr")
////
////
////        test.loadUrl("file:///android_asset/index.html")
//
//        // Observe MutableLiveData
//        mAuthViewModel.positionId.observe(this, Observer { data ->
//            progressDialog.hideLoading() // hide loading
//            // Open Activity by position
//            openActivityByPositionId(this)
//        })
//        mAuthViewModel.onErrorToken.observe(this, Observer {
//            progressDialog.hideLoading() // hide loading
//            finish()
//        })
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.auth_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_close -> {
//                progressDialog.showLoading()
//                mAuthViewModel.fetchToken(transactionId!!)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        finish()
//        return true
//    }
//}