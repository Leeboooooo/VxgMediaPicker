package me.iwf.photopicker.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.iwf.photopicker.R;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.entity.PhotoDirectory;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.MIME_TYPE;
import static android.provider.MediaStore.MediaColumns.SIZE;

/**
 * Created by vxg on 15/6/28.
 */
public class MediaAllDirectoryLoader extends CursorLoader {
  private static final String SELECTION_IMAGE_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";
  private static final String SELECTION_IMAGE_MIME_TYPE_WITHOUT_GIF = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";
  private static final String SELECTION_ID = MediaStore.Images.Media.BUCKET_ID + "=? and (" + SELECTION_IMAGE_MIME_TYPE + " )";
  private static final String SELECTION_ID_WITHOUT_GIF = MediaStore.Images.Media.BUCKET_ID + "=? and (" + SELECTION_IMAGE_MIME_TYPE_WITHOUT_GIF + " )";
  private static final String[] SELECTION_ARGS_IMAGE_MIME_TYPE = {"image/jpeg", "image/png", "image/jpg", "image/gif"};
  private static final String[] SELECTION_ARGS_IMAGE_MIME_TYPE_WITHOUT_GIF = {"image/jpeg", "image/png", "image/jpg"};
  private onMediaLoad mOnMediaLoad;
  private List<PhotoDirectory> mDirectories;
  private PhotoDirectory photoDirectoryAll = new PhotoDirectory();
  private ContentResolver mContentResolver;

  final String[] IMAGE_PROJECTION = {
          Media._ID,
          Media.DATA,
          Media.BUCKET_ID,
          Media.BUCKET_DISPLAY_NAME,
          Media.DATE_ADDED,
          Media.SIZE,
          Media.MIME_TYPE
  };

  interface onMediaLoad{
    void onFinished(List<PhotoDirectory> directories);
  }

  public MediaAllDirectoryLoader(Context context, boolean showGif,onMediaLoad callback) {
    super(context);
    mContentResolver = ((Activity) context).getContentResolver();
    this.mOnMediaLoad = callback;
    this.mDirectories = new ArrayList<>();
    photoDirectoryAll.setName(context.getString(R.string.__picker_all_image));
    photoDirectoryAll.setId("ALL");
    loadPhoto(showGif);
    loadVideos();
  }

  private MediaAllDirectoryLoader(Context context, Uri uri, String[] projection, String selection,
                                  String[] selectionArgs, String sortOrder) {
    super(context, uri, projection, selection, selectionArgs, sortOrder);
  }

  private void loadPhoto(boolean showGif){
    setProjection(IMAGE_PROJECTION);
    setUri(Media.EXTERNAL_CONTENT_URI);
    setSortOrder(Media.DATE_ADDED + " DESC");

    setSelection(
            MIME_TYPE + "=? or " + MIME_TYPE + "=? or "+ MIME_TYPE + "=? " + (showGif ? ("or " + MIME_TYPE + "=?") : ""));
    String[] selectionArgs;
    if (showGif) {
      selectionArgs = new String[] { "image/jpeg", "image/png", "image/jpg","image/gif" };
    } else {
      selectionArgs = new String[] { "image/jpeg", "image/png", "image/jpg" };
    }
    setSelectionArgs(selectionArgs);
    Cursor cursor = null;
    try {
      cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
              IMAGE_PROJECTION,
              MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=? " + (showGif ? ("or " + MIME_TYPE + "=?") : ""),
              selectionArgs,
              Media.DATE_ADDED + " DESC");
      addItem(cursor);
    }finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  private void buildAlbumList(Context context,boolean showGif) {
    Cursor cursor = null;
    try {
      String imageMimeType = showGif ? SELECTION_IMAGE_MIME_TYPE : SELECTION_IMAGE_MIME_TYPE_WITHOUT_GIF;
      String[] args = showGif ? SELECTION_ARGS_IMAGE_MIME_TYPE : SELECTION_ARGS_IMAGE_MIME_TYPE_WITHOUT_GIF;
      String order = Media.DATE_ADDED + " DESC";
      String selectionId = showGif ? SELECTION_ID : SELECTION_ID_WITHOUT_GIF;
      cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, imageMimeType,args, order);
      addItem(cursor);
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  private void addItem(Cursor cursor) {
    if (cursor != null && cursor.moveToFirst()) {
      do {
        String picPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        if (needFilter(picPath)) {
          Log.e("", "path:" + picPath + " has been filter");
        } else {
          int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
          int size = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
          String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
          String modifyDate = cursor.getString(cursor.getColumnIndexOrThrow(Media.DATE_ADDED));
          String bucketId = cursor.getString(cursor.getColumnIndexOrThrow(BUCKET_ID));
          String name = cursor.getString(cursor.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
          String path = cursor.getString(cursor.getColumnIndexOrThrow(DATA));
          if (size < 1) continue;
          PhotoDirectory photoDirectory = new PhotoDirectory();
          photoDirectory.setId(bucketId);
          photoDirectory.setName(name);

          Photo photo = new Photo(id, path);
          photo.mMimeType = mimeType;
          photo.mModifyData = modifyDate;
          if (!mDirectories.contains(photoDirectory)) {
            photoDirectory.setCoverPath(path);
            photoDirectory.addPhoto(photo);
            photoDirectory.setDateAdded(cursor.getLong(cursor.getColumnIndexOrThrow(DATE_ADDED)));
            mDirectories.add(photoDirectory);
          } else {
            mDirectories.get(mDirectories.indexOf(photoDirectory)).addPhoto(id, path);
          }
          photoDirectoryAll.addPhoto(photo);
        }
      } while (!cursor.isLast() && cursor.moveToNext());
    }
  }

  private void loadVideos() {
    final List<PhotoDirectory> videoMedias = new ArrayList<>();
    final Cursor cursor = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            getMediaColumns(), null, null,null
            /*MediaStore.Images.Media.DATE_MODIFIED + " desc" + " LIMIT " + 100 + " , " + 100*/);
    try {
      int count = 0;
      if (cursor != null && cursor.moveToFirst()) {
        count = cursor.getCount();
        do {
          int i = 0;
          String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
          int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
          String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
          String type = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
          String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
          String date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN));
          String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
          String modifyDate = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
          String bucketId = cursor.getString(cursor.getColumnIndexOrThrow(BUCKET_ID));
          String name = cursor.getString(cursor.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
          if (!size.equals("0") && size.compareToIgnoreCase("5120000")<0){
            PhotoDirectory photoDirectory = new PhotoDirectory();
            photoDirectory.setId(bucketId);
            photoDirectory.setName(name);

            Photo photo = new Photo(id,data);
            photo.mMimeType = type;
            photo.mModifyData = modifyDate;
            photo.mDateTaken = date;
            photo.mDuration = duration;
            photo.mTitle = title;
            photo.mSize = size;

            if (!videoMedias.contains(photoDirectory)){
              photoDirectory.setCoverPath(data);
              photoDirectory.addPhoto(photo);
              photoDirectory.setDateAdded(cursor.getLong(cursor.getColumnIndexOrThrow(DATE_ADDED)));
              videoMedias.add(photoDirectory);
            }else {
              videoMedias.get(videoMedias.indexOf(photoDirectory)).addPhoto(id, data);
            }
            photoDirectoryAll.addPhoto(photo);
          }
        } while (!cursor.isLast() && cursor.moveToNext());
        mDirectories.addAll(videoMedias);
        if (photoDirectoryAll.getPhotoPaths().size() > 0) {
          photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
        }
        mDirectories.add(0, photoDirectoryAll);
        sort();
      }else {
        mDirectories.addAll(videoMedias);
        if (photoDirectoryAll.getPhotoPaths().size() > 0) {
          photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
        }
        mDirectories.add(0, photoDirectoryAll);
        sort();
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  private void sort(){
    if (mDirectories == null || mDirectories.size() < 1) return;
    Comparator<Photo> comparator = new Comparator<Photo>() {
      @Override
      public int compare(Photo o1, Photo o2) {
        if (o1.mModifyData == null)return 0;
        if (o2.mModifyData == null)return 0;
        return o2.mModifyData.compareToIgnoreCase(o1.mModifyData);
      }
    };
    for(PhotoDirectory photoDirectory : mDirectories){
      Collections.sort(photoDirectory.getPhotos(),comparator);
    }
    if (mOnMediaLoad != null){
      mOnMediaLoad.onFinished(mDirectories);
    }
  }

  @NonNull
  private String[] getMediaColumns(){
    return new String[]{
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
    };
  }

  public boolean needFilter(String path) {
    return TextUtils.isEmpty(path) || !(new File(path).exists());
  }

}
