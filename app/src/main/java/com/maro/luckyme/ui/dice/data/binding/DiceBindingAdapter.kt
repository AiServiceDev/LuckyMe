package com.maro.luckyme.ui.dice.data.binding

import android.animation.Animator
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.maro.luckyme.data.common.CommonData
import com.maro.luckyme.ui.dice.DiceViewModel
import com.maro.luckyme.ui.dice.data.DiceUiData
import kotlin.random.Random


/**
 * Created by smartman99@ncsoft.com on 2020/11/19.
 *
 */
class DiceBindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter("diceUiData", "diceId", "diceViewModel")
        fun setDiceUiByStatus(
            view: LottieAnimationView,
            diceUiData: DiceUiData?,
            diceId: Int?,
            diceViewModel: DiceViewModel?
        ) {
            diceUiData?.run {
                diceViewModel?.let {
                    diceId?.let {
                        val diceData = diceViewModel.getDiceData(this, diceId)
                        diceData?.let {
                            view.setAnimation(
                                when (diceData.resultValue) {
                                    1 -> "dice/touzidice1.json"
                                    2 -> "dice/touzidice2.json"
                                    3 -> "dice/touzidice3.json"
                                    4 -> "dice/touzidice4.json"
                                    5 -> "dice/touzidice5.json"
                                    6 -> "dice/touzidice6.json"
                                    else -> "dice/touzidice_random.json"
                                }
                            )
                            view.progress = when (diceData.type) {
                                DiceViewModel.DiceStatusType.SHUFFLE -> {
                                    0f
                                }
                                else -> {
                                    view.repeatMode = LottieDrawable.REVERSE
                                    1f
                                }
                            }
                            view.removeAllAnimatorListeners()
                            if (diceData.type == DiceViewModel.DiceStatusType.RESULT) return
                            view.repeatCount = 0
                            view.speed = (Random.nextLong(9L, 18L) / 10f)
                            view.addAnimatorListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator?) {
                                }

                                override fun onAnimationEnd(animation: Animator?) {
                                    diceData.type = DiceViewModel.DiceStatusType.RESULT
                                    diceViewModel.checkFinishDicesRolling(diceId)
                                }

                                override fun onAnimationCancel(animation: Animator?) {
                                }

                                override fun onAnimationRepeat(animation: Animator?) {
                                }
                            })
                            view.playAnimation()
                        }
                    }
                }
            }
        }

        @JvmStatic
        @BindingAdapter("kanjiSrc")
        fun setKanjiImageDrawable(view: AppCompatImageView, position: Int?) {
            position?.run {
                CommonData.get12KanjiListByIndex(position)?.let {
                    view.setImageResource(
                        it
                    )
                }
            }
        }
    }
}