package com.magnox.birthdays.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PersonDao {
    @Query("SELECT * FROM PersonEntity")
    fun getAll(): LiveData<List<PersonEntity>>
//    fun getAll(): LiveData<MutableList<PersonEntity>>

//    @Query("SELECT * FROM userentity WHERE first IN (:firstNames)")
//    fun loadAllByName(firstNames: Array<String>): LiveData<List<UserEntity>>

    @Transaction
    @Query("SELECT * FROM GroupEntity")
    fun getGroupOfPersons(): LiveData<List<GroupOfPersons>>

    @Query("SELECT * FROM PersonEntity WHERE uid IN (:ids)")
    fun loadAllById(ids: Array<Int>): LiveData<List<PersonEntity>>

    @Query("SELECT * FROM PersonEntity WHERE firstName LIKE :firstName AND lastName LIKE :lastName LIMIT 1")
    fun findByName(firstName: String, lastName: String): PersonEntity

    @Insert
    fun insert(person: PersonEntity) : Long

    @Insert
    fun insert(group: GroupEntity) : Long

    @Insert
    fun insertAll(persons: List<PersonEntity>)

    @Update
    fun update(person: PersonEntity)

    @Delete
    fun delete(person: PersonEntity)

    @Query("DELETE FROM PersonEntity WHERE uid = :uid")
    fun deleteByUserId(uid: Int)

    @Query("DELETE FROM GroupEntity WHERE groupId = :groupId")
    fun deleteByGroupId(groupId: Int) //TODO check what happens! are all related entries deleted?

    @Query("SELECT * FROM PersonEntity WHERE uid = :id LIMIT 1")
    fun getById(id: Int): PersonEntity
}