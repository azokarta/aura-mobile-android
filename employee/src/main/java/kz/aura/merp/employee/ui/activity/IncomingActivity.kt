package kz.aura.merp.employee.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityIncomingBinding
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.ui.dialog.TimePickerFragment
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


@AndroidEntryPoint
class IncomingActivity : AppCompatActivity(), TimePickerFragment.TimePickerListener {

    private lateinit var binding: ActivityIncomingBinding

    private lateinit var progressDialog: ProgressDialog
    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private lateinit var phoneNumber: String
    private var contractId: Long = 0L
    private var selectedHour: Int? = null
    private var selectedMinute: Int? = null
    private var countryCode: CountryCode = CountryCode.KZ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contractId = intent.getLongExtra("contractId", 0L)
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_incoming_call)

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        setupObservers()

        mFinanceViewModel.getCountryCode()

        binding.save.setOnClickListener(::save)

        binding.selectTimeBtn.setOnClickListener {
            showTimePicker()
        }
    }

    private fun save(view: View) {
        val description = binding.descriptionText.text.toString()
        val dtf: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
        val currentDate: String = dtf.print(DateTime.now())
        val typedPhoneNumber = binding.phoneNumberText.text.toString()

        if (typedPhoneNumber.isNotBlank() && selectedHour != null && selectedMinute != null && typedPhoneNumber.length == countryCode.format.length) {
            progressDialog.showLoading()

            SmartLocation.with(this).location().oneFix()
                .start {
                    val assign = AssignCall(
                        countryCode = countryCode.name,
                        phoneNumber = typedPhoneNumber,
                        callTime = currentDate,
                        duration = "PT${selectedHour}H${selectedMinute}M",
                        description = description,
                        longitude = it.longitude,
                        latitude = it.latitude
                    )
                    mFinanceViewModel.assignIncomingCall(assign, contractId)
                }
        } else {
            showException(getString(R.string.fill_out_all_fields), this)
        }
    }

    private fun showTimePicker() {
        val timePicker = TimePickerFragment(this, selectedHour, selectedMinute)
        timePicker.show(supportFragmentManager)
    }

    override fun selectedTime(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute
        val time = "$hour:$minute"
        binding.selectTimeBtn.text = time
    }

    private fun setupObservers() {
        mFinanceViewModel.assignCallResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val intent = Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                is NetworkResult.Loading -> {}
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
        mFinanceViewModel.countryCode.observe(this, { countryCode ->
            this.countryCode = countryCode
            binding.phoneNumberText.mask = countryCode.format
            binding.phoneNumberText.setText(phoneNumber)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}