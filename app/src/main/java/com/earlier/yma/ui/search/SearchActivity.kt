/*
 * Copyright 2021 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.earlier.yma.ui.search

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import com.earlier.yma.data.model.School
import com.earlier.yma.databinding.ActivitySearchBinding
import com.earlier.yma.util.setOnActionSearch
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchViewModel by viewModels()
    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val adapter by lazy { SearchAdapter(this::navigateMain) }

    val errorMessage = ObservableField(" ")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            binding.apply {
                lifecycleOwner = this@SearchActivity
                viewModel = viewModel
                adapter = adapter
            }.root
        )

        binding.edtKeyword.setOnActionSearch(viewModel::search)

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is SearchUiState.Success -> {
                    adapter.submitList(state.schoolList)
                }
                is SearchUiState.Error -> {
                    if (state.exception is HttpException) {
                        errorMessage.set("Can't receive search result (code: ${state.exception.code()})")
                    } else {
                        errorMessage.set("Can't receive search result")
                    }
                }
                else -> {
                    // No-op
                }
            }
        }
    }

    private fun navigateMain(school: School) {
        viewModel.saveSchool(school)
        Toast.makeText(this, school.name, Toast.LENGTH_SHORT).show()
    }
}
