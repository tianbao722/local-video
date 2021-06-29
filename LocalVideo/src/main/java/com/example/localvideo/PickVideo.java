package com.example.localvideo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.video.VideoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created           :Herve on 2016/8/29.
 *
 * @ Author          :Herve
 * @ e-mail          :lijianyou.herve@gmail.com
 * @ LastEdit        :2016/8/29
 * @ projectName     :LocalMedia
 * @ version
 */
public class PickVideo extends BasePickMedia<VideoBean> {
    private final String pickMedia = PickMediaCallBack.PICK_MEDIA_VIDEO;
    private HashMap<String, ArrayList<VideoBean>> mGroupMap = new HashMap<>();
    private ArrayList<PickMediaTotal> mPhotoItems = new ArrayList<>();
    private String TAG = getClass().getSimpleName();

    public PickVideo(Context context, PickMediaCallBack callback) {
        super(context, callback);
    }


    @Override
    public void readMedia() {
        readVideo();
    }

    private void readVideo() {
        mGroupMap.clear();
        mPhotoItems.clear();
        Uri PhotoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = mContext.getContentResolver();

        String[] projection1 = new String[]{MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATE_TAKEN, MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Video.Media._ID};

        //只查询mp4的视频
        Cursor cursor = contentResolver.query(PhotoUri, projection1,
                null,
                null,
                null);

        if (cursor == null || cursor.getCount() == 0) {
            PickMessage pickMessage = new PickMessage();
            onError(pickMessage);
        } else {

            while (cursor.moveToNext()) {

                String originalFilePath = cursor.getString(0);

                File file = new File(originalFilePath);

                if (file.getName().endsWith(".mp4") && file.exists() && file.canRead()) {
                    String fileName = cursor.getString(1);

                    String mediaLength = cursor.getString(2);

                    String mediaTakeTime = cursor.getString(3);
                    String mediaEditTime = cursor.getString(4);

                    String origId = cursor.getString(5);

                    VideoBean VideoBean = new VideoBean();

                    VideoBean.setOriginalFilePath(originalFilePath);
                    VideoBean.setFileName(fileName);
                    VideoBean.setMediaTakeTime(mediaTakeTime);
                    VideoBean.setMediaEditTime(mediaEditTime);
                    VideoBean.setMediaLength(mediaLength);
                    VideoBean.setOrigId(origId);

                    try {
                        //获取该图片的父路径名
                        String parentName = new File(originalFilePath).getParentFile().getName();
                        //根据父路径名将图片放入到groupMap中
                        if (!mGroupMap.containsKey(parentName)) {

                            ArrayList<com.example.video.VideoBean> newchileList = new ArrayList<>();
                            newchileList.add(VideoBean);
                            mGroupMap.put(parentName, newchileList);
                        } else {

                            ArrayList<com.example.video.VideoBean> chileList = mGroupMap.get(parentName);

                            chileList.add(VideoBean);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            cursor.close();
            //通知Handler扫描图片完成
            mPhotoItems = subGroupOfPhoto(mGroupMap);
            onSuccess(mPhotoItems,pickMedia);

        }
    }

    /**
     * 组装分组数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     *
     * @param groupMap
     * @return
     */
    private ArrayList<PickMediaTotal> subGroupOfPhoto(HashMap<String, ArrayList<VideoBean>> groupMap) {
        ArrayList<PickMediaTotal> ArrayList = new ArrayList<>();
        if (groupMap.size() == 0) {
            return ArrayList;
        }

        Iterator<Map.Entry<String, ArrayList<VideoBean>>> it = groupMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<VideoBean>> entry = it.next();
            PickMediaTotal PhotoTotal = new PickMediaTotal();
            String key = entry.getKey();
            ArrayList<VideoBean> value = entry.getValue();
            SortDataList sortList = new SortDataList();
            Collections.sort(value, sortList);//按修改时间排序

            PhotoTotal.setFolderName(key);
            PhotoTotal.setPhotoCount(value.size());
            PhotoTotal.setTopPhotoPath(value.get(0).getOriginalFilePath());//获取该组的第一张图片

            ArrayList.add(PhotoTotal);
        }
        return ArrayList;
    }

    public ArrayList<VideoBean> getChildPathList(int position) {
        if (position > mPhotoItems.size() - 1) {
            return null;
        }
        ArrayList<VideoBean> childList = new ArrayList<>();
        if (mPhotoItems.size() == 0)
            return childList;
        PickMediaTotal PhotoTotal = mPhotoItems.get(position);
        childList = mGroupMap.get(PhotoTotal.getFolderName());
        SortDataList sortList = new SortDataList();
        Collections.sort(childList, sortList);
        return childList;
    }
}
