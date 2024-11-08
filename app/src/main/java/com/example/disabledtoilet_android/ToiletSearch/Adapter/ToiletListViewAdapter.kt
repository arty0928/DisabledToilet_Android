package com.example.disabledtoilet_android.ToiletSearch.Adapter

import ToiletModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.example.disabledtoilet_android.Near.NearActivity
import com.example.disabledtoilet_android.databinding.ToiletListItemBinding
import com.kakao.vectormap.LatLng

class ToiletListViewAdapter(
    private var itemList: MutableList<ToiletModel>,
    val context: Context,
    val userLocation: LatLng?
) :
    RecyclerView.Adapter<ToiletListViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ToiletListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(toiletListItem: ToiletModel) {
            binding.toiletName.text = toiletListItem.restroom_name
            val toiletRoadAddress = toiletListItem.address_road
            if (toiletRoadAddress.isNotBlank() && toiletRoadAddress != "\"\"") {
                binding.address.text = toiletListItem.address_road
            } else {
                binding.address.text = toiletListItem.address_lot
            }

            binding.itemLayout.setOnClickListener {
                handleItemDataToNearPage(toiletListItem)
            }

            binding.distance.text ="${calculateDistance(toiletListItem)}"
        }

        /** 거리 계산
         *
         */
        @SuppressLint("DefaultLocale")
        private fun calculateDistance(toiletData: ToiletModel): String {
            var distanceInMeters: Float = 3F
            var formattedDistance: String? = null

            // userLocation은 null값 가능
            if (userLocation != null) {
                val currentLatitude = userLocation.latitude
                val currentLongitude = userLocation.longitude
                val currentLocation = Location("").apply {
                    latitude = currentLatitude
                    longitude = currentLongitude
                }

                Log.d("test log", "userLocation: ${currentLocation}")

                // 화장실 위치의 Location 객체 생성
                val toiletLocation = Location("").apply {
                    latitude = toiletData.wgs84_latitude
                    longitude = toiletData.wgs84_longitude
                }

                // 두 위치 사이의 거리 계산 (미터 단위)
                 distanceInMeters = currentLocation.distanceTo(toiletLocation)

                if (toiletLocation.latitude.toInt() != 0 || toiletLocation.longitude.toInt() != 0){
                    // 거리를 적절한 형식으로 변환
                    formattedDistance = when {
                        distanceInMeters < 1000 -> "${distanceInMeters.toInt()}m"
                        else -> String.format("%.1fkm", distanceInMeters / 1000)
                    }
                } else {
                    formattedDistance = "-"
                }

            } else {
                Log.d("test log", "userLocation is null in ToiletListViewAdapter")
                formattedDistance = " - "
            }

            return formattedDistance.toString()
        }

        private fun handleItemDataToNearPage(toiletData: ToiletModel) {
            val intent = Intent(context, NearActivity::class.java)
            intent.putExtra("toiletData", toiletData)
            intent.putExtra("rootActivity", "ToiletFilterSearchActivity")
            context.startActivity(intent)
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
