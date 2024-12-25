package com.dream.disabledtoilet_android.Near.DataLayer

import ToiletModel
import com.android.tools.build.jetifier.core.utils.Log
import com.dream.disabledtoilet_android.R
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

/**
 *      카카오맵 레이블
 */
class LabelBuilder(val kakaoMap: KakaoMap) {

    /**
     *      ToiletModel 리스트를 기반으로 Label 리스트 생성
     */
    fun makeToiletLabelList(toiletList: List<ToiletModel>): List<Label> {
        val toiletLabelList = mutableListOf<Label>()
        if(toiletList.isNotEmpty()){
            for (i in toiletList.indices){
                val toiletLabel = makeToiletLabel(toiletList.get(i))
                if (toiletLabel != null){
                    toiletLabelList.add(toiletLabel)
                }
            }
        }
        else {
            Log.d("test log: ToiletLabelBuilder", "toiletList is empty")
        }

        return toiletLabelList.toList()
    }
    /**
     *      맵에 화장실 레이블 생성
     */
    private fun makeToiletLabel(toiletModel: ToiletModel): Label?{
        val styles = kakaoMap.labelManager?.addLabelStyles(
            LabelStyles.from(
                LabelStyle.from(R.drawable.map_pin1).setZoomLevel(10),
                LabelStyle.from(R.drawable.map_pin2).setZoomLevel(13),
                LabelStyle.from(R.drawable.map_pin3).setZoomLevel(16),
                LabelStyle.from(R.drawable.map_pin4).setZoomLevel(19)
            )
        )
        val position = LatLng.from(toiletModel.wgs84_latitude, toiletModel.wgs84_longitude)
        val options = LabelOptions.from(position)
            .setStyles(styles)
            .setClickable(true)

        val layer = kakaoMap.labelManager?.layer
        val label = layer?.addLabel(options)
        return label
    }
}