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
        private const val DEFAULT_TOTAL= 4 // 기본 인원수
        private const val DEFAULT_WINNING = 2 // 기본 꽝 개수

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
                // initPlain(DEFAULT_WINNING, DEFAULT_TOTAL) // 밋밋한 버전. 임의 지정
                // initCoroutine(DEFAULT_WINNING, DEFAULT_TOTAL) // 코루틴 버전
                initFlow(DEFAULT_WINNING, DEFAULT_TOTAL) // Flow 버전
            }
            lifecycleOwner = this@JebiFragment
        }

        return binding.root
    }
}