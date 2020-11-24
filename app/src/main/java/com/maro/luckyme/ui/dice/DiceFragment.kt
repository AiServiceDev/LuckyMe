package com.maro.luckyme.ui.dice

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.maro.luckyme.R
import com.maro.luckyme.databinding.DiceFragmentBinding
import com.maro.luckyme.databinding.ItemDiceBinding
import com.maro.luckyme.ui.dice.data.DiceUiData
import com.maro.luckyme.ui.user.UserConfirmDialog
import com.maro.luckyme.ui.user.UserConfirmViewModel


class DiceFragment : Fragment() {

    companion object {
        fun newInstance() = DiceFragment()
    }

    private lateinit var viewModel: DiceViewModel
    private lateinit var binding: DiceFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            DiceViewModel::class.java
        )

        binding = DiceFragmentBinding.inflate(inflater, null, false).apply {
            viewModel = this@DiceFragment.viewModel
            lifecycleOwner = this@DiceFragment
            rvResult.apply {
                adapter = DiceResultAdapter(
                    this@DiceFragment.viewModel,
                    this@DiceFragment.viewLifecycleOwner
                )
                layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
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
                        val position: Int = parent.getChildAdapterPosition(view)
                        when (position % 2) {
                            0 -> outRect.set(dp8, dp8, dp4, 0)
                            else -> outRect.set(dp4, dp8, dp8, 0)
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
        showUserConfirmDialog()
    }

    private fun showUserConfirmDialog() {
        UserConfirmDialog.newInstance()
            .show(childFragmentManager, UserConfirmViewModel::class.java.simpleName)
    }

    private fun setObserver() {
        viewModel.apply {
            currentDataList.observe(viewLifecycleOwner) {
                (binding.rvResult.adapter as? DiceResultAdapter)?.submitList(it)
            }
            scrollToEnd.observe(viewLifecycleOwner) {
                binding.rvResult.adapter?.itemCount?.let {
                    binding.rvResult.smoothScrollToPosition(it)
                }
            }
            retry.observe(viewLifecycleOwner) {
                showUserConfirmDialog()
                (binding.rvResult.adapter as? DiceResultAdapter)?.submitList(null)
            }
        }
    }
}

class DiceResultAdapter(
    private val viewModel: DiceViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    ListAdapter<DiceUiData, BaseDiceViewHolder>(DiceResultAdapterDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDiceViewHolder {
        return DiceViewHolder(
            viewModel,
            binding = ItemDiceBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            lifecycleOwner
        )
    }

    override fun onBindViewHolder(holder: BaseDiceViewHolder, position: Int) {
        holder.onBindView(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }
}

object DiceResultAdapterDiffUtil : DiffUtil.ItemCallback<DiceUiData>() {
    override fun areItemsTheSame(oldItem: DiceUiData, newItem: DiceUiData): Boolean {
        return oldItem == newItem && oldItem.viewType == newItem.viewType
    }

    override fun areContentsTheSame(oldItem: DiceUiData, newItem: DiceUiData): Boolean {
        return oldItem == newItem
    }
}

abstract class BaseDiceViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    abstract fun onBindView(diceDataList: DiceUiData)
}

class DiceViewHolder(
    private val viewModel: DiceViewModel,
    val binding: ItemDiceBinding,
    private val lifecycleOwner: LifecycleOwner
) :
    BaseDiceViewHolder(binding.root) {
    override fun onBindView(diceDataList: DiceUiData) {
        binding.apply {
            viewModel = this@DiceViewHolder.viewModel
            diceUiData = diceDataList
            position = adapterPosition
            lifecycleOwner = this@DiceViewHolder.lifecycleOwner
            executePendingBindings()
        }
    }
}