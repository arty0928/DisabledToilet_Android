package com.example.disabledtoilet_android.ToiletSearch.SearchFilter

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.databinding.FilterSearchDialogBinding

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
        // 최근 점검
        setCurrentCheck()
        setRecentCheckObserver()
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
     * 최근 점검 리스너 세팅
     */
    private fun setCurrentCheck(){
        // 화장실 최근 점검
        binding.recentCheck1.setOnClickListener {
            viewModel.toiletRecentCheck.value =
                viewModel.filterString.toiletCheckNever
        }
        binding.recentCheck2.setOnClickListener {
            viewModel.toiletRecentCheck.value =
                viewModel.filterString.toiletCheckInYear
        }
        binding.recentCheck3.setOnClickListener {
            viewModel.toiletRecentCheck.value =
                viewModel.filterString.toiletCheckHalfYear
        }
        binding.recentCheck4.setOnClickListener {
            viewModel.toiletRecentCheck.value =
                viewModel.filterString.toiletCheckInMonth
        }
    }
    /**
     * 최근 점검 데이터 옵저버 세팅
     */
    private fun setRecentCheckObserver(){
        viewModel.toiletRecentCheck.observe(this) { value ->
            // value가 어떤 String인지 when으로 처리
            when (value) {
                viewModel.filterString.toiletCheckNever -> {
                    // 버튼 먼저 초기화 후
                    clearRecentButton()
                    // 버튼 색 칠하기
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
    }
    /**
     * 최근 점검 버튼 UI 초기화
     */
    private fun clearRecentButton() {
        binding.circle1.setImageResource(R.drawable.check_circle)
        binding.circle2.setImageResource(R.drawable.check_circle)
        binding.circle3.setImageResource(R.drawable.check_circle)
        binding.circle4.setImageResource(R.drawable.check_circle)
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
/**
 * FilterSearchDialog에 사용하는 ViewModel
 * FilterString을 통해서 조건 적용에 들어가는 텍스트까지 관리
 */
class FilterViewModel : ViewModel() {
    // filterString dataClass 초기화
    val filterString = FilterString()
    // Dialog 현재 띄워져있는지 데이터
    var isDialogDismissed = MutableLiveData<Boolean>()
    // 화장실 최근점검 데이터
    var toiletRecentCheck = MutableLiveData<Int>()
    // 현재 운영 데이터
    var isToiletOperating = MutableLiveData<Boolean>()
    // 조건 적용 데이터 리스트
    private var filterList = mutableListOf<FilterModel>()
    // 조건 적용 데이터 라이브 데이터
    var filterLiveList = MutableLiveData<MutableList<FilterModel>>()
    //storeData() 호출 시 데이터 상태 저장 정보
    private var savedStatus: FilterStatus? = null

    init {
        // filterLiveList 초기화
        filterLiveList.value = filterList
        // filterList 초기화
        for (i in 0 until filterString.filterNameList.size) {
            filterList.add(
                FilterModel(
                    filterString.filterNameList[i],
                    false
                )
            )
        }
        toiletRecentCheck.value = filterString.toiletCheckNever
        isToiletOperating.value = false
    }
    /**
     * 조건 적용에서 update를 위한 함수
     */
    fun updateFilterCheck(index: Int, isChecked: Boolean) {
        filterList[index].checked = isChecked
        filterLiveList.value = filterList

        viewModelScope
    }
    /**
     * 지정된 값 이용하기 위한 data class
     */
    data class FilterString(
        val toiletCheckNever: Int = 0,
        val toiletCheckInYear: Int = 1,
        val toiletCheckHalfYear: Int = 3,
        val toiletCheckInMonth: Int = 4,
        // 필터 이름(조건적용) 리스트
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
    /**
     * 조건 적용에 사용되는 데이터 클래스
     */
    data class FilterModel(
        var filterName: String,
        var checked: Boolean
    )
    /**
     *  필터 상태 저장을 위한 데이터 클래스
     */
    data class FilterStatus(
        val filterCheckedStates: List<Boolean>,
        val toiletRecentCheckValue: Int,
        val isToiletOperatingValue: Boolean
    )
    /**
     * 조건 검색 show 되었을 때 값 store
     */
    fun storeStatus() {
        savedStatus = FilterStatus(
            filterCheckedStates = filterList.map { it.checked },
            toiletRecentCheckValue = toiletRecentCheck.value ?: filterString.toiletCheckNever,
            isToiletOperatingValue = isToiletOperating.value ?: false
        )
    }
    /**
     * storeStatus() 호출 당시 데이터로 load
     */
    fun loadStatus() {
        savedStatus?.let { status ->
            // filterList 복원
            status.filterCheckedStates.forEachIndexed { index, checked ->
                filterList[index].checked = checked
            }
            filterLiveList.value = filterList

            // 다른 상태값들 복원
            toiletRecentCheck.value = status.toiletRecentCheckValue
            isToiletOperating.value = status.isToiletOperatingValue
        }
    }
}