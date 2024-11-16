package com.example.disabledtoilet_android.ToiletPlus

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.ToiletPlus.ViewModel.PlusToiletViewModel
import com.example.disabledtoilet_android.Utility.Dialog.dialog.LoadingDialog
import com.example.disabledtoilet_android.Utility.KaKaoAPI.KakaoApiRepository
import com.example.disabledtoilet_android.Utility.KaKaoAPI.Model.AddressNameModel
import com.example.disabledtoilet_android.databinding.ActivityInputPlusToiletInfoBinding
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InputPlusToiletInputPageActivity : AppCompatActivity() {
    val Tag = "test log"
    private lateinit var binding: ActivityInputPlusToiletInfoBinding
    private val loadingDialog = LoadingDialog()
    // 도로명, 지번 주소 모델
    lateinit var addressNameModel: AddressNameModel
    // 뷰모델
    lateinit var viewModel: PlusToiletViewModel
    // 화장실 상태 선택 버튼 리스트
    lateinit var statusButtonList: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputPlusToiletInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 뷰모델 받기
        viewModel = ViewModelProvider(this)[PlusToiletViewModel::class.java]
        // 화장실 상태 선택 버튼 리스트 초기화
        statusButtonList = listOf<TextView>(
            binding.filter1,
            binding.filter2,
            binding.filter3,
            binding.filter4,
            binding.filter5,
            binding.filter6,
            binding.filter7,
            binding.filter8,
        )
        // 좌표 받아서 주소로 변환 비동기 처리
        lifecycleScope.launch(Dispatchers.Main){
            // 받아오는 동안 로딩
            showLoading()
            withContext(Dispatchers.IO){
                // 인텐트에서 좌표받아서 바로 주소로 변환
                addressNameModel = getRoadAddressFromCoordinate(getCoordinateFromIntent())
            }
            // UI 세팅
            setUi()
            // 로딩 제거
            dismissLoading()
        }
    }
    /**
     * Ui 세팅
     */
    private fun setUi(){
        // 뒤로 가기
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        // 주소값 넣어 주기, textWatcher 세팅
        setAddressEditText(getAddressName(addressNameModel))
        // 이름 EditText 세팅
        setToiletNameEditTxt()
        // 화장실 상태 선택 세팅
        setToiletStatusSelect(statusButtonList)
    }
    /**
     * intet에서 좌표값 갖고 오기
     */
    private fun getCoordinateFromIntent(): LatLng {
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        return LatLng.from(latitude, longitude)
    }
    /**
     * 좌표 값으로 주소 구하기
     */
    private suspend fun getRoadAddressFromCoordinate(location: LatLng): AddressNameModel{
        val kakaoRepository = KakaoApiRepository.KakaoLocalRepository()
        val callResult = kakaoRepository.getAddressFromCoordinate(location.longitude,location.latitude)
        var roadAddressName = ""
        var lotAddressName = ""
        // Call 성공 시
        callResult.onSuccess { response ->
            Log.d(Tag, response.toString())
            Log.d(Tag, response.documents.toString())
            // response.documents가 비어있지 않은 경우 첫 번째 요소 처리
            response.documents.firstOrNull()?.let { document ->
                // 지번 확인
                document.address?.let { address ->
                    lotAddressName = address.address_name
                }
                // 도로명 확인
                document.road_address?.let { roadAddress ->
                    roadAddressName = roadAddress.address_name
                }
            }
        }
        // 실패시
        callResult.onFailure {
            Log.d(Tag, "좌표 주소 변환 실패")
        }
        Log.d(Tag, "도로명 주소: $roadAddressName 지번 주소: $lotAddressName")
        // AddressNameModel 만들어서 return
        return AddressNameModel(roadAddressName, lotAddressName)
    }
    /**
     * 로딩 표시
     */
    private fun showLoading(){
        loadingDialog.show(supportFragmentManager, loadingDialog.tag)
    }
    /**
     * 로딩 끄기
     */
    private fun dismissLoading(){
        loadingDialog.dismiss()
    }
    /**
     * AddressNameModel에서 있는 값 사용
     */
    private fun getAddressName(addressNameModel: AddressNameModel): String{
        var result = ""
        if (addressNameModel.roadAddressName == ""){
            result = addressNameModel.lotAddressName
        } else{
            result = addressNameModel.roadAddressName
        }
        return result
    }
    /**
     *  주소 EditText 세팅
     */
    private fun setAddressEditText(address: String){
        binding.toiletAddressEdit.setText(address)
        viewModel.setToiletAddress(address)
        binding.toiletAddressEdit.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 텍스트 값 바뀌면 바로 뷰모델에 적용
                viewModel.setToiletAddress(
                    binding.toiletAddressEdit.text.toString()
                )
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }
    /**
     * 화장실 이름 EditText 세팅
     */
    private fun setToiletNameEditTxt(){
        binding.plusToiletInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 텍스트 값 바뀌면 바로 뷰모델에 적용
                viewModel.setToiletAddress(
                    binding.toiletAddressEdit.text.toString()
                )
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }
    /**
     * 화장실 상태 선택 세팅
     */
    private fun setToiletStatusSelect(buttonList: List<TextView>){
        val clicked = R.drawable.filter_button_selected
        val unClicked = R.drawable.toilet_status_nonselected_button
        // UI 버튼 개수와 뷰모델 실제 버튼 개수 비교
        if (buttonList.size == viewModel.statusStringList.size){
            // 개수 비교 통과 시, 한번에 뷰모델, 버튼 연결
            for (i in buttonList.indices){
                // UI 버튼 텍스트 세팅
                buttonList[i].text = viewModel.statusStringList[i]
                val viewModelToiletList = viewModel.toiletStatusList.value!!
                // UI 클릭 시, 뷰모델 데이터 변경
                buttonList[i].setOnClickListener{
                    // 원래 status 확인 후, 리스트 Update
                    if (viewModelToiletList.get(i).status){
                        viewModel.updateToiletStatusSelectList(
                            viewModelToiletList,
                            i,
                            false
                        )
                    } else{
                        viewModel.updateToiletStatusSelectList(
                            viewModelToiletList,
                            i,
                            true
                        )
                    }
                }
                // 뷰모델 데이터 변경 시, UI 세팅
                viewModel.toiletStatusList.observe(this){
                    if (viewModelToiletList.get(i).status){
                        buttonList[i].setBackgroundResource(clicked)
                    } else{
                        buttonList[i].setBackgroundResource(unClicked)
                    }
                }
            }
        }
    }
}