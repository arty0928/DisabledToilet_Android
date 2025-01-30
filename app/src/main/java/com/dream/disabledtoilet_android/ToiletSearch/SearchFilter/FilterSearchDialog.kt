package com.dream.disabledtoilet_android.ToiletSearch.SearchFilter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dream.disabledtoilet_android.R
import com.dream.disabledtoilet_android.ToiletSearch.Adapter.AdapterEventListener
import com.dream.disabledtoilet_android.ToiletSearch.FilterApplyListener
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.Apdater.OptionRecyclerAdapter
import com.dream.disabledtoilet_android.Model.OptionModel
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel.FilterStatus
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel.FilterViewModel
import com.dream.disabledtoilet_android.databinding.FilterSearchDialogBinding

class FilterSearchDialog(val originFilterStatus: FilterStatus, val onApplyFilterListener: FilterApplyListener) : DialogFragment() {
    lateinit var binding: FilterSearchDialogBinding
    lateinit var viewModel: FilterViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        isCancelable = false

        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바깥 화면 터치 시 취소 방지
        this.setCancelable(false)
        // 뷰모델 받아오기
        viewModel = ViewModelProvider(this)[FilterViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterSearchDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(
            R.drawable.filter_dialog_round
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // 최근 점검 슬라이더
        binding.recentCheckSlider.addOnChangeListener { slider, value, fromUser ->
            viewModel.setRecentCheck(value.toInt())
        }
        // 리사이클러뷰 어댑터, 리스너
        val recyclerAdapter = OptionRecyclerAdapter(
            object : AdapterEventListener {
                override fun onOptionCheckedChange(optionList: List<OptionModel>) {
                    viewModel.setOptionStatus(optionList)
                }
            }
        )

        // 옵션 리사이클러뷰
        binding.optionRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.optionRecycler.adapter = recyclerAdapter

        binding.recentCheckSlider.setValue(viewModel.filterStatus.value!!.recentCheck.value.toFloat())

        // 필터 UI 변화 시, 필터 상태 변경
        viewModel.recentCheck.observe(this){
            viewModel.makeFilterStatus()
        }
        viewModel.optionStatus.observe(this){
            viewModel.makeFilterStatus()
        }

        // 필터 상태 옵저빙
        viewModel.filterStatus.observe(this){
            binding.recentCheckSlider.setValue(it.recentCheck.value.toFloat())
            binding.optionRecycler.post{
                recyclerAdapter.updateOptionList(it.copy().optionStatus.optionStatusList.map { it.copy() })
            }
        }

        // 뒤로 가기 버튼
        binding.backButton.setOnClickListener {
            viewModel.getOriginalFilterStatus()
            onApplyFilterListener.onDialogDismissListener(true)
            dismiss()
        }

        // 적용 버튼
        binding.searchToilet.setOnClickListener {
            onApplyFilterListener.onApplyFilterListener(viewModel.getFilterStatus())
            onApplyFilterListener.onDialogDismissListener(true)
            dismiss()
        }

        // 초기화 버튼
        binding.clearButton.setOnClickListener{
            viewModel.clearFilterStatus()
        }

        // 초기 필터 상태 세팅
        viewModel.saveOriginalFilterStatus(originFilterStatus)
    }
}