package com.magnox.birthdays.room

import android.os.Parcelable
import android.text.TextUtils
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Entity
@Parcelize
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    val firstName: String?,
    val lastName: String?,
    val birthday: Calendar,
    val notes: String?,
    val savedGroupId: Long? //TODO link correctly!
) : Parcelable {
    fun getAge() : Int {
        val today = Calendar.getInstance()
        var age = today[Calendar.YEAR] - birthday[Calendar.YEAR]

        if (today[Calendar.DAY_OF_YEAR] < birthday[Calendar.DAY_OF_YEAR]) {
            age--
        }

        return age
    }

    fun getFullName() : String? {
        if (!TextUtils.isEmpty(firstName)) {
            if (!TextUtils.isEmpty(lastName)) {
                return "$firstName $lastName"
            }
            return firstName
        }
        return lastName
    }

    fun getMonthName(): String? {
        return SimpleDateFormat("MMM", Locale.getDefault()).format(birthday.time)
    }
}

@Entity
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val groupId: Long,
    val groupName: String
)

data class GroupOfPersons(
    @Embedded val person: PersonEntity,
    @Relation(
        parentColumn = "groupId",
        entityColumn = "savedGroupId"
    )
    val persons: List<PersonEntity>
)