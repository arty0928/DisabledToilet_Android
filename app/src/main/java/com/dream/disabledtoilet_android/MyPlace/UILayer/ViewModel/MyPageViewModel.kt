package com.dream.disabledtoilet_android.MyPlace.UILayer.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dream.disabledtoilet_android.Model.ToiletListModel
import com.dream.disabledtoilet_android.MyPlace.Fragment.MyPlaceList.DataLayer.PlaceToiletGroupListBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPageViewModel: ViewModel() {
    val _currentFragment = MutableLiveData<String>()
    val currentFragment: LiveData<String> get() = _currentFragment

    val _placeList = MutableLiveData<List<ToiletListModel>>()
    val placeList: LiveData<List<ToiletListModel>> get() = _placeList
    init {
        _placeList.value = listOf()
    }

    fun setPlaceList(context: Context){
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                _placeList.value = PlaceToiletGroupListBuilder(context).makeList()
            }
        }
    }

    fun setCurrentFragment(fragmentName: String) {
        _currentFragment.value = fragmentName
    }


}