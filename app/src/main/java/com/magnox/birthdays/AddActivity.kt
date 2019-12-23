package com.magnox.birthdays

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.magnox.birthdays.room.PersonEntity
import com.magnox.spinnerdatepicker.DatePicker
import com.magnox.spinnerdatepicker.DatePickerDialog
import com.magnox.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_add.*
import java.util.*

class AddActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        btn_calendar.setOnClickListener {
            val today = Calendar.getInstance()

            SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .customTitle(getString(R.string.add_date_picker_dialog_title))
                .showDaySpinner(true)
                .defaultDate(
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
                )
                .build()
                .show()
        }

        et_day.addTextChangedListener(FocusNextViewTextWatcher(2, et_month))
        et_month.addTextChangedListener(FocusNextViewTextWatcher(2, et_year))
    }

    private fun finishAndReturnData() {
        val data = Intent()
        val birthday = Calendar.getInstance().apply {
            if (et_year.text.toString().isNotEmpty()
                && et_month.text.toString().isNotEmpty()
                && et_day.text.toString().isNotEmpty()) {
                set(
                    et_year.text.toString().toInt(),
                    et_month.text.toString().toInt() - 1,
                    et_day.text.toString().toInt()
                )
            }
        }

        val notes = if (et_notes.text.toString().isNotEmpty()) et_notes.text.toString() else null
        val firstName = if (et_firstname.text.toString().isNotEmpty()) et_firstname.text.toString() else null
        val lastName = if (et_lastname.text.toString().isNotEmpty()) et_lastname.text.toString() else null

        val personData = PersonEntity(null, firstName, lastName, birthday, notes, null)

        data.putExtra(MainActivity.RESULT_DATA_ADD, personData)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cancel_confirm, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cancel -> {
                finish()
                true
            }
            R.id.action_confirm -> {
                finishAndReturnData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        et_day.setText(dayOfMonth.toString())
        et_month.setText((monthOfYear + 1).toString())
        et_year.setText(year.toString())
    }

    private class FocusNextViewTextWatcher internal constructor(var count: Int, var view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            if (editable.length == count) view.requestFocus()
        }
    }
}
