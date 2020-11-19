package com.maro.luckyme.ui.jebi

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maro.luckyme.R
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
            viewModel = this@JebiFragment.viewModel.apply {
                items.value = listOf(
                    JebiItem(R.drawable.ic_rat),
                    JebiItem(R.drawable.ic_cow),
                    JebiItem(R.drawable.ic_tiger),
                    JebiItem(R.drawable.ic_rabbit),
                    JebiItem(R.drawable.ic_dragon),
                    JebiItem(R.drawable.ic_snake),
                    JebiItem(R.drawable.ic_horse),
                    JebiItem(R.drawable.ic_sheep),
                    JebiItem(R.drawable.ic_monkey),
                    JebiItem(R.drawable.ic_chicken),
                    JebiItem(R.drawable.ic_dog),
                    JebiItem(R.drawable.ic_pig)
                )
            }
            lifecycleOwner = this@JebiFragment
        }

        return binding.root
    }
}