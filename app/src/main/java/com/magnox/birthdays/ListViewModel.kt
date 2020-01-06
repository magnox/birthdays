package com.magnox.birthdays

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.magnox.birthdays.room.PersonDatabase
import com.magnox.birthdays.room.PersonEntity
import java.util.*

class ListViewModel : ViewModel() {

    fun getPersonData(context: Context): LiveData<List<PersonEntity>> {
//    fun getPersonData(context: Context): LiveData<MutableList<PersonEntity>> {
        val db = PersonDatabase.getInstance(context)
        return db.personDao().getAll()
    }

//    fun getPersonData2(context: Context): MutableList<PersonEntity> {
//        val db = PersonDatabase.getInstance(context)
//        return db.personDao().getAll().value!!
//    }

    //TODO is this the right location for these methods?
    fun addPerson(person: PersonEntity, context: Context): Int {
        return addPerson(person.firstName, person.lastName, person.birthday, person.notes, context)
    }

    private fun addPerson(
        first: String?,
        last: String?,
        birthday: Calendar,
        notes: String?,
        context: Context
    ): Int {
        val db = PersonDatabase.getInstance(context)
        return db.personDao().insert(PersonEntity(null, first, last, birthday, notes, null)).toInt()
    }

    fun updatePerson(person: PersonEntity, context: Context) {
        val db = PersonDatabase.getInstance(context)
        db.personDao().update(PersonEntity(person.uid, person.firstName, person.lastName, person.birthday, person.notes, null))
    }
}