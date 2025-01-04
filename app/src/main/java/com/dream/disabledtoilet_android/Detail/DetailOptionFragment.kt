package com.dream.disabledtoilet_android.Detail

import ToiletModel
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dream.disabledtoilet_android.R
import com.dream.disabledtoilet_android.ToiletSearch.ToiletData
import com.dream.disabledtoilet_android.User.ViewModel.UserViewModel
import com.dream.disabledtoilet_android.databinding.FragmentDetailOptionBinding

class DetailOptionFragment : Fragment() {

    private val TAG = "DetailOptionFragment"

    private var _binding: FragmentDetailOptionBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailOptionBinding.inflate(inflater, container, false)

        binding.root.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        // 전달받은 화장실 데이터
        val toiletData = arguments?.getParcelable<ToiletModel>("TOILET_DATA")

        toiletData?.let { toilet ->
            setupUI(toilet)
            setupSaveButton(toilet)
        }

        return binding.root
    }

    private fun setupUI(toilet: ToiletModel) {
        // 기본 정보 표시
        binding.toiletName.text = toilet.restroom_name
        binding.toiletLocationAddress.text = toilet.address_road
        binding.toiletManageOfficeNumber.text = toilet.phone_number ?: "-"

        copyAddress(toilet)
        clickPhoneNumber(toilet)
    }

    /**
     * 전화번호 클릭시 키패드 연결
     */
    private fun clickPhoneNumber(toilet: ToiletModel){
        // 전화번호 클릭 이벤트
        binding.toiletManageOfficeNumber.setOnClickListener {
            val number = toilet.phone_number
            if (!number.isNullOrEmpty()) {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$number")
                }
                startActivity(dialIntent)
            } else {
                Toast.makeText(requireContext(), "전화번호가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 주소 클릭시 복사
     */
    private fun copyAddress(toilet: ToiletModel){
        // 주소 복사 기능
        binding.copyAddressIcon.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("address", toilet.address_road)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "주소가 복사되었습니다!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSaveButton(toilet: ToiletModel) {
        val saveButton = binding.saveBtn3
        val saveIcon = binding.iconToggle

        // 초기 상태 설정
        val isLiked = ToiletData.currentUser?.likedToilets?.contains(toilet.number) == true
        updateSaveIcon(saveIcon, isLiked)

        // 저장 버튼 클릭 이벤트
        saveButton.setOnClickListener {
            val currentUser = ToiletData.currentUser
            if (currentUser == null) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newLikedState = !isLiked

            // 로컬 데이터 업데이트
            ToiletData.updateSaveValueForToilet(toilet.number, currentUser.email, newLikedState)

            // Firebase 동기화
            ToiletData.updateUserLikes(toilet.number, newLikedState)
            ToiletData.syncToFirebase()

            // UI 업데이트
            updateSaveIcon(saveIcon, newLikedState)

            // 사용자에게 피드백
            val message = if (newLikedState) "좋아요가 추가되었습니다." else "좋아요가 취소되었습니다."
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSaveIcon(saveIcon: ImageView, isLiked: Boolean) {
        saveIcon.setImageResource(
            if (isLiked) R.drawable.saved_star_icon else R.drawable.save_icon
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
