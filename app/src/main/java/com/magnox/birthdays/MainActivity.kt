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
                    val personAdapter = PersonAdapter(personList)
                    rv_birthday_list.adapter = personAdapter
//                    enableSwipe(personAdapter, rv_birthday_list)
                }
            })
        }

        rv_birthday_list.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivityForResult(intent, ACTIVITY_REQUEST_CODE_ADD)
        }

        createNotificationChannel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ACTIVITY_REQUEST_CODE_ADD -> {
                ioThread {
                    if (vm != null && data != null) {
                        val personExtra = data.getParcelableExtra<PersonEntity>(RESULT_DATA_ADD)
                        if (personExtra != null) {
                            val id = vm!!.addPerson(personExtra, this)

                            val db = PersonDatabase.getInstance(this)
                            val person: PersonEntity = db.personDao().getById(id)
                            NotificationHandler.addBirthday(this, person)
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

    //TODO swipe behavior is not working yet! access to person list for restoring items not possible yet!
    //https://demonuts.com/kotlin-recyclerview-swipe-to-delete/
//    private fun enableSwipe(adapter: PersonAdapter?, recyclerView: RecyclerView) {
//        val p = Paint()
//        val simpleItemTouchCallback =
//            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
//
//                override fun onMove(
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder,
//                    target: RecyclerView.ViewHolder
//                ): Boolean {
//                    return false
//                }
//
//                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                    val position = viewHolder.adapterPosition
//
//                    if (direction == ItemTouchHelper.LEFT) {
//                        val deletedModel = vm!!.getPersonData2(this@MainActivity)[position] //TODO rename
////                        val deletedModel = imageModelArrayList!![position] //TODO rename
//                        adapter!!.removeItem(position)
//                        // showing snack bar with Undo option
//                        val snackbar = Snackbar.make(
//                            window.decorView.rootView,
//                            " removed from Recyclerview!",
//                            Snackbar.LENGTH_LONG
//                        )
//                        snackbar.setAction("UNDO") {
//                            // undo is selected, restore the deleted item
//                            adapter!!.restoreItem(deletedModel, position)
//                        }
//                        snackbar.setActionTextColor(Color.YELLOW)
//                        snackbar.show()
//                    } else {
//                        val deletedModel = vm!!.getPersonData2(this@MainActivity)[position]
//                        adapter!!.removeItem(position)
//                        // showing snack bar with Undo option
//                        val snackbar = Snackbar.make(
//                            window.decorView.rootView,
//                            " removed from Recyclerview!",
//                            Snackbar.LENGTH_LONG
//                        )
//                        snackbar.setAction("UNDO") {
//                            // undo is selected, restore the deleted item
//                            adapter!!.restoreItem(deletedModel, position)
//                        }
//                        snackbar.setActionTextColor(Color.YELLOW)
//                        snackbar.show()
//                    }
//                }
//
//                override fun onChildDraw(
//                    c: Canvas,
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder,
//                    dX: Float,
//                    dY: Float,
//                    actionState: Int,
//                    isCurrentlyActive: Boolean
//                ) {
//
//                    val icon: Bitmap
//                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//
//                        val itemView = viewHolder.itemView
//                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
//                        val width = height / 3
//
//                        if (dX > 0) {
//                            p.color = Color.parseColor("#388E3C")
//                            val background =
//                                RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
//                            c.drawRect(background, p)
//                            icon = BitmapFactory.decodeResource(resources, android.R.drawable.ic_delete) //TODO
//                            val icon_dest = RectF(
//                                itemView.left.toFloat() + width,
//                                itemView.top.toFloat() + width,
//                                itemView.left.toFloat() + 2 * width,
//                                itemView.bottom.toFloat() - width
//                            )
//                            c.drawBitmap(icon, null, icon_dest, p)
//                        } else {
//                            p.color = Color.parseColor("#D32F2F")
//                            val background = RectF(
//                                itemView.right.toFloat() + dX,
//                                itemView.top.toFloat(),
//                                itemView.right.toFloat(),
//                                itemView.bottom.toFloat()
//                            )
//                            c.drawRect(background, p)
//                            icon = BitmapFactory.decodeResource(resources, android.R.drawable.ic_delete) //TODO
//                            val icon_dest = RectF(
//                                itemView.right.toFloat() - 2 * width,
//                                itemView.top.toFloat() + width,
//                                itemView.right.toFloat() - width,
//                                itemView.bottom.toFloat() - width
//                            )
//                            c.drawBitmap(icon, null, icon_dest, p)
//                        }
//                    }
//                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//                }
//            }
//        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
//        itemTouchHelper.attachToRecyclerView(recyclerView)
//    }
}
