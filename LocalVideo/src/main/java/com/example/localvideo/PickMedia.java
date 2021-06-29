package com.example.localvideo;

import java.util.ArrayList;

/**
 * Created by Herve on 2016/7/20.
 */
public interface PickMedia<T extends MediaBean> {
    ArrayList<T> getChildPathList(int position);

    void readMedia();
}
