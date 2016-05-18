package com.earlier.yma.util;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by namhyun on 2016-02-01.
 */
public class RxBus {
    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());
    private static RxBus mInstance;

    public static RxBus getInstance() {
        if (mInstance == null)
            mInstance = new RxBus();
        return mInstance;
    }

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}
