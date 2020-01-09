package com.magnox.birthdays

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.magnox.birthdays.notification.NotificationHandler
import com.magnox.birthdays.room.PersonDatabase
import com.magnox.birthdays.room.PersonEntity
import com.magnox.birthdays.util.ioThread
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERSON_DATA = "PERSON_DATA"
        const val ACTIVITY_REQUEST_CODE_ADD = 1
        const val ACTIVITY_REQUEST_CODE_EDIT = 2
        const val ACTION_DELETE = "ACTION_DELETE"
    }

    private var vm: ListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        vm = ViewModelProviders.of(this).get(ListViewModel::class.java).also {
            it.getPersonData(this).observe(this, Observer { personList ->
                if (personList != null) {
                    val personAdapter = PersonAdapter(personList)
                    rv_birthday_list.adapter = personAdapter
                    personAdapter.onItemClick = { person ->
                        val intent = Intent(this, AddOrEditActivity::class.java)
                        intent.putExtra(PERSON_DATA, person)
                        startActivityForResult(intent, ACTIVITY_REQUEST_CODE_EDIT)
                    }
                }
            })
        }

        rv_birthday_list.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            val intent = Intent(this, AddOrEditActivity::class.java)
            startActivityForResult(intent, ACTIVITY_REQUEST_CODE_ADD)
        }

        createNotificationChannel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val deleteUid = data?.getIntExtra(ACTION_DELETE, -1)
        if (deleteUid != null && deleteUid > 0) {
            ioThread {
                if (vm != null) {
                    vm!!.deletePerson(deleteUid, this)

                    NotificationHandler.deleteBirthday(this, deleteUid)
                }
            }
        }

        when (requestCode) {
            //TODO simplify duplicated code!
            ACTIVITY_REQUEST_CODE_ADD -> {
                ioThread {
                    if (vm != null && data != null) {
                        val personExtra = data.getParcelableExtra<PersonEntity>(PERSON_DATA)
                        if (personExtra != null) {
                            val id = vm!!.addPerson(personExtra, this)

                            val db = PersonDatabase.getInstance(this)
                            val person: PersonEntity = db.personDao().getById(id)
                            NotificationHandler.addOrEditBirthday(this, person, false)
                        }
                    }
                }
            }
            ACTIVITY_REQUEST_CODE_EDIT -> {
                ioThread {
                    if (vm != null && data != null) {
                        val personExtra = data.getParcelableExtra<PersonEntity>(PERSON_DATA)
                        if (personExtra?.uid != null) {
                            vm!!.updatePerson(personExtra, this)

                            val db = PersonDatabase.getInstance(this)
                            val person: PersonEntity = db.personDao().getById(personExtra.uid)
                            NotificationHandler.addOrEditBirthday(this, person, true)
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

    //TODO use channels for groups?
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = getString(R.string.notification_channel_id)
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
