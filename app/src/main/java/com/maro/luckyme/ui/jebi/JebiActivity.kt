package com.maro.luckyme.ui.jebi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maro.luckyme.R

class JebiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, JebiFragment.newInstance())
                .commitNow()
        }
    }
}