package com.dream.disabledtoilet_android.Utility.Dialog.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.dream.disabledtoilet_android.R
import com.dream.disabledtoilet_android.ToiletSearch.ViewModel.SortViewModel
import com.dream.disabledtoilet_android.databinding.DialogFilterBinding

class SortDialog: DialogFragment() {
    lateinit var binding: DialogFilterBinding
    lateinit var viewModel : SortViewModel

    /**
     * Dialog 바로 생성
     */
    companion object{
        fun newInstance() = SortDialog()
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
        viewModel = ViewModelProvider(requireActivity())[SortViewModel::class.java]
        // 초기 상태 저장
        viewModel.storeStatus()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFilterBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.filter_dialog_round)

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
    fun setUi(){
        setOKButton()
        //화장실 정렬
        setSortCheck()
        setSortCheckObserver()
    }

    /**
     * 확인 버튼 리스너 세팅
     */
    private fun setOKButton(){
        binding.cancelButton.setOnClickListener{
            dismiss()

            viewModel.isDialogDismissed.value = true
        }
    }

    /**
     * 정렬 기준 리스너 세팅
     */
    private fun setSortCheck(){

        //정렬 기준
        binding.sortByDistance.setOnClickListener {
            viewModel.SortCheck.value =
                viewModel.sortString.sortByDistance
        }
        binding.sortBySaved.setOnClickListener {
            viewModel.SortCheck.value =
                viewModel.sortString.sortBySaved
        }
        //텍스트
        binding.filterText1.setOnClickListener {
            viewModel.SortCheck.value =
                viewModel.sortString.sortByDistance
        }
        binding.filterText2.setOnClickListener {
            viewModel.SortCheck.value =
                viewModel.sortString.sortBySaved
        }
        //체크
        binding.check1.setOnClickListener {
            viewModel.SortCheck.value =
                viewModel.sortString.sortByDistance
        }
        binding.check2.setOnClickListener {
            viewModel.SortCheck.value =
                viewModel.sortString.sortBySaved
        }
    }
    /**
     * 정렬 기준 데이터 옵저버 세팅
     */
    private fun setSortCheckObserver(){
        viewModel.SortCheck.observe(this){value ->
            //value가 어떤 String인지 when으로 처리
            when(value){
                viewModel.sortString.sortByDistance -> {
                    //버튼 먼저 초기화
                    clearSortButton()
                    //버튼 보이게
                    binding.check1.visibility = View.VISIBLE
                    binding.filterText1.setTextColor(ContextCompat.getColor(requireContext(), R.color.subColor))
                }
                viewModel.sortString.sortBySaved -> {
                    clearSortButton()
                    binding.check2.visibility = View.VISIBLE
                    binding.filterText2.setTextColor(ContextCompat.getColor(requireContext(), R.color.subColor))
                }
            }
        }
    }
    /**
     * 정렬 버튼 UI 초기화
     */
    private fun clearSortButton(){
        binding.check1.visibility = View.INVISIBLE
        binding.check2.visibility = View.INVISIBLE

        binding.filterText1.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkerGrey))
        binding.filterText2.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkerGrey))
    }
}