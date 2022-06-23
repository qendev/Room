package com.example.roomdatabaseapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabaseapp.databinding.ItemsRowBinding
import com.example.roomdatabaseapp.entity.EmployeeEntity

class ItemAdapter(private val items:ArrayList<EmployeeEntity>,
                  private val updateListener:(id:Int)->Unit,
                  private val deleteListener:(id:Int)->Unit
):RecyclerView.Adapter<ItemAdapter.ViewHolder>() {


    class ViewHolder(binding:ItemsRowBinding):RecyclerView.ViewHolder(binding.root){
        val llMain = binding.llMain
        val tvName = binding.tvName
        val tvEmail = binding.tvEmail
        val ivEdit = binding.ivEdit
        val ivDelete = binding.ivDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemsRowBinding.inflate
            (LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //we first get the context
        val context = holder.itemView.context
        val item = items[position]
        //assign the textViews in the item layout
        holder.tvName.text = item.name
        holder.tvEmail.text = item.email

        //now to change the background color of the linearlayout
        if(position % 2 ==0){
            holder.llMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,
            R.color.colorLightGray))
        }
        else{
            holder.llMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,
            R.color.white))
        }
        //use the click listeners for editing and deleting
        holder.ivEdit.setOnClickListener {
            updateListener.invoke(item.id)
        }

        holder.ivDelete.setOnClickListener {
            deleteListener.invoke(item.id)
        }
    }

    override fun getItemCount(): Int {
        return  items.size
    }
}