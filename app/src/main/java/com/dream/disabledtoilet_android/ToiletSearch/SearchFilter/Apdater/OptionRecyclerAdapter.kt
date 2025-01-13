package com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.Apdater

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dream.disabledtoilet_android.ToiletSearch.Adapter.AdapterEventListener
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.Model.OptionModel
import com.dream.disabledtoilet_android.databinding.FilterOptionItemBinding

class OptionRecyclerAdapter(private val listener: AdapterEventListener): RecyclerView.Adapter<OptionRecyclerAdapter.ItemViewHolder>(){
    private var optionList: MutableList<OptionModel> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OptionRecyclerAdapter.ItemViewHolder {
        return ItemViewHolder(
            FilterOptionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return optionList.size
    }

    override fun onBindViewHolder(holder: OptionRecyclerAdapter.ItemViewHolder, position: Int) {
        return holder.bind(optionList[position])
    }

    inner class ItemViewHolder(
        val binding: FilterOptionItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(optionListItem: OptionModel){
            binding.option.text = optionListItem.option
            binding.optionSwitch.isChecked = optionListItem.isChecked

            // 체크표시 바뀌면 리스트에 반영
            binding.optionSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                optionListItem.isChecked = isChecked
                listener.onOptionCheckedChange(optionList)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateOptionList(optionList: List<OptionModel>){
        this.optionList = optionList.toMutableList()
        Log.d("test optionList", optionList.toString())
        notifyDataSetChanged()
    }

    fun getOptionList(): List<OptionModel> {
        return optionList
    }
}