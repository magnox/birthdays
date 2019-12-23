package com.magnox.birthdays.room

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize
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
) : Parcelable

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