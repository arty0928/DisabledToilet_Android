package com.dream.disabledtoilet_android.MyPlace.UILayer.Adpater

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dream.disabledtoilet_android.MyPlace.Fragment.MyPlaceList.UILayer.MyPlaceListFragment
import com.dream.disabledtoilet_android.MyPlace.Fragment.SavedToiletList.UILayer.SavedToiletListFragment

class MyPageAdapter (
    fragmentActivity: FragmentActivity,
    val myPlaceListFragment: MyPlaceListFragment,
    val savedToiletListFragment: SavedToiletListFragment
    ) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> myPlaceListFragment
            1 -> savedToiletListFragment
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}