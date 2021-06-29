package com.example.localvideo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.video.PhotoBean;

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
public class PickPhoto extends BasePickMedia<PhotoBean> {
    private final String pickMedia = PickMediaCallBack.PICK_MEDIA_PHOTO;
    private HashMap<String, ArrayList<PhotoBean>> mGroupMap = new HashMap<>();
    private ArrayList<PickMediaTotal> mPhotoItems = new ArrayList<>();
    private String TAG = getClass().getSimpleName();

    public PickPhoto(Context context, PickMediaCallBack callback) {
        super(context, callback);
    }

    @Override
    public void readMedia() {
        readPhoto();
    }

    private void readPhoto() {
        mGroupMap.clear();
        mPhotoItems.clear();
        Uri PhotoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = mContext.getContentResolver();
        //只查询jpeg和png的图片
        Cursor cursor = contentResolver.query(PhotoUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
        if (cursor == null || cursor.getCount() == 0) {

            PickMessage pickMessage = new PickMessage();


            onError(pickMessage);

        } else {
            while (cursor.moveToNext()) {
                PhotoBean PhotoBean = new PhotoBean();
                //获取图片的路径
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                try {
                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();
                    PhotoBean.setOriginalFilePath(path);
                    PhotoBean.setFileName(fileName);

                    //根据父路径名将图片放入到groupMap中
                    if (!mGroupMap.containsKey(parentName)) {
                        ArrayList<com.example.video.PhotoBean> chileList = new ArrayList<>();

                        chileList.add(PhotoBean);
                        mGroupMap.put(parentName, chileList);
                    } else {
                        ArrayList<com.example.video.PhotoBean> chileList = mGroupMap.get(parentName);

                        chileList.add(PhotoBean);
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
    private ArrayList<PickMediaTotal> subGroupOfPhoto(HashMap<String, ArrayList<PhotoBean>> groupMap) {
        ArrayList<PickMediaTotal> ArrayList = new ArrayList<>();
        if (groupMap.size() == 0) {
            return ArrayList;
        }
        Iterator<Map.Entry<String, ArrayList<PhotoBean>>> it = groupMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, ArrayList<PhotoBean>> entry = it.next();
            PickMediaTotal PhotoTotal = new PickMediaTotal();
            String key = entry.getKey();

            ArrayList<PhotoBean> value = entry.getValue();

            SortDataList<PhotoBean> sortList = new SortDataList<>();
            Collections.sort(value, sortList);//按修改时间排序
            PhotoTotal.setFolderName(key);
            PhotoTotal.setPhotoCount(value.size());
            PhotoTotal.setTopPhotoPath(value.get(value.size() - 1).getOriginalFilePath());//获取该组的第一张图片
            ArrayList.add(PhotoTotal);
        }
        return ArrayList;
    }

    public ArrayList<PhotoBean> getChildPathList(int position) {
        ArrayList<PhotoBean> childList = new ArrayList<>();
        if (mPhotoItems.size() == 0)
            return childList;
        PickMediaTotal PhotoTotal = mPhotoItems.get(position);
        childList = mGroupMap.get(PhotoTotal.getFolderName());
        SortDataList<PhotoBean> sortList = new SortDataList<>();
        Collections.sort(childList, sortList);
        return childList;
    }


}
