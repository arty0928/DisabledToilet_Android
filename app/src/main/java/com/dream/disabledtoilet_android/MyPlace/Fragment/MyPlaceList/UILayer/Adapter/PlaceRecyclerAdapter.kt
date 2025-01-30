package com.dream.disabledtoilet_android.MyPlace.Fragment.MyPlaceList.UILayer.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dream.disabledtoilet_android.Model.PlaceModel
import com.dream.disabledtoilet_android.Model.ToiletListModel
import com.dream.disabledtoilet_android.Model.ToiletModel
import com.dream.disabledtoilet_android.MyPlace.Fragment.SavedToiletList.UILayer.Adapter.SavedToiletRecyclerAdapter
import com.dream.disabledtoilet_android.databinding.ItemMyPlaceBinding

class PlaceRecyclerAdapter(val listener: MyPlaceListListener) : RecyclerView.Adapter<PlaceRecyclerAdapter.ItemViewHolder>(){
    lateinit var binding: ItemMyPlaceBinding
    private var itemList = listOf<ToiletListModel>()
    inner class ItemViewHolder(
        val binding: ItemMyPlaceBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(toiletListModel: ToiletListModel){
            binding.textView.text = toiletListModel.place.place_name
            binding.layout.setOnClickListener{
                listener.addOnPlaceClickListener(toiletListModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemMyPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        return holder.bind(itemList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(updatedList: List<ToiletListModel>){
        itemList = updatedList
        notifyDataSetChanged()
    }
}