package com.example.disabledtoilet_android.ToiletSearch.SearchFilter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.databinding.DialogFilterBinding
import com.example.disabledtoilet_android.databinding.FilterSearchDialogBinding

class FilterSearchDialog : DialogFragment() {
    lateinit var binding: FilterSearchDialogBinding
    lateinit var viewModel: FilterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterSearchDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(
            R.drawable.filter_dialog_round
        )
        viewModel = ViewModelProvider(this).get(FilterViewModel::class.java)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        isCancelable = false

        setUi()
    }

    fun clearRecentButton() {
        binding.circle1.setImageResource(R.drawable.check_circle)
        binding.circle2.setImageResource(R.drawable.check_circle)
        binding.circle3.setImageResource(R.drawable.check_circle)
        binding.circle4.setImageResource(R.drawable.check_circle)
    }

    fun applyFilterTextView(textView: TextView) {
    }

    fun setUi() {
        // 뒤록가기
        binding.backButton.setOnClickListener {
            this.dismiss()
        }

        // 화장실 최근 점검
        binding.circle1.setOnClickListener {
            viewModel.toiletRecentCheck.value =
                viewModel.filterString.toiletCheckNever
        }
        binding.circle2.setOnClickListener {
            viewModel.toiletRecentCheck.value =
                viewModel.filterString.toiletCheckInYear
        }
        binding.circle3.setOnClickListener {
            viewModel.toiletRecentCheck.value =
                viewModel.filterString.toiletCheckHalfYear
        }
        binding.circle4.setOnClickListener {
            viewModel.toiletRecentCheck.value =
                viewModel.filterString.toiletCheckInMonth
        }
        viewModel.toiletRecentCheck.observe(this) { value ->
            when (value) {
                viewModel.filterString.toiletCheckNever -> {
                    clearRecentButton()
                    binding.circle1.setImageResource(R.drawable.checked_circle)
                }

                viewModel.filterString.toiletCheckInYear -> {
                    clearRecentButton()
                    binding.circle2.setImageResource(R.drawable.checked_circle)
                }

                viewModel.filterString.toiletCheckHalfYear -> {
                    clearRecentButton()
                    binding.circle3.setImageResource(R.drawable.checked_circle)
                }

                viewModel.filterString.toiletCheckInMonth -> {
                    clearRecentButton()
                    binding.circle4.setImageResource(R.drawable.checked_circle)
                }
            }
        }

        // 현재 운영
        binding.operatingCircle1.setOnClickListener {
            viewModel.isToiletOperating.value = false
        }
        binding.operatingCircle2.setOnClickListener {
            viewModel.isToiletOperating.value = true
        }
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


        // 조건 적용
        val filterButtonList: List<TextView> = listOf(
            binding.filter1, binding.filter2,
            binding.filter3, binding.filter4,
            binding.filter5, binding.filter6,
            binding.filter7, binding.filter8
        )
        for(i in 0 until filterButtonList.size){
            filterButtonList[i].text = viewModel.filterString.filterNameList[i]
        }
        for (i in 0 until filterButtonList.size) {
            filterButtonList.get(i).setOnClickListener {
                if (viewModel.filterLiveList.value!!.get(i).checked) {
                    Log.d("check", "unselect")
                    viewModel.updateFilterCheck(i, false)
                } else {
                    Log.d("check", "select")
                    viewModel.updateFilterCheck(i, true)
                }
            }
        }
        for (i in 0 until viewModel.filterLiveList.value!!.size) {
            viewModel.filterLiveList.observe(this) { value ->
                for (i in 0 until value.size) {
                    if (value.get(i).checked) {
                        Log.d("observe", "select")
                        filterButtonList.get(i)
                            .setBackgroundResource(
                                R.drawable.filter_button_selected
                            )
                    } else {
                        Log.d("observe", "unselect")
                        filterButtonList.get(i)
                            .setBackgroundResource(
                                R.drawable.filter_button_around
                            )
                    }
                }
            }
        }

        // 초기화
        binding.clearButton.setOnClickListener {
            clearAll()
        }

        // 화장실 보기
        binding.searchToilet.setOnClickListener {
            dismiss()
        }

    }

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

class FilterViewModel : ViewModel() {
    val filterString = FilterString()
    var toiletRecentCheck = MutableLiveData<Int>()
    var isToiletOperating = MutableLiveData<Boolean>()
    private var filterList = mutableListOf<FilterModel>()
    var filterLiveList = MutableLiveData<MutableList<FilterModel>>()

    init {
        filterLiveList.value = filterList
        // filterList 초기화
        for (i in 0 until filterString.filterNameList.size) {
            filterList.add(
                FilterModel(
                    filterString.filterNameList.get(i),
                    false
                )
            )
        }

        toiletRecentCheck.value = filterString.toiletCheckNever
        isToiletOperating.value = false
    }

    fun updateFilterCheck(index: Int, isChecked: Boolean) {
        filterList[index].checked = isChecked
        filterLiveList.value = filterList
    }


    data class FilterString(
        val toiletCheckNever: Int = 0,
        val toiletCheckInYear: Int = 1,
        val toiletCheckHalfYear: Int = 3,
        val toiletCheckInMonth: Int = 4,
        val filterNameList: List<String> = listOf(
            "장애인 소변기",
            "장애인 대변기",
            "비상벨",
            "입구 CCTV",
            "개방화장실",
            "공중화장실",
            "민간소유",
            "공공기관"
        )

    )

    data class FilterModel(
        var filterName: String,
        var checked: Boolean
    )
}