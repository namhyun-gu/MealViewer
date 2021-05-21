package com.earlier.yma.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.earlier.yma.databinding.ActivityMainBinding
import com.earlier.yma.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            binding.apply {
                lifecycleOwner = this@MainActivity
                activity = this@MainActivity
                viewModel = viewModel
                adapter = MainPagerAdapter(this@MainActivity)
            }.root
        )
    }

    override fun onBackPressed() {
        if (binding.pager.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.pager.currentItem -= 1
        }
    }

    fun onTypeChanged() {
        val position = binding.pager.currentItem
        val types = arrayOf(
            Constants.TYPE_LUNCH,
            Constants.TYPE_DINNER
        )

        viewModel.updateType(types[position])
    }
}