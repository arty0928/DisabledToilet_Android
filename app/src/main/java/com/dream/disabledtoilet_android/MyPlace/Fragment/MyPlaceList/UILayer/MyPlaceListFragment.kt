package com.dream.disabledtoilet_android.MyPlace.Fragment.MyPlaceList.UILayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dream.disabledtoilet_android.Model.ToiletListModel
import com.dream.disabledtoilet_android.MyPlace.Fragment.MyPlaceList.UILayer.Adapter.MyPlaceListListener
import com.dream.disabledtoilet_android.MyPlace.Fragment.MyPlaceList.UILayer.Adapter.PlaceRecyclerAdapter
import com.dream.disabledtoilet_android.MyPlace.Fragment.MyPlaceList.UILayer.ViewModel.MyPlaceListViewModel
import com.dream.disabledtoilet_android.databinding.FragmentMyPlaceListBinding

class MyPlaceListFragment(val placeList: List<ToiletListModel>, val listener: MyPlaceListListener) : Fragment() {
    private lateinit var binding: FragmentMyPlaceListBinding
    private lateinit var viewModel: MyPlaceListViewModel
    val recyclerAdapter = PlaceRecyclerAdapter(
        object : MyPlaceListListener {
            override fun addOnPlaceClickListener(toiletListModel: ToiletListModel) {
                listener.addOnPlaceClickListener(toiletListModel)
            }

            override fun addOnBackButtonClickListener() {

            }
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MyPlaceListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPlaceListBinding.inflate(layoutInflater)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = recyclerAdapter
        binding.backButton.setOnClickListener{
            listener.addOnBackButtonClickListener()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        recyclerAdapter.updateList(placeList)
    }
}