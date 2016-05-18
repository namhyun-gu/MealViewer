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

package com.earlier.yma.ui.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.earlier.yma.R;
import com.earlier.yma.data.model.preference.Time;
import com.google.gson.Gson;

import java.util.Calendar;

public class TimePickerPreference extends Preference implements MaterialDialog.SingleButtonCallback {
    private String TAG = getClass().getSimpleName();
    private Dialog mDialog;
    private int mDefaultHour;
    private int mDefaultMinute;
    private boolean mIs24HourView;
    private String mSummaryFormat;
    private String mSummaryTimeFormat;
    private OnTimeSetListener mListener;

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        final Calendar c = Calendar.getInstance();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimePickerPreference);
        mDefaultHour = a.getInteger(R.styleable.TimePickerPreference_defaultHour,
                c.get(Calendar.HOUR_OF_DAY));
        mDefaultMinute = a.getInteger(R.styleable.TimePickerPreference_defaultMinute,
                c.get(Calendar.MINUTE));
        mIs24HourView = a.getBoolean(R.styleable.TimePickerPreference_is24HourView,
                DateFormat.is24HourFormat(context));
        mSummaryFormat = a.getString(R.styleable.TimePickerPreference_summaryFormat);
        mSummaryTimeFormat = a.getString(R.styleable.TimePickerPreference_summaryTimeFormat);
        a.recycle();

        syncValue();
        prepareDialog();
    }

    public TimePickerPreference(Context context) {
        this(context, null);
    }

    public void setDefaultHour(@IntRange(from = 0, to = 23) int hour) {
        mDefaultHour = hour;
    }

    public void setDefaultMinute(@IntRange(from = 0, to = 59) int minute) {
        mDefaultMinute = minute;
    }

    public int getDefaultHour() {
        return mDefaultHour;
    }

    public int getDefaultMinute() {
        return mDefaultMinute;
    }

    public void setOnTimeSetListener(OnTimeSetListener listener) {
        mListener = listener;
    }

    @Override
    protected void onClick() {
        super.onClick();
        mDialog.show();
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        boolean isValidation = true;
        TimePicker timePicker = (TimePicker) dialog.getCustomView();
        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        if (mListener != null) {
            isValidation = mListener.onTimeSet(timePicker, hour, minute);
        }
        if (isValidation) {
            if (hasKey()) {
                setValue(new Time(hour, minute));
                setSummaryByFormat(hour, minute);
                mDefaultHour = hour;
                mDefaultMinute = minute;
            }
            dialog.dismiss();
        }
    }

    private void setValue(Time time) {
        final boolean changed = !new Time(mDefaultHour, mDefaultMinute).equals(time);
        final boolean isNotExist = !PreferenceManager.getDefaultSharedPreferences(getContext()).contains(getKey());
        if (changed || isNotExist) {
            String value = new Gson().toJson(time);
            SharedPreferences.Editor editor =
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putString(getKey(), value);
            editor.apply();
        }
    }

    private void syncValue() {
        if (hasKey()) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            Time time = new Gson().fromJson(preferences.getString(getKey(), null), Time.class);
            if (time != null) {
                mDefaultHour = time.getHour();
                mDefaultMinute = time.getMinute();
            }
        }
        setValue(new Time(mDefaultHour, mDefaultMinute));
        setSummaryByFormat(mDefaultHour, mDefaultMinute);
    }

    private void setSummaryByFormat(int hour, int minute) {
        if (mSummaryFormat != null) {
            if (mSummaryTimeFormat == null)
                mSummaryTimeFormat = "hh:mm a";
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            CharSequence formattedDate = DateFormat.format(mSummaryTimeFormat, c);
            setSummary(mSummaryFormat.replace("{date}", formattedDate));
        }
    }

    private void prepareDialog() {
        final boolean wrapInScrollView = false;
        mDialog = new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_time_picker, wrapInScrollView)
                .autoDismiss(false)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(this)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build();

        TimePicker timePicker = (TimePicker) ((MaterialDialog) mDialog).getCustomView();
        if (timePicker != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(mDefaultHour);
                timePicker.setMinute(mDefaultMinute);
            } else {
                timePicker.setCurrentHour(mDefaultHour);
                timePicker.setCurrentMinute(mDefaultMinute);
            }
            timePicker.setIs24HourView(mIs24HourView);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        myState.hour = mDefaultHour;
        myState.minute = mDefaultMinute;
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setValue(new Time(myState.hour, myState.minute));
    }

    static class SavedState extends BaseSavedState {
        int hour, minute;

        public SavedState(Parcel source) {
            super(source);
            hour = source.readInt();
            minute = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(hour);
            dest.writeInt(minute);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new ClassLoaderCreator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                        return null;
                    }

                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    /**
     * The callback interface used to indicate the user is done filling in the time
     * (e.g. they clicked on the 'OK' button)
     */
    public interface OnTimeSetListener {
        /**
         * Called when the user is clicked positive button
         *
         * @param view      timepicker view
         * @param hourOfDay returned hourofday
         * @param minute    returned minute
         * @return if true dialog dismiss
         */
        boolean onTimeSet(TimePicker view, int hourOfDay, int minute);
    }
}
