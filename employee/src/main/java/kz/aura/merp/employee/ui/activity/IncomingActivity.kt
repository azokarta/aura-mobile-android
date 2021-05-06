package kz.aura.merp.employee.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityIncomingBinding
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.ui.dialog.TimePickerFragment
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeParser
import java.sql.Time
import java.util.*


@AndroidEntryPoint
class IncomingActivity : AppCompatActivity(), TimePickerFragment.TimePickerListener {

    private lateinit var binding: ActivityIncomingBinding

    private lateinit var progressDialog: ProgressDialog
    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private lateinit var phoneNumber: String
    private var callDirectionId: Long = 1L
    private var contractId: Long = 0L
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contractId = intent.getLongExtra("contractId", 0L)
        phoneNumber = intent.getStringExtra("phoneNumber")!!
        callDirectionId = intent.getLongExtra("callDirectionId", 2L)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_call)

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        setupObservers()

        binding.save.setOnClickListener(::save)

//        binding.selectTimeBtn.setOnClickListener {
//            showTimePicker()
//        }
//
//        binding.phoneNumberText.setText(phoneNumber)
    }

    private fun save(view: View) {
        progressDialog.showLoading()

        val description = binding.descriptionText.text.toString()
        val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")
        val currentDate: String = dtf.print(DateTime.now())
        val typedPhoneNumber =" binding.phoneNumberText.text.toString()"
        val durationDate = DateTime.now()
        durationDate.withHourOfDay(selectedHour)
        durationDate.withMinuteOfHour(selectedMinute)
        val duration = Duration(durationDate).toString()
        println(durationDate)
        if (typedPhoneNumber.isNotBlank() && selectedHour != 0 && selectedMinute != 0 && description.isNotBlank()) {
            SmartLocation.with(this).location().oneFix()
                .start {
                    val assign = AssignCall(
                        typedPhoneNumber,
                        null,
                        callDirectionId,
                        currentDate,
                        duration,
                        description,
                        it.longitude,
                        it.latitude
                    )
                    mFinanceViewModel.assignCall(assign, contractId)
                }
        } else {
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show()
        }
    }

    private fun showTimePicker() {
        val timePicker = TimePickerFragment(this)
        timePicker.show(supportFragmentManager)
    }

    override fun onPositiveButtonClick(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute
        val time = "$hour, $minute"
//        binding.selectTimeBtn.text = time
    }

    private fun setupObservers() {
        mFinanceViewModel.assignCallResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val intent = Intent();
                    intent.putExtra("calls", res.data)
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
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}