package com.dream.disabledtoilet_android.MyPlace.Fragment.SavedToiletList.UILayer.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.dream.disabledtoilet_android.Model.PlaceModel
import com.dream.disabledtoilet_android.Model.ToiletModel
import com.dream.disabledtoilet_android.Near.UILayer.NearActivity
import com.dream.disabledtoilet_android.ToiletSearch.ToiletRepository
import com.dream.disabledtoilet_android.databinding.ToiletListItemBinding
import com.kakao.vectormap.LatLng

class SavedToiletRecyclerAdapter(
    val context: Context,
    var myPlace: PlaceModel
) : RecyclerView.Adapter<SavedToiletRecyclerAdapter.ItemViewHolder>() {
    /**
     * Activity와 별도의 itemList 사용
     * updateList()에서 clearAll하고 파라미터의 list addAll함
     */
    private var itemList: MutableList<ToiletModel> = mutableListOf()
    @RequiresApi(Build.VERSION_CODES.O)
    val toiletRepository = ToiletRepository()
    private fun buildLocation():LatLng{
        return LatLng.from(myPlace.y.toDouble(), myPlace.x.toDouble())
    }

    inner class ItemViewHolder(
        val binding: ToiletListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * bind
         */
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(toiletListItem: ToiletModel) {
            binding.toiletName.text = toiletListItem.restroom_name
            val toiletRoadAddress = toiletListItem.address_road
            //지번이나 도로명 주소 하나만 있는 경우 많음
            if (toiletRoadAddress.isNotBlank() && toiletRoadAddress != "\"\"") {
                binding.address.text = toiletListItem.address_road
            } else {
                binding.address.text = toiletListItem.address_lot
            }
            // 리스트의 아이템 선택 시
            binding.itemLayout.setOnClickListener {
                handleItemDataToNearPage(toiletListItem)
            }
            // 거리정보
//            binding.distance.text ="${calculateDistance(toiletListItem)}"
            binding.distance.text = toiletRepository.calculateDistance(toiletListItem,buildLocation())

        }
        /**
         * 거리 계산
         */
        @SuppressLint("DefaultLocale")
        private fun calculateDistance(toiletData: ToiletModel): String {
            var distanceInMeters: Float = 3F
            var formattedDistance: String? = null
            // userLocation은 null값 가능
            if (buildLocation() != null) {
                val currentLatitude = buildLocation().latitude
                val currentLongitude = buildLocation().longitude
                // 유저 위치
                val currentLocation = Location("").apply {
                    latitude = currentLatitude
                    longitude = currentLongitude
                }
                // 화장실 위치의 Location 객체 생성
                val toiletLocation = Location("").apply {
                    latitude = toiletData.wgs84_latitude
                    longitude = toiletData.wgs84_longitude
                }
                // 두 위치 사이의 거리 계산 (미터 단위)
                distanceInMeters = currentLocation.distanceTo(toiletLocation)
                // 위치 0,0 데이터 필터링
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
        /**
         * NearActivity로 화장실 데이터 intent
         */
        private fun handleItemDataToNearPage(toiletData: ToiletModel) {
            val intent = Intent(context, NearActivity::class.java)
            intent.putExtra("toiletData", toiletData)
            intent.putExtra("placeLocation", myPlace)
            // 넘겨준 Activity 명시
            intent.putExtra("rootActivity", "MyPageActivity")
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        return holder.bind(itemList[position])
    }
    /**
     * 리사이클러뷰 업데이트
     *
     * 거리순 오름차순 정렬
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(updatedList: List<ToiletModel>, sort: Int) {
        // 거리 계산 후 Pair로 저장

        val validList = updatedList.filter {it.distance != -1.0}
        val invalidList = updatedList.filter { it.distance == -1.0 }

        // -1.0 거리 제외, 거리 기준으로 오름차순 정렬
        val sortedList = when (sort){
            0 -> {
                validList.sortedBy { it.distance } + invalidList.sortedByDescending { it.save.size }
            }
            1 -> {
                updatedList.sortedWith(compareByDescending<ToiletModel> {it.save.size}
                    .thenBy{it.distance}).also { sorted ->
                    sorted.forEach { toilet ->
//                            Log.d("SortLog", "Save : ${toilet.restroom_name}, Name: ${toilet.save}")
                    }
                }
            }
            else -> updatedList
        }

        itemList.clear()
        itemList.addAll(sortedList)
        notifyDataSetChanged()
    }

    fun setPlace(place: PlaceModel){
        myPlace = place
    }
}