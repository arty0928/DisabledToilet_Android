package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
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
    var toiletListViewAdapter = ToiletListViewAdapter(mutableListOf())
    val loadingDialog = LoadingDialog()
    val filterDialog = FilterDialog()
    var allToiletData = listOf<ToiletModel>()

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
                ToiletData.getToiletAllData(
                    onSuccess = { toilets ->
                        allToiletData = toilets
                        loadingDialog.dismiss()
                        toiletListViewAdapter.updateList(
                            toiletRepository.getToiletWithSearchKeyword(
                                toilets,
                                query
                            )
                        )
                    },
                    onFailure = { exception ->
                        Log.d("[ToiletFilterSearchActivity] ", exception.toString())
                    }
                )
            }
        } else {
            toiletListViewAdapter.updateList(
                toiletRepository.getToiletByRoadAddress(
                    ToiletData.cachedToiletList!!,
                    query
                )
            )
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

        getSearchKeyWord()
    }

    private fun getSearchKeyWord() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                query = binding.searchBar.text.toString()
                if (ToiletData.toiletListInit){
                    toiletListViewAdapter.updateList(
                        toiletRepository.getToiletWithSearchKeyword(
                            allToiletData,
                            query
                        )
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    private fun showFilterDialog() {
        filterDialog.show(supportFragmentManager, loadingDialog.tag)
    }

    private fun applyFilter() {
        filterSearchDialog.show(supportFragmentManager, loadingDialog.tag)
    }
}