package com.magnox.birthdays

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.magnox.birthdays.room.PersonEntity
import com.magnox.birthdays.util.ioThread
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val RESULT_DATA_ADD = "RESULT_DATA_ADD"
        const val ACTIVITY_REQUEST_CODE_ADD = 1
    }

    private var vm: ListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        vm = ViewModelProviders.of(this).get(ListViewModel::class.java).also {
            it.getPersonData(this).observe(this, Observer { personList ->
                if (personList != null) {
                    rv_birthday_list.adapter = PersonAdapter(personList)
                }
            })
        }

        rv_birthday_list.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivityForResult(intent, ACTIVITY_REQUEST_CODE_ADD)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ACTIVITY_REQUEST_CODE_ADD -> {
                ioThread {
                    if (vm != null && data != null) {
                        val personExtra = data.getParcelableExtra<PersonEntity>(RESULT_DATA_ADD)
                        if (personExtra != null) {
                            vm!!.addPerson(personExtra, this)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
