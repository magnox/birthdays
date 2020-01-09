package com.magnox.birthdays

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.magnox.birthdays.databinding.PersonRvItemBinding
import com.magnox.birthdays.room.PersonEntity

class PersonAdapter(private val persons: List<PersonEntity>): RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    var onItemClick: ((PersonEntity) -> Unit)? = null

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

    inner class PersonViewHolder(v: PersonRvItemBinding): RecyclerView.ViewHolder(v.root){
        private val demoRvItemBinding = v
        fun bind(person: PersonEntity) {
            demoRvItemBinding.person = person
            demoRvItemBinding.executePendingBindings()
        }

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(persons[adapterPosition])
            }
        }
    }
}