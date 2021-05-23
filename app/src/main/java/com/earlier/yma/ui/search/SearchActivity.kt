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
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.earlier.yma.R
import com.earlier.yma.data.model.School
import com.earlier.yma.databinding.ActivitySearchBinding
import com.earlier.yma.util.setOnActionSearch
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchViewModel by viewModels()
    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val adapter by lazy { SearchAdapter(::navigateMain) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        binding.adapter = adapter
        binding.viewModel = viewModel
        binding.errorMessage = ""

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.edtKeyword.setOnActionSearch {
            hideKeyboard()
            viewModel.search(it)
        }

        binding.schoolList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore()
                }
            }
        })

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is SearchUiState.Success -> {
                    updateFilterChipGroup(state.orgList, state.filterOrg)
                    updateList(state.schoolList, state.filterOrg)
                }
                is SearchUiState.Error -> {
                    binding.errorMessage =
                        if (state.exception is HttpException) {
                            getString(R.string.msg_search_error_code, state.exception.code())
                        } else {
                            getString(R.string.msg_search_error)
                        }
                }
                else -> {
                    // No-op
                }
            }
        }

        viewModel.loadMoreError.observe(this) {
            Snackbar.make(
                binding.root,
                getString(R.string.msg_search_no_more),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    fun updateList(schoolList: List<School>, filterOrg: Set<String>) {
        val filterList = if (filterOrg.isNotEmpty()) {
            schoolList.filter { filterOrg.contains(it.orgName) }
        } else {
            schoolList
        }

        adapter.submitList(filterList)
    }

    fun updateFilterChipGroup(orgList: List<String>, filterOrg: Set<String>) {
        binding.filterChipGroup.removeAllViews()

        val allFilterChip = makeFilterChip(
            getString(R.string.filter_all),
            "",
            filterOrg.isEmpty()
        )
        allFilterChip.setOnClickListener { clearFilter() }
        binding.filterChipGroup.addView(allFilterChip)

        orgList.map {
            makeFilterChip(it, it, filterOrg.contains(it))
        }.forEach { chip ->
            chip.setOnClickListener {
                updateFilter()
            }
            binding.filterChipGroup.addView(chip)
        }
    }

    fun makeFilterChip(text: String, org: String, checked: Boolean): Chip {
        return Chip(this).apply {
            id = ViewCompat.generateViewId()
            tag = org
            setText(text)
            isCheckable = true
            isChecked = checked
        }
    }

    fun updateFilter() {
        val ids = binding.filterChipGroup.checkedChipIds
        val checkedOrg = mutableSetOf<String>()
        ids.forEach {
            val chip = findViewById<Chip>(it)
            val org = chip.tag as String
            checkedOrg.add(org)
        }
        viewModel.updateFilter(checkedOrg)
    }

    fun clearFilter() {
        viewModel.updateFilter(emptySet())
    }

    fun navigateMain(school: School) {
        viewModel.saveSchool(school)
        Toast.makeText(this, school.name, Toast.LENGTH_SHORT).show()
    }

    fun hideKeyboard() {
        val inputMethodManager = getSystemService<InputMethodManager>()
        inputMethodManager?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}
