package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.disabledtoilet_android.MainActivity
import com.example.disabledtoilet_android.ToiletSearch.Adapter.ToiletListViewAdapter
import com.example.disabledtoilet_android.ToiletSearch.SearchFilter.FilterSearchDialog
import com.example.disabledtoilet_android.Utility.Dialog.FilterDialog
import com.example.disabledtoilet_android.Utility.Dialog.LoadingDialog
import com.example.disabledtoilet_android.databinding.ActivityToiletFilterSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToiletFilterSearchActivity : AppCompatActivity() {
    lateinit var binding: ActivityToiletFilterSearchBinding
    val toiletRepository = ToiletRepository()
    var toiletListViewAdapter = ToiletListViewAdapter(ToiletData.toilets)
    val loadingDialog = LoadingDialog()
    val filterDialog = FilterDialog()

    var query = "능동로"
    val filterSearchDialog = FilterSearchDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToiletFilterSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        CoroutineScope(Dispatchers.Main).launch {
            setUi()
        }
    }

    suspend fun setUi() {
        binding.toiletRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.toiletRecyclerView.adapter = toiletListViewAdapter

        if (!ToiletData.toiletListInit) {
            loadingDialog.show(supportFragmentManager, loadingDialog.tag)
            withContext(Dispatchers.IO) {
                ToiletData.getToiletData { toilets: List<ToiletModel>? ->
                    loadingDialog.dismiss()
                    toiletListViewAdapter.updateList(toiletRepository.getToiletByRoadAddress(query))
                }
            }
        } else {
            toiletListViewAdapter.updateList(toiletRepository.getToiletByRoadAddress(query))
        }

        binding.filterButton.setOnClickListener {
            applyFilter()
        }
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.filter.setOnClickListener {
            showFilterDialog()
        }
        binding.toggle.setOnClickListener {
            showFilterDialog()
        }
    }

    fun showFilterDialog() {
        filterDialog.show(supportFragmentManager, loadingDialog.tag)
    }

    fun applyFilter() {
        filterSearchDialog.show(supportFragmentManager, loadingDialog.tag)
    }
}