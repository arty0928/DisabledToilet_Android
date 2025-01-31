package com.dream.disabledtoilet_android.MyPlace.Fragment.MyPlaceList.UILayer.Adapter

import com.dream.disabledtoilet_android.Model.ToiletListModel
import com.dream.disabledtoilet_android.Model.ToiletModel

interface MyPlaceListListener {
    fun addOnPlaceClickListener(toiletListModel: ToiletListModel)

    fun addOnBackButtonClickListener()
}