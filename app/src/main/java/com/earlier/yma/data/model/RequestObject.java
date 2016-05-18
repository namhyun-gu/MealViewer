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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class RequestObject implements Parcelable {
    public final String path;
    public final String schulCode;
    public final String schulCrseScCode;
    public final String schulKndScCode;

    private RequestObject(Builder builder) {
        this.path = builder.path;
        this.schulCode = builder.schulCode;
        this.schulCrseScCode = builder.schulCrseScCode;
        this.schulKndScCode = builder.schulKndScCode;
    }

    protected RequestObject(Parcel in) {
        path = in.readString();
        schulCode = in.readString();
        schulCrseScCode = in.readString();
        schulKndScCode = in.readString();
    }

    public static final Creator<RequestObject> CREATOR = new Creator<RequestObject>() {
        @Override
        public RequestObject createFromParcel(Parcel in) {
            return new RequestObject(in);
        }

        @Override
        public RequestObject[] newArray(int size) {
            return new RequestObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(schulCode);
        dest.writeString(schulCrseScCode);
        dest.writeString(schulKndScCode);
    }

    public static class Builder {
        private String path;
        private String schulCode;
        private String schulCrseScCode;
        private String schulKndScCode;

        public Builder path(@NonNull String path) {
            this.path = path;
            return this;
        }

        public Builder schulCode(@NonNull String schulCode) {
            this.schulCode = schulCode;
            return this;
        }

        public Builder schulCrseScCode(@NonNull String schulCrseScCode) {
            this.schulCrseScCode = schulCrseScCode;
            return this;
        }

        public Builder schulKndScCode(@NonNull String schulKndScCode) {
            this.schulKndScCode = schulKndScCode;
            return this;
        }

        public RequestObject build() {
            return new RequestObject(this);
        }
    }
}
