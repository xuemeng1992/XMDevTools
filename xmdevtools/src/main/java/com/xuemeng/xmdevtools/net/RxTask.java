
package com.xuemeng.xmdevtools.net;

import android.app.Activity;
import android.content.Context;


import com.xuemeng.xmdevtools.utils.Preconditions;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * 封装 RxTask
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class RxTask<Params, Progress, Result> {
    private ObservableEmitter<? super Result> subscriber;
    private Context context;


    public RxTask(Context context) {
        this.context = context;
    }


    public final void execute(Params... params) {
        onPreExecute();
        Observable.create(subscriber -> doInbackgroud(subscriber, params)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseInfo -> {
                    try {
                        if (!Preconditions.isNullOrEmpty(context) && ((Activity) context).isFinishing()) {
                            onCancelled();
                            return;
                        }
                    } catch (Exception exp) {

                    }
                    onPostExecute((Result) responseInfo);
                }, e -> {
                    onCancelled();
                    onPostExecute(null);
                });
    }

    protected abstract Result doInBackground(Params... params);

    private void doInbackgroud(ObservableEmitter<? super Result> subscriber, Params... params) {
        this.subscriber = subscriber;
        try {
            Result result = doInBackground(params);
            subscriber.onNext(result);
            subscriber.onComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onPreExecute() {
    }


    protected void onPostExecute(Result result) {
    }

    protected void onCancelled() {
        cancel(true);
    }

    public void cancel(boolean forceCancel) {
        if (subscriber != null) {
            subscriber.onComplete();
        }
    }

}

