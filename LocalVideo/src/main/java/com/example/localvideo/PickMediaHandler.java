package com.example.localvideo;

import java.util.ArrayList;

/**
 * Created by hupei on 2016/7/14.
 */
public class PickMediaHandler implements PickMediaCallBack {
    public final static int SCAN_OK = 1;
    public final static int SCAN_ERROR = 2;

    private PickMediaCallBack mCallback;

    public PickMediaHandler(PickMediaCallBack callback) {
        this.mCallback = callback;
    }

    @Override
    public void onStart() {
        mCallback.onStart();
    }

    @Override
    public void onSuccess(ArrayList<PickMediaTotal> list, String PICK_MEDIA) {

        mCallback.onSuccess(list, PICK_MEDIA);
    }


    @Override
    public void onError(PickMessage pickMessage) {
        mCallback.onError(pickMessage);
    }
}