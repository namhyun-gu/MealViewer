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

package com.earlier.yma.data.model;

import java.util.List;

public class MealObject {
    private List<Meal> data;

    public MealObject(List<Meal> data) {
        this.data = data;
    }

    public List<Meal> getData() {
        return data;
    }

    public static class Meal {
        private List<String> meal;
        private double kcal;
        private double carbohydrate;
        private double protein;
        private double fat;

        public Meal(List<String> meal, double kcal, double carbohydrate, double protein, double fat) {
            this.meal = meal;
            this.kcal = kcal;
            this.carbohydrate = carbohydrate;
            this.protein = protein;
            this.fat = fat;
        }

        public List<String> getMeal() {
            return meal;
        }

        public double getKcal() {
            return kcal;
        }

        public double getCarbohydrate() {
            return carbohydrate;
        }

        public double getProtein() {
            return protein;
        }

        public double getFat() {
            return fat;
        }
    }
}
