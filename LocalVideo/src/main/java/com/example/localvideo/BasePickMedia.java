package com.example.localvideo;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created           :Herve on 2017/3/11.
 *
 * @ Author          :Herve
 * @ e-mail          :lijianyou.herve@gmail.com
 * @ LastEdit        :2017/3/11
 * @ projectName     :LocalMedia
 * @ version
 */
public abstract class BasePickMedia<T extends MediaBean> implements PickMedia<T>, PickMediaCallBack {
    protected BJXExecutor bjxExecutor;
    private PickMediaHandler pickMediaHandler;
    protected Context mContext;

    public BasePickMedia(Context context, PickMediaCallBack callback) {
        mContext = context;
        pickMediaHandler = new PickMediaHandler(callback);
        bjxExecutor = BJXExecutor.getInstance();
    }

    public void start() {
        onStart();
    }

    @Override
    public void onStart() {
        pickMediaHandler.onStart();

        bjxExecutor.runWorker(new Runnable() {
            @Override
            public void run() {
                readMedia();
            }
        });
    }

    @Override
    public void onSuccess(final ArrayList<PickMediaTotal> list, final String PICK_MEDIA) {
        bjxExecutor.runUI(new Runnable() {
            @Override
            public void run() {

                pickMediaHandler.onSuccess(list, PICK_MEDIA);

            }
        });
    }

    @Override
    public void onError(final PickMessage pickMessage) {
        bjxExecutor.runUI(new Runnable() {
            @Override
            public void run() {

                pickMediaHandler.onError(pickMessage);

            }
        });
    }


}
