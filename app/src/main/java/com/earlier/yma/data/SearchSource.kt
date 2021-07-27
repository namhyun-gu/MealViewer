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
package com.earlier.yma.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.earlier.yma.data.model.School
import com.earlier.yma.data.remote.NeisService
import com.earlier.yma.data.remote.ResponseParser
import com.earlier.yma.util.EmptyResponseException
import com.skydoves.sandwich.getOrThrow

class SearchSource(val neisService: NeisService, val keyword: String) :
    PagingSource<Int, School>() {
    override fun getRefreshKey(state: PagingState<Int, School>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, School> {
        return try {
            val page = params.key ?: 1
            val response = neisService.search(keyword, page = page)
            val data = response.getOrThrow()
            val searchResponse = ResponseParser.parseSearchResponse(data)
                ?: return LoadResult.Error(EmptyResponseException())

            LoadResult.Page(
                data = searchResponse.schoolList,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
