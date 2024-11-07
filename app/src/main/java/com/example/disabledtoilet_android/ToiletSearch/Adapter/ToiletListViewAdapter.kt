package com.example.disabledtoilet_android.ToiletSearch.Adapter

import ToiletModel
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.disabledtoilet_android.databinding.ToiletListItemBinding

class ToiletListViewAdapter(private var itemList: MutableList<ToiletModel>) :
    RecyclerView.Adapter<ToiletListViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ToiletListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(toiletListItem: ToiletModel) {
            binding.toiletName.text = toiletListItem.restroom_name
            val toiletRoadAddress = toiletListItem.address_road
            if (toiletRoadAddress.isNotBlank() && toiletRoadAddress != "\"\"") {
                binding.address.text = toiletListItem.address_road
            } else {
                binding.address.text = toiletListItem.address_lot
            }
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
    fun updateList(updatedList: MutableList<ToiletModel>) {
        itemList.clear()
        itemList.addAll(updatedList)
        notifyDataSetChanged()
    }
}
