package com.maro.luckyme.ui.jebi

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maro.luckyme.databinding.JebiFragmentBinding

class JebiFragment : Fragment() {

    companion object {
        fun newInstance() = JebiFragment()
    }

    private lateinit var viewModel: JebiViewModel
    private lateinit var binding: JebiFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            JebiViewModel::class.java)

        binding = JebiFragmentBinding.inflate(inflater, null, false).apply {
            viewModel = this@JebiFragment.viewModel
            lifecycleOwner = this@JebiFragment
        }

        return binding.root
    }
}