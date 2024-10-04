package com.example.disabledtoilet_android.ToiletSearch.SearchFilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.databinding.DialogFilterBinding
import com.example.disabledtoilet_android.databinding.FilterSearchDialogBinding

class FilterSearchDialog: DialogFragment() {
    lateinit var binding: FilterSearchDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterSearchDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.filter_dialog_round)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        isCancelable = false

        setUi()
    }

    fun setUi(){
        binding.backButton.setOnClickListener{
            this.dismiss()
        }
    }

}