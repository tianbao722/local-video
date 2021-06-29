package com.example.localvideo;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by hupei on 2016/7/14.
 */
public class PickMediaHelper {

    private BasePickMedia<PhotoBean> mPickPhoto;
    private BasePickMedia<VideoBean> mPickVideo;
    private BasePickMedia<SongBean> mPickAudio;
    private Context mContext;

    public PickMediaHelper(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 开始读取视频
     *
     * @param callback
     * @return
     */
    public void readVideoListener(PickMediaCallBack callback) {
        if (mPickVideo == null) {
            mPickVideo = new PickVideo(mContext.getApplicationContext(), callback);
        }
    }

    /**
     * 开始读取相册
     *
     * @param callback
     * @return
     */
    public void readPhotoListener(PickMediaCallBack callback) {
        if (mPickPhoto == null) {
            mPickPhoto = new PickPhoto(mContext.getApplicationContext(), callback);
        }

    }

    /**
     * 开始读取相册
     *
     * @param callback
     * @return
     */
    public void readAudioListener(PickMediaCallBack callback) {
        if (mPickAudio == null) {
            mPickAudio = new PickAudio(mContext.getApplicationContext(), callback);
        }
    }

    public void startReadAudio() {
        if (mPickAudio == null) {
            throw new RuntimeException("you must set readAudioListener first");
        }
        mPickAudio.start();

    }

    public void startReadPhoto() {
        if (mPickPhoto == null) {
            throw new RuntimeException("you must set readPhotoListener first");
        }
        mPickPhoto.start();

    }

    public void startReadVideo() {
        if (mPickVideo == null) {
            throw new RuntimeException("you must set readVideoListener first");
        }
        mPickVideo.start();
    }

    public ArrayList<SongBean> getAudioChildPathList(int position) {
        return mPickAudio.getChildPathList(position);
    }

    public ArrayList<VideoBean> getVideoChildPathList(int position) {
        return mPickVideo.getChildPathList(position);
    }

    public ArrayList<PhotoBean> getPhotoChildPathList(int position) {
        return mPickPhoto.getChildPathList(position);
    }


}
