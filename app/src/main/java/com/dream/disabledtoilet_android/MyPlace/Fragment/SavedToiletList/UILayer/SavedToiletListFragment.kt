package com.dream.disabledtoilet_android.MyPlace.Fragment.SavedToiletList.UILayer

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dream.disabledtoilet_android.Model.PlaceModel
import com.dream.disabledtoilet_android.Model.ToiletModel
import com.dream.disabledtoilet_android.MyPlace.Fragment.SavedToiletList.UILayer.Adapter.SavedToiletListListener
import com.dream.disabledtoilet_android.MyPlace.Fragment.SavedToiletList.UILayer.Adapter.SavedToiletRecyclerAdapter
import com.dream.disabledtoilet_android.MyPlace.Fragment.SavedToiletList.UILayer.ViewModel.SavedToiletLIstViewModel
import com.dream.disabledtoilet_android.databinding.FragmentSavedToiletListBinding

class SavedToiletListFragment(val listener: SavedToiletListListener) : Fragment() {
    lateinit var binding: FragmentSavedToiletListBinding
    lateinit var viewModel: SavedToiletLIstViewModel
    var myPlace = PlaceModel()
    lateinit var adapter: SavedToiletRecyclerAdapter
    var toiletList = listOf<ToiletModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = SavedToiletRecyclerAdapter(this.requireContext(), myPlace)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SavedToiletLIstViewModel::class.java]
        binding = FragmentSavedToiletListBinding.inflate(layoutInflater)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.backButton.setOnClickListener {
            listener.addOnBackButtonClickListener()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    fun setPlace(place: PlaceModel){
        myPlace = place
        adapter.setPlace(place)
    }

    fun updateToiletList(toiletList: List<ToiletModel>){
        adapter.updateList(toiletList,1)
    }
}