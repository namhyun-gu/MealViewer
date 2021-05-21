package com.earlier.yma.ui.search

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import com.earlier.yma.data.model.School
import com.earlier.yma.databinding.ActivitySearchBinding
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchViewModel by viewModels()
    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val adapter by lazy { SearchAdapter(::navigateMain) }

    val errorMessage = ObservableField("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            binding.apply {
                lifecycleOwner = this@SearchActivity
                viewModel = viewModel
                adapter = adapter
            }.root
        )

        viewModel.uiState.observe(this) { state ->
            if (state is SearchUiState.Success) {
                adapter.submitList(state.schoolList)
            } else if (state is SearchUiState.Error) {
                if (state.exception is HttpException) {
                    errorMessage.set("Can't receive search result (code: ${state.exception.code()})")
                } else {
                    errorMessage.set("Can't receive search result")
                }
            }
        }
    }

    private fun navigateMain(school: School) {
        viewModel.saveSchool(school)
        Toast.makeText(this, school.name, Toast.LENGTH_SHORT).show()
    }
}