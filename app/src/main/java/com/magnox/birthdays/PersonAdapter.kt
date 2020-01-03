package com.magnox.birthdays

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.magnox.birthdays.databinding.PersonRvItemBinding
import com.magnox.birthdays.room.PersonEntity

class PersonAdapter(private val persons: List<PersonEntity>): RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {
//class PersonAdapter(private val persons: MutableList<PersonEntity>): RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        return PersonViewHolder(
            PersonRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(persons[position])
    }

    override fun getItemCount(): Int {
        return persons.size
    }

//    fun removeItem(position: Int) {
//        persons.removeAt(position)
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, persons.size)
//        //TODO remove from database!
//    }
//
//    fun restoreItem(person: PersonEntity, position: Int) {
//        persons.add(position, person)
//        notifyItemInserted(position)
//        //TODO write to DB again!
//    }

    class PersonViewHolder(v: PersonRvItemBinding): RecyclerView.ViewHolder(v.root){
        val demoRvItemBinding = v
        fun bind(person: PersonEntity) {
            demoRvItemBinding.person = person
            demoRvItemBinding.executePendingBindings()
        }
    }
}