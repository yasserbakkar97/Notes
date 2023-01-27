package com.example.notes.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.R.layout.item_rv_notes
import com.example.notes.entities.Notes
import kotlinx.android.synthetic.main.item_rv_notes.view.*

class NotesAdapter() :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    var arrayList = ArrayList<Notes>()
    var listener : OnItemClickListener? = null
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

        if(arrayList[position].imgPath != null){
            holder.itemView.imgNote.setImageBitmap(BitmapFactory.decodeFile(arrayList[position].imgPath))
            holder.itemView.imgNote.visibility = View.VISIBLE
        }else{
            holder.itemView.imgNote.visibility = View.GONE
        }

        if(arrayList[position].webLink != null){
            holder.itemView.tvWebLink.text = arrayList[position].webLink
            holder.itemView.tvWebLink.visibility = View.VISIBLE
        }else{
            holder.itemView.tvWebLink.visibility = View.GONE
        }

        holder.itemView.cardView.setOnClickListener {
            listener!!.onClicked(arrayList[position].id!!)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setData(arrNotesList: List<Notes>){
        arrayList = arrNotesList as ArrayList<Notes>
    }
    fun setOnClickListener(listener1: OnItemClickListener){
        listener = listener1
    }

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }

    interface OnItemClickListener{
        fun onClicked(noteId:Int)
    }

}