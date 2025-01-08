package com.dream.disabledtoilet_android.Detail

import ToiletModel
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailOptionFragment : Fragment() {

    private val TAG = "DetailOptionFragment"

    private var _binding: FragmentDetailOptionBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    //현재 화장실 데이터 저장
    private var currentToilet : ToiletModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        //현재 로그인된 사용자가 있다면 ViewModel 데이터 로드
        ToiletData.currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                userViewModel.loadUser(user.email)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailOptionBinding.inflate(inflater, container, false)
        binding.root.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        // 전달받은 화장실 데이터
        currentToilet = arguments?.getParcelable<ToiletModel>("TOILET_DATA")

        Log.d("test", "currentToilet : ${currentToilet}")
        currentToilet?.let { toilet ->
            setupUI(toilet)
            setupSaveButton(toilet)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // LiveData 관찰 설정
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            Log.d("test es", "User LiveData updated: $user")
        }

        userViewModel.likedToilets.observe(viewLifecycleOwner) { likedToilets ->
            Log.d("test es", "LikedToilets LiveData updated: $likedToilets")
            currentToilet?.let { toilet ->
                val isLiked = likedToilets?.contains(toilet.number) ?: false
                updateSaveIcon(binding.iconToggle, isLiked)
                updateSaveCount(toilet.save)
            }
        }


//        userViewModel.likedToilets.observe(viewLifecycleOwner){ likedToilets ->
//            currentToilet.let{
//                val isLiked = it?.let { it1 -> likedToilets.contains(it1.number) }
//
//                if (isLiked != null) {
//                    Log.d("test es click", isLiked.toString())
//                    Log.d("test es likedToilets", likedToilets.toString())
//
//                    updateSaveIcon(binding.iconToggle,isLiked)
//                    updateSaveCount(it.save)
//                }
//            }
//        }
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
        val saveTxt = binding.toiletSaveCount

        // 초기 상태 설정
        val currentUser = ToiletData.currentUser
        if (currentUser == null) {
            // 비로그인 상태 UI 설정
            updateSaveIcon(saveIcon, false)
            updateSaveCount(toilet.save)

            saveButton.setOnClickListener {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        // ViewModel에 사용자 데이터가 없다면 로드
        if (userViewModel.user.value == null) {
            CoroutineScope(Dispatchers.IO).launch {
                userViewModel.loadUser(currentUser.email)
            }
        }

        // 현재 좋아요 상태 확인 및 초기 UI 설정
        val isLiked = userViewModel.likedToilets.value?.contains(toilet.number)
            ?: currentUser.likedToilets?.contains(toilet.number)
            ?: false

        updateSaveIcon(saveIcon, isLiked)
        updateSaveCount(toilet.save)


        saveButton.setOnClickListener {
            if (ToiletData.currentUser == null) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            val currentLikedState = userViewModel.likedToilets.value?.contains(toilet.number) ?: false
            val currentLikedState = ToiletData.currentUser!!.likedToilets.contains(toilet.number) ?: false
            val newLikedState = !currentLikedState

            Log.d("test es", "Updating like status:")
            Log.d("test es", "Current state: $currentLikedState")
            Log.d("test es", "New state: $newLikedState")
            Log.d("test es", "Current liked toilets: ${userViewModel.likedToilets.value}")

            userViewModel.updateLikeStatus(toilet.number, newLikedState)

            // UI 즉시 업데이트 (LiveData 업데이트 전)
            updateSaveIcon(saveIcon, newLikedState)

            val message = if (newLikedState) "좋아요가 추가되었습니다." else "좋아요가 취소되었습니다."
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

    }


    private fun  updateSaveCount(count : Int){
        binding.toiletSaveCount.text = "저장 (${count})"
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
