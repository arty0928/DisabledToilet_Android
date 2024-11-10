package com.example.disabledtoilet_android.Detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.disabledtoilet_android.databinding.FragmentFilterOptionBinding

class OptionAddToScrollView : Fragment() {

    private var _binding: FragmentFilterOptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterOptionBinding.inflate(inflater, container, false)

        // CustomButtonFragment 인스턴스 생성
        val customButtonFragment = FragmentFilterOption()

        // Fragment 트랜잭션으로 추가
        childFragmentManager.beginTransaction()
            .add(binding.linearLayout.id, customButtonFragment)
            .commit()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}