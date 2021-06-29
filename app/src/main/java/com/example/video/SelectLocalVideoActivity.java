package com.example.video;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.localvideo.MediaBean;
import com.example.localvideo.OnAdapterItemClickListener;
import com.example.localvideo.PickMediaCallBack;
import com.example.localvideo.PickMediaHelper;
import com.example.localvideo.PickMediaTotal;
import com.example.localvideo.PickMessage;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import java.util.ArrayList;


public class SelectLocalVideoActivity extends Activity {
    private MediaItemAdapter mediaItemAdapter;
    private PickMediaHelper pickMediaHelper;
    private ArrayList<MediaBean> mediaBeanArrayList = new ArrayList<MediaBean>();
    private Context mContext = this;

    private RecyclerView mRvItems;
    private ProgressBar mPro;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_local_video);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mediaItemAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {
            @Override
            public void OnAdapterItemClickListener(RecyclerView.ViewHolder holder, View view, int position) {
                //视频路径mediaBeanArrayList.get(position).getOriginalFilePath()
            }
        });
    }

    private void initView() {
        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                Toast.makeText(mContext, "权限请求成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                Toast.makeText(mContext, "权限请求失败", Toast.LENGTH_SHORT).show();
            }
        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
        mRvItems = findViewById(R.id.rv_items);
        mPro = findViewById(R.id.progress_bar);

        mediaItemAdapter = new MediaItemAdapter(mContext);
        RecyclerView.LayoutManager mediaLayoutManager = new GridLayoutManager(mContext, 4, RecyclerView.VERTICAL, false);
        mRvItems.setLayoutManager(mediaLayoutManager);
        mRvItems.setAdapter(mediaItemAdapter);
        mPro.setVisibility(View.GONE);
    }

    private void initData() {
        pickMediaHelper = new PickMediaHelper(mContext);
        refRefreshPhoto();
    }

    private void refRefreshPhoto() {
        pickMediaHelper.readVideoListener(new PickMediaCallBack() {
            @Override
            public void onStart() {
                Log.e("readVideoListener", " onStart() 开始获取媒体库");
                mPro.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(ArrayList<PickMediaTotal> list, String PICK_MEDIA) {
                Log.e("readVideoListener", " onSuccess() 开始获取视频媒体库");
                Log.e("readVideoListener", " onSuccess() 开始获取视频媒体库");
                mediaBeanArrayList.clear();
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        mediaBeanArrayList.addAll(pickMediaHelper.getVideoChildPathList(i));
                    }
                    mediaItemAdapter.setData(mediaBeanArrayList);
                }
                mPro.setVisibility(View.GONE);
                //读取成功，返回 list，直接丢入到 ListView 适配器中
            }

            @Override
            public void onError(PickMessage pickMessage) {
                Log.e("readVideoListener", " onError() 获取媒体库失败");
                mPro.setVisibility(View.GONE);
            }
        });
        pickMediaHelper.startReadVideo();
    }
}