package com.maro.luckyme.ui.user

import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.maro.luckyme.R
import com.maro.luckyme.data.common.UserUiData
import com.maro.luckyme.databinding.UserChipItemBinding
import com.maro.luckyme.databinding.UserConfirmFragmentBinding
import com.maro.luckyme.ui.dice.DiceViewModel


class UserConfirmDialog : DialogFragment() {

    companion object {
        fun newInstance() = UserConfirmDialog()
    }

    private lateinit var viewModel: UserConfirmViewModel
    private lateinit var binding: UserConfirmFragmentBinding
    private val parentViewModel: DiceViewModel? by lazy {
        parentFragment?.let {
            ViewModelProvider(it, ViewModelProvider.NewInstanceFactory()).get(
                DiceViewModel::class.java
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.let {
            it.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    activity?.finish()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            it.setCanceledOnTouchOutside(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            UserConfirmViewModel::class.java
        )

        binding = UserConfirmFragmentBinding.inflate(inflater, null, false).apply {
            viewModel = this@UserConfirmDialog.viewModel
            lifecycleOwner = viewLifecycleOwner
            rvResult.apply {
                adapter = UserListAdapter(
                    this@UserConfirmDialog.viewLifecycleOwner
                )
                layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
                itemAnimator = DefaultItemAnimator()
                val dp4 = resources.getDimensionPixelOffset(R.dimen.dp4)
                val dp8 = resources.getDimensionPixelOffset(R.dimen.dp8)

                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        super.getItemOffsets(outRect, view, parent, state)
                        val position: Int = parent.getChildAdapterPosition(view)
                        when (position % 3) {
                            0 -> outRect.set(dp8, dp8, dp4, 0)
                            2 -> outRect.set(dp4, dp8, dp8, 0)
                            else -> {
                                outRect.set(dp4, dp8, dp4, 0)
                            }
                        }
                    }
                })
            }
            executePendingBindings()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    private fun setObserver() {
        viewModel.apply {
            userDataList.observe(viewLifecycleOwner) {
                (binding.rvResult.adapter as? UserListAdapter)?.submitList(it)
            }

            start.observe(viewLifecycleOwner) {
                parentViewModel?.generatorNextDiceData()
                dismissAllowingStateLoss()
            }

            totalUserCount.observe(viewLifecycleOwner) {
                parentViewModel?.totalUserCount?.postValue(it)
            }

            penaltyWinnerCount.observe(viewLifecycleOwner) {
                parentViewModel?.penaltyWinningCount?.postValue(it)
            }

            diceCount.observe(viewLifecycleOwner) {
                parentViewModel?.diceCount?.postValue(it)
            }

            gameRule.observe(viewLifecycleOwner) {
                parentViewModel?.gameRule?.value = it
            }
        }
    }

}

class UserListAdapter(
    private val lifecycleOwner: LifecycleOwner
) :
    ListAdapter<UserUiData, UserViewHolder>(UserConfirmAdapterDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            binding = UserChipItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            lifecycleOwner
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.onBindView(getItem(position))
    }
}

object UserConfirmAdapterDiffUtil : DiffUtil.ItemCallback<UserUiData>() {
    override fun areItemsTheSame(oldItem: UserUiData, newItem: UserUiData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserUiData, newItem: UserUiData): Boolean {
        return oldItem == newItem
    }
}

class UserViewHolder(
    val binding: UserChipItemBinding,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBindView(userUiData: UserUiData) {
        binding.apply {
            position = userUiData.index
            lifecycleOwner = this@UserViewHolder.lifecycleOwner
            executePendingBindings()
        }
    }
}