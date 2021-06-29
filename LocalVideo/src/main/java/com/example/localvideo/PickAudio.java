package com.example.localvideo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
public class PickAudio extends BasePickMedia<SongBean> {
    private final String pickMedia = PickMediaCallBack.PICK_MEDIA_AUDIO;
    private HashMap<String, ArrayList<SongBean>> mGroupMap = new HashMap<>();
    private ArrayList<PickMediaTotal> mPhotoItems = new ArrayList<>();
    private String TAG = getClass().getSimpleName();

    public PickAudio(Context context, PickMediaCallBack callback) {
        super(context, callback);
    }

    @Override
    public void readMedia() {
        readAduio();
    }

    private void readAduio() {
        mGroupMap.clear();
        mPhotoItems.clear();
        Uri picAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = mContext.getContentResolver();
        //只查询jpeg和png的图片
//        Cursor cursor = contentResolver.query(PhotoUri, null,
//                MediaStore.Images.Media.MIME_TYPE + "=? or "
//                        + MediaStore.Images.Media.MIME_TYPE + "=?",
//                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
        //查询音乐文件
//        AudioUtils.getAllSongs(mContext);
        Cursor cursor = contentResolver.query(picAudioUri,
                new String[]{MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media.MIME_TYPE + "=? or "
                        + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[]{"audio/mpeg", "audio/x-ms-wma"}, null);
        if (cursor == null || cursor.getCount() == 0) {
            PickMessage pickMessage = new PickMessage();


            onError(pickMessage);
        } else {
            while (cursor.moveToNext()) {

                Log.i(TAG, "readPhoto: " + System.currentTimeMillis());
                SongBean songBean = new SongBean();
                //获取图片的路径
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                try {
                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();

//                            mediaBean.setOriginalFilePath(path);
//                            mediaBean.setFileName(fileName);

                    // 文件名
                    songBean.setFileName(cursor.getString(1));
                    // 歌曲名
                    songBean.setSongName(cursor.getString(2));
                    // 时长
                    songBean.setMediaLength(cursor.getString(3));
                    // 歌手名
                    songBean.setSinger(cursor.getString(4));
                    // 专辑名
                    songBean.setAlbum(cursor.getString(5));
                    // 年代
                    if (cursor.getString(6) != null) {
                        songBean.setYear(cursor.getString(6));
                    } else {
                        songBean.setYear("未知");
                    }
                    // 歌曲格式
                    if ("audio/mpeg".equals(cursor.getString(7).trim())) {
                        songBean.setType("mp3");
                    } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
                        songBean.setType("wma");
                    }
                    // 文件大小
                    if (cursor.getString(8) != null) {
                        float size = cursor.getInt(8) / 1024f / 1024f;
                        songBean.setSize((size + "").substring(0, 4) + "M");
                    } else {
                        songBean.setSize("未知");
                    }
                    // 文件路径
                    if (cursor.getString(9) != null) {
                        songBean.setOriginalFilePath(cursor.getString(9));
                    }

                    //根据父路径名将图片放入到groupMap中
                    if (!mGroupMap.containsKey(parentName)) {
                        ArrayList<SongBean> chileList = new ArrayList<>();

                        chileList.add(songBean);
                        mGroupMap.put(parentName, chileList);
                    } else {
                        List<SongBean> chileList = mGroupMap.get(parentName);

                        chileList.add(songBean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
    private ArrayList<PickMediaTotal> subGroupOfPhoto(HashMap<String, ArrayList<SongBean>> groupMap) {
        ArrayList<PickMediaTotal> list = new ArrayList<>();
        if (groupMap.size() == 0) {
            return list;
        }
        Iterator<Map.Entry<String, ArrayList<SongBean>>> it = groupMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, ArrayList<SongBean>> entry = it.next();
            PickMediaTotal PhotoTotal = new PickMediaTotal();
            String key = entry.getKey();

            List<SongBean> value = entry.getValue();

            SortDataList sortList = new SortDataList();
            Collections.sort(value, sortList);//按修改时间排序
            PhotoTotal.setFolderName(key);
            PhotoTotal.setPhotoCount(value.size());
            PhotoTotal.setTopPhotoPath(value.get(value.size() - 1).getOriginalFilePath());//获取该组的第一张图片
            list.add(PhotoTotal);
        }
        return list;
    }

    public ArrayList<SongBean> getChildPathList(int position) {
        ArrayList<SongBean> childList = new ArrayList<>();
        if (mPhotoItems.size() == 0)
            return childList;
        PickMediaTotal PhotoTotal = mPhotoItems.get(position);
        childList = mGroupMap.get(PhotoTotal.getFolderName());
        SortDataList sortList = new SortDataList();
        Collections.sort(childList, sortList);
        return childList;
    }
}
