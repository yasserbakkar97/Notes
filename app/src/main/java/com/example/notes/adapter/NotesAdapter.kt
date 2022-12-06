package com.example.notes.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.R.layout.item_rv_notes
import com.example.notes.entities.Notes
import kotlinx.android.synthetic.main.item_rv_notes.view.*

class NotesAdapter(val arrayList: List<Notes>) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(item_rv_notes, parent , false))
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.itemView.tvTitle.text = arrayList[position].title
        holder.itemView.tvDesc.text = arrayList[position].noteText
        holder.itemView.tvDataTime.text = arrayList[position].dataTime

        if (arrayList[position].color != null){
            holder.itemView.cardView.setCardBackgroundColor(Color.parseColor(arrayList[position].color))
        }else{
            holder.itemView.cardView.setCardBackgroundColor(Color.parseColor("#171C26"))
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }

}