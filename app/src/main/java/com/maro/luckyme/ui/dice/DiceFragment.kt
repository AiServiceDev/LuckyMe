package com.maro.luckyme.ui.dice

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maro.luckyme.databinding.DiceFragmentBinding

class DiceFragment : Fragment() {

    companion object {
        fun newInstance() = DiceFragment()
    }

    private lateinit var viewModel: DiceViewModel
    private lateinit var binding: DiceFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            DiceViewModel::class.java)

        binding = DiceFragmentBinding.inflate(inflater, null, false).apply {
            viewModel = this@DiceFragment.viewModel
            lifecycleOwner = this@DiceFragment
        }

        return binding.root
    }
}