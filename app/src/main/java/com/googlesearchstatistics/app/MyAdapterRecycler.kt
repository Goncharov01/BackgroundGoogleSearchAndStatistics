package com.googlesearchstatistics.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.googlesearchstatistics.app.databinding.SingleItemBinding
import com.googlesearchstatistics.app.db.DataLink

class MyAdapterRecycler : RecyclerView.Adapter<MyViewHolder>() {

    private val listLink = mutableListOf<DataLink>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: SingleItemBinding =
            SingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dataLink = listLink[position]
        holder.binding.textViewTime.text = dataLink.time
        holder.binding.textViewLink.text = dataLink.link
    }

    override fun getItemCount(): Int {
        return listLink.size
    }

    fun addListRecycler(listLink: List<DataLink>) {
        this.listLink.clear()
        this.listLink.addAll(listLink)
        notifyDataSetChanged()
    }

}

class MyViewHolder(var binding: SingleItemBinding) :
    RecyclerView.ViewHolder(binding.root)