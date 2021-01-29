package com.tizzone.go4lunch.utils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;


public class RxUtils {
    private RxUtils() {
    }

    public static <T> Observable<T> toObservable(@NonNull final ObservableField<T> observableField) {
        return Observable.create(new ObservableOnSubscribe<T>() {

            /**
             * Called for each {@link Observer} that subscribes.
             *
             * @param emitter the safe emitter instance, never {@code null}
             * @throws Throwable on error
             */
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<T> emitter) throws Throwable {

            }
        });
    }
}
