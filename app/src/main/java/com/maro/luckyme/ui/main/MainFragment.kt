package com.maro.luckyme.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.maro.luckyme.databinding.MainFragmentBinding
import com.maro.luckyme.ui.dice.DiceActivity
import com.maro.luckyme.ui.jebi.JebiActivity
import com.maro.luckyme.ui.sadari.SadariActivity

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        binding = MainFragmentBinding.inflate(inflater, null, false).apply {
            viewModel = this@MainFragment.viewModel
            lifecycleOwner = this@MainFragment
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.sadariEvent.observe(viewLifecycleOwner, Observer {
            startActivity(Intent(context, SadariActivity::class.java))
        })

        viewModel.diceEvent.observe(viewLifecycleOwner, Observer {
            startActivity(Intent(context, DiceActivity::class.java))
        })

        viewModel.jebiEvent.observe(viewLifecycleOwner, Observer {
            startActivity(Intent(context, JebiActivity::class.java))
        })
    }
}