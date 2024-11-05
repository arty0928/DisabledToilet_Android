package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.disabledtoilet_android.MainActivity
import com.example.disabledtoilet_android.ToiletSearch.Adapter.ToiletListViewAdapter
import com.example.disabledtoilet_android.ToiletSearch.SearchFilter.FilterSearchDialog
import com.example.disabledtoilet_android.ToiletSearch.SearchFilter.FilterViewModel
import com.example.disabledtoilet_android.Utility.Dialog.SortDialog
import com.example.disabledtoilet_android.Utility.Dialog.LoadingDialog
import com.example.disabledtoilet_android.databinding.ActivityToiletFilterSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class ToiletFilterSearchActivity : AppCompatActivity() {
    lateinit var binding: ActivityToiletFilterSearchBinding
    val toiletRepository = ToiletRepository()
    var toiletListViewAdapter = ToiletListViewAdapter(mutableListOf())
    val loadingDialog = LoadingDialog()
    val sortDialog = SortDialog()

    lateinit var toiletList: MutableList<ToiletModel>

    var query = ""
    lateinit var filterSearchDialog: FilterSearchDialog

    lateinit var filterViewModel: FilterViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToiletFilterSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filterViewModel = ViewModelProvider(this)[FilterViewModel::class.java]

        if (ToiletData.cachedToiletList.isNullOrEmpty()){
            toiletList = mutableListOf<ToiletModel>()
        } else {
            toiletList = ToiletData.cachedToiletList!!.toMutableList()
        }

        CoroutineScope(Dispatchers.Main).launch {
            setUi()
        }

    }

    private suspend fun setUi() {
        binding.toiletRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.toiletRecyclerView.adapter = toiletListViewAdapter

        if (!ToiletData.toiletListInit) {
            loadingDialog.show(supportFragmentManager, loadingDialog.tag)
            withContext(Dispatchers.IO) {

                toiletListViewAdapter.updateList(
                    toiletRepository.getToiletWithSearchKeyword(
                        toiletList,
                        query
                    )
                )
                loadingDialog.dismiss()
            }
        } else {
            toiletListViewAdapter.updateList(
                ToiletData.cachedToiletList!!.toMutableList()
            )
        }

        binding.filterButton.setOnClickListener {
            showFilter()
        }
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.filter.setOnClickListener {
            showSortDialog()
        }
        binding.toggle.setOnClickListener {
            showSortDialog()
        }

        filterViewModel.isDialogDismissed.observe(this) { isDismissed ->
            if (isDismissed) {
                applyFilter()
            }
        }

        getSearchKeyWord()
    }

    private fun getSearchKeyWord() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                query = binding.searchBar.text.toString()
                if (ToiletData.toiletListInit) {
                    toiletListViewAdapter.updateList(
                        toiletRepository.getToiletWithSearchKeyword(
                            ToiletData.cachedToiletList!!,
                            query
                        )
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    private fun showSortDialog() {
        sortDialog.show(supportFragmentManager,ㅣㅐ.tag)
    }

    private fun showFilter() {
        filterSearchDialog = FilterSearchDialog.newInstance()
        filterSearchDialog.show(supportFragmentManager, filterSearchDialog.tag)
        filterViewModel.isDialogDismissed.value = false
    }

    private fun applyFilter() {
        Log.d("test log", "[applyFilter]: dismissed")
        toiletRepository.setFilter(filterViewModel, ToiletData.cachedToiletList!!.toList())
        toiletListViewAdapter.updateList(
            toiletRepository.getToiletWithSearchKeyword(
                ToiletData.cachedToiletList!!, query
            )
        )
    }
}