package com.maro.luckyme.ui.sadari

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maro.luckyme.databinding.SadariFragmentBinding

class SadariFragment : Fragment() {

    companion object {
        fun newInstance() = SadariFragment()
    }

    private lateinit var viewModel: SadariViewModel
    private lateinit var binding: SadariFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            SadariViewModel::class.java)

        binding = SadariFragmentBinding.inflate(inflater, null, false).apply {
            viewModel = this@SadariFragment.viewModel
            lifecycleOwner = this@SadariFragment
        }

        return binding.root
    }
}