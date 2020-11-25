package com.maro.luckyme.ui.dice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maro.luckyme.R

class DiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DiceFragment.newInstance())
                .commitNow()
        }
    }

    override fun onBackPressed() {
        if (!onBackPressedDispatcher.hasEnabledCallbacks()) {
            onBackPressedDispatcher.onBackPressed()
            return
        }
        super.onBackPressed()
    }
}