package com.example.disabledtoilet_android.ToiletSearch.Adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.disabledtoilet_android.ToiletSearch.Model.ToiletModel
import com.example.disabledtoilet_android.databinding.ToiletListItemBinding

class ToiletListViewAdapter(itemList: MutableList<ToiletModel>): RecyclerView.Adapter<ToiletListViewAdapter.ItemViewHolder>() {
    var itemList = itemList

    inner class ItemViewHolder(val binding: ToiletListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(toiletListItem: ToiletModel){
            binding.toiletName.text = toiletListItem.restroom_name
            binding.address.text = toiletListItem.address_road
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ToiletListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        return holder.bind(itemList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(updatedList: MutableList<ToiletModel>){
        itemList.clear()
        itemList.addAll(updatedList)
        notifyDataSetChanged()
    }
}
