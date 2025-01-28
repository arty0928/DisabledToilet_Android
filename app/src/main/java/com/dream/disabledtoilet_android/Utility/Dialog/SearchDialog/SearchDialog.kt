package com.dream.disabledtoilet_android.Utility.Dialog.SearchDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dream.disabledtoilet_android.R
import com.dream.disabledtoilet_android.Utility.Dialog.SearchDialog.Adapter.SearchResultAdapter
import com.dream.disabledtoilet_android.Utility.Dialog.SearchDialog.Listener.SearchDialogListener
import com.dream.disabledtoilet_android.Utility.Dialog.SearchDialog.Listener.SearchResultSelectListener
import com.dream.disabledtoilet_android.Utility.Dialog.SearchDialog.ViewModel.SearchDialogViewModel
import com.dream.disabledtoilet_android.Utility.KaKaoAPI.KakaoApiRepository
import com.dream.disabledtoilet_android.Utility.KaKaoAPI.Model.SearchResultDocument
import com.dream.disabledtoilet_android.databinding.DialogSearchLocationBinding
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.launch

/**
 * 현재 하드코딩 해놓은 부분 많음
 */
class SearchDialog(
    private val query: String,
    private val userPosition: LatLng?,
    private val x: Int,
    private val y: Int,
    val searchDialogListener: SearchDialogListener
) : DialogFragment() {
    lateinit var binding: DialogSearchLocationBinding
    lateinit var viewModel: SearchDialogViewModel
    private lateinit var recyclerAdapter: SearchResultAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 뷰모델 받아오기
        viewModel = ViewModelProvider(this)[SearchDialogViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSearchLocationBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(
            R.drawable.filter_dialog_round
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // 화면 꽉 채우기
        setDialogFullScreen()
        // 리사이클러뷰
        recyclerAdapter = SearchResultAdapter(
            object : SearchResultSelectListener {
                override fun onSearchResultSelected(searchResult: SearchResultDocument) {
                    searchDialogListener.addOnSearchResultListener(searchResult)
                    dismiss()
                }
            }
        )
        binding.resultRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.resultRecycler.adapter = recyclerAdapter

        // 검색 버튼
        binding.searchButton.setOnClickListener {
            search(viewModel.query.value)
        }

        // TextWatcher
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 혹시 나중에 필요하면 추가
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setQuery(binding.searchBar.text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                // 혹시 나중에 필요하면 추가
            }
        })
        binding.searchBar.requestFocus()

        // 키보드 표시를 지연 처리
        binding.searchBar.post {
            showKeyboard(binding.searchBar)
        }

        if(query != ""){
            search(query)
            binding.searchBar.setText(query)
        }
    }

    private fun setDialogFullScreen() {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = x
        val deviceHeight = y
        params?.width = (deviceWidth)
        params?.height = (deviceHeight)
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun search(query: String?) {
        lifecycleScope.launch {
            val kakaoRepository = KakaoApiRepository.KakaoLocalRepository()

            val result = query?.let {
                kakaoRepository.searchWithKeyword(
                    query = it,
                    x = userPosition?.longitude.toString(),
                    y = userPosition?.latitude.toString(),
                    sort = "accuracy"
                ).getOrNull()
            }

            val resultList = result?.documents
            recyclerAdapter.updateOptionList(resultList)
        }
    }

    private fun showKeyboard(view: View) {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }
}