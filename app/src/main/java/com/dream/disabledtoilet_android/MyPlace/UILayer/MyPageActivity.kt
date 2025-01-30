package com.dream.disabledtoilet_android.MyPlace.UILayer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dream.disabledtoilet_android.Model.ToiletListModel
import com.dream.disabledtoilet_android.Model.ToiletModel
import com.dream.disabledtoilet_android.MyPlace.Fragment.MyPlaceList.UILayer.Adapter.MyPlaceListListener
import com.dream.disabledtoilet_android.MyPlace.Fragment.MyPlaceList.UILayer.MyPlaceListFragment
import com.dream.disabledtoilet_android.MyPlace.Fragment.SavedToiletList.UILayer.Adapter.SavedToiletListListener
import com.dream.disabledtoilet_android.MyPlace.Fragment.SavedToiletList.UILayer.SavedToiletListFragment
import com.dream.disabledtoilet_android.MyPlace.UILayer.Adpater.MyPageAdapter
import com.dream.disabledtoilet_android.MyPlace.UILayer.ViewModel.MyPageViewModel
import com.dream.disabledtoilet_android.databinding.ActivityMypageBinding

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageBinding
    private lateinit var viewModel: MyPageViewModel
    private lateinit var myPlaceListFragment: MyPlaceListFragment
    private lateinit var savedToiletListFragment: SavedToiletListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MyPageViewModel::class.java]
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.setPlaceList(this)

        viewModel.placeList.observe(this){ placeList ->

            val adapter = MyPageAdapter(
                this,
                setMyPlaceListFragment(placeList),
                setSavedToiletListFragment()
            )

            val viewPager = binding.viewPager
            viewPager.adapter = adapter
            viewPager.offscreenPageLimit = 2

            viewModel.currentFragment.observe(this) { fragment ->
                viewPager.currentItem = when(fragment){
                    "MyPlaceListFragment" -> 0
                    "SavedToiletListFragment" -> 1
                    else -> 0
                }
            }
        }
    }

    private fun setMyPlaceListFragment(placeList: List<ToiletListModel>): MyPlaceListFragment {
        myPlaceListFragment = MyPlaceListFragment(placeList, object : MyPlaceListListener {
            override fun addOnPlaceClickListener(toiletListModel: ToiletListModel) {
                savedToiletListFragment.updateToiletList(toiletListModel.toiletList)
                savedToiletListFragment.setPlace(toiletListModel.place)
                viewModel.setCurrentFragment("SavedToiletListFragment")
            }
        })

        return myPlaceListFragment
    }

    private fun setSavedToiletListFragment(): SavedToiletListFragment {
        savedToiletListFragment = SavedToiletListFragment(object : SavedToiletListListener {
            override fun addOnBackButtonClickListener() {
                viewModel.setCurrentFragment("MyPlaceListFragment")
            }
        })
        return savedToiletListFragment
    }
}
