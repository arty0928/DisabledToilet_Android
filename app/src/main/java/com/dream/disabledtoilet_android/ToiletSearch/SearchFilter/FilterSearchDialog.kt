package com.dream.disabledtoilet_android.ToiletSearch.SearchFilter

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.dream.disabledtoilet_android.R
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel.FilterViewModel
import com.dream.disabledtoilet_android.databinding.FilterSearchDialogBinding

/**
 * 뷰모델 MVVM으로 적용 시켜야 함
 */
class FilterSearchDialog : DialogFragment() {
    lateinit var binding: FilterSearchDialogBinding
    lateinit var viewModel: FilterViewModel
    /**
     * 필터 바로 생성
     */
    companion object{
        fun newInstance() = FilterSearchDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바깥 화면 터치 시 취소 방지
        this.setCancelable(false)
        //뷰모델 받아오기
        viewModel = ViewModelProvider(requireActivity())[FilterViewModel::class.java]
        // 초기 상태 저장
        viewModel.storeStatus()
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
        isCancelable = false
        setUi()
    }
    /**
     * UI 세팅
     */
    private fun setUi() {
        setBackButton()
        // 초기화 버튼
        setClearButton()
        // 화장실 보기
        setSearchToiletButton()
        // 현재 운영
        setOperatingButton()
        setOperatingObserver()
        //조건 적용
        setFilterButtonAndObserver()
    }
    /**
     * 조건 적용 파트는 리스트에 한번에 넣어서 관리
     * 버튼리스트를 만들고 버튼 이름을 뷰모델의 filterString으로 한번에 초기화
     * 리스너도 한번에 모두 처리
     */
    private fun setFilterButtonAndObserver(){
        // 필터 버튼을 리스트에 넣기
        val filterButtonList: List<TextView> = listOf(
            binding.filter1, binding.filter2,
            binding.filter3, binding.filter4,
            binding.filter5, binding.filter6,
            binding.filter7, binding.filter8
        )
        // 버튼 텍스트 뷰모델의 filterString으로 한번에 생성
        for(i in filterButtonList.indices){
            filterButtonList[i].text = viewModel.filterString.filterNameList[i]
        }
        // 필터 버튼에 리스너 세팅
        for (i in filterButtonList.indices) {
            filterButtonList[i].setOnClickListener {
                if (viewModel.filterLiveList.value!![i].checked) {
                    Log.d("check", "unselect")
                    viewModel.updateFilterCheck(i, false)
                } else {
                    Log.d("check", "select")
                    viewModel.updateFilterCheck(i, true)
                }
            }
        }
        // 필터데이터 옵저버
        for (a in 0 until viewModel.filterLiveList.value!!.size) {
            viewModel.filterLiveList.observe(this) { value ->
                for (i in 0 until value.size) {
                    if (value[i].checked) {
                        Log.d("observe", "select")
                        filterButtonList[i]
                            .setBackgroundResource(
                                R.drawable.filter_button_selected
                            )
                    } else {
                        Log.d("observe", "unselect")
                        filterButtonList[i]
                            .setBackgroundResource(
                                R.drawable.filter_button_around
                            )
                    }
                }
            }
        }
    }
    /**
     * 현재 운영 버튼 리스너
     */
    private fun setOperatingButton(){
        binding.operating1.setOnClickListener {
            viewModel.isToiletOperating.value = false
        }
        binding.operating2.setOnClickListener {
            viewModel.isToiletOperating.value = true
        }
        binding.operatingCircle1.setOnClickListener {
            viewModel.isToiletOperating.value = false
        }
        binding.operatingCircle2.setOnClickListener {
            viewModel.isToiletOperating.value = true
        }
    }
    /**
     * 현재 운영 데이터 옵저버
     */
    private fun setOperatingObserver(){
        viewModel.isToiletOperating.observe(this) { value ->
            if (value) {
                binding.operatingCircle1.setImageResource(
                    R.drawable.check_circle
                )
                binding.operatingCircle2.setImageResource(
                    R.drawable.checked_circle
                )
            } else {
                binding.operatingCircle1.setImageResource(
                    R.drawable.checked_circle
                )
                binding.operatingCircle2.setImageResource(
                    R.drawable.check_circle
                )
            }
        }
    }
    /**
     * 뒤로가기 버튼 리스너 세팅
     */
    private fun setBackButton(){

        binding.backButton.setOnClickListener {
            this.dismiss()
            // 뷰모델 초기 상태로
            viewModel.loadStatus()
        }
    }
    /**
     * 초기화 버튼 리스너 세팅
     */
    private fun setClearButton(){
        binding.clearButton.setOnClickListener {
            clearAll()
        }
    }
    /**
     * 화장실 보기 버튼 리스너 세팅
     */
    private fun setSearchToiletButton(){
        // 화장실 보기
        binding.searchToilet.setOnClickListener {
            dismiss()
            viewModel.isDialogDismissed.value = true
        }
    }
    /**
     * 초기화 버튼 리스너 세팅
     */
    private fun clearAll() {
        // 조건 필터 초기화
        for (i in 0 until viewModel.filterLiveList.value!!.size) {
            viewModel.updateFilterCheck(i, false)
        }
        // 현재운영 초기화
        viewModel.isToiletOperating.value = false
        // 최근 점검
        viewModel.toiletRecentCheck.value =
            viewModel.filterString.toiletCheckNever
    }
}