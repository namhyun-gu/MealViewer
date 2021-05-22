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
package com.earlier.yma.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.earlier.yma.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException

@AndroidEntryPoint
class MealFragment : Fragment() {
    private val mainVieWModel: MainViewModel by activityViewModels()
    private val mealViewModel: MealViewModel by viewModels()
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    val errorMessage = ObservableField("")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.apply {
            lifecycleOwner = this@MealFragment
            viewModel = mealViewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mealViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MealUiState.Success -> {
                    TODO()
                }
                is MealUiState.Error -> {
                    if (state.exception is HttpException) {
                        errorMessage.set("Can't receive meal (code: ${state.exception.code()})")
                    } else {
                        errorMessage.set("Can't receive meal")
                    }
                }
                else -> {
                    // No-op
                }
            }
        }

        mainVieWModel.uiState.observe(viewLifecycleOwner) { state ->
            mealViewModel.loadData(state.date)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(type: String): MealFragment {
            return MealFragment().apply {
                arguments = Bundle().apply {
                    putString("type", type)
                }
            }
        }
    }
}
