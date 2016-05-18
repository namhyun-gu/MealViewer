/*
 * Copyright 2016 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.earlier.yma.data;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.earlier.yma.data.model.MealObject;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MealDataManager {
    private static MealDataManager ourInstance = new MealDataManager();
    private static Context mContext;
    private final String FILENAME_TEMPLATE = "cache_meal_%d.json";

    public static void initialize(@NonNull Context context) {
        if (mContext != null) {
            Log.e("MealDataManager", "MealDataManager is already initalized");
            return;
        }
        mContext = context;
    }

    public static MealDataManager getInstance() {
        if (mContext == null) {
            throw new NullPointerException("MealDataManager is not already initalized");
        }
        return ourInstance;
    }

    private MealDataManager() {
    }

    public void save(@NonNull MealObject object, @IntRange(from = 1, to = 3) int index) {
        Gson gson;
        FileOutputStream outputStream;
        try {
            gson = new Gson();
            String jsonString = gson.toJson(object);

            String fileName = String.format(FILENAME_TEMPLATE, index);
            outputStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public MealObject read(@IntRange(from = 1, to = 3) int index) {
        Gson gson;
        FileInputStream inputStream;
        try {
            gson = new Gson();
            String fileName = String.format(FILENAME_TEMPLATE, index);
            inputStream = mContext.openFileInput(fileName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();

                String line;
                while((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
                inputStream.close();
                return gson.fromJson(builder.toString(), MealObject.class);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
        Called at once on App initialize
     */
    public void clearLegacyFiles() {
        final String FILENAME_TEMPLATE_LEGACY = "cache_%d.json";
        for (int index = 1; index < 4; index++) {
            String fileName = String.format(FILENAME_TEMPLATE_LEGACY, index);
            boolean deleted = mContext.deleteFile(fileName);
            if (deleted) {
                Log.d("MealDataManager", "Deleted " + fileName);
            }
        }
    }
}
