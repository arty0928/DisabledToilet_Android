package com.dream.disabledtoilet_android.Utility.Dialog.SearchDialog.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dream.disabledtoilet_android.Utility.Dialog.SearchDialog.Listener.SearchResultSelectListener
import com.dream.disabledtoilet_android.Model.PlaceModel
import com.dream.disabledtoilet_android.databinding.ItemLocationBinding

class SearchResultAdapter(val listener: SearchResultSelectListener): RecyclerView.Adapter<SearchResultAdapter.ItemViewHolder>() {
    private var searchResultList: List<PlaceModel> = listOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return ItemViewHolder(
            ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        return holder.bind(searchResultList[position])
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    inner class ItemViewHolder(
        val binding: ItemLocationBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(searchResultListItem: PlaceModel){
            binding.placeName.text = formatName(searchResultListItem.place_name)
            binding.groupName.text = extractLastCategory(searchResultListItem.category_name)
            binding.address.text = searchResultListItem.address_name
            binding.distance.text = searchResultListItem.distance?.let {
                formatDistance(it.toInt())
            }
            binding.itemLayout.setOnClickListener{
                listener.onSearchResultSelected(searchResultListItem)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateOptionList(searchResultList: List<PlaceModel>?){
        if (searchResultList != null) {
            this.searchResultList = searchResultList
        } else {
            this.searchResultList = listOf()
        }
        notifyDataSetChanged()
    }

    private fun extractLastCategory(categoryName: String): String {
        // '>'를 기준으로 문자열을 분리
        val categories = categoryName.split(">")

        // 마지막 요소의 공백 제거 후 반환
        return categories.last().trim()
    }

    private fun formatDistance(distance: Int): String {
        return if (distance >= 1000) {
            // km로 변환하여 소수점 한 자리까지 표시
            String.format("%.1f km", distance / 1000.0)
        } else {
            // m 단위로 표시
            "$distance m"
        }
    }
    private fun formatName(address: String): String {
        return if (address.length > 10) {
            "${address.substring(0, 10)}..."
        } else {
            address
        }
    }

}