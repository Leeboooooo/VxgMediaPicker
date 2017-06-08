package me.iwf.photopicker.entity;

import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by donglua on 15/6/30.
 */
public class Photo {
  private static final long MB = 1024 * 1024;
  private int id;
  private String path;

  public String mSize;
  public String mTitle;
  public String mDuration;
  public String mDateTaken;
  public String mModifyData;
  public boolean mIsSelected;
  public String mThumbnailPath;
  public String mCompressPath;
  public int mHeight;
  public int mWidth;
  public MEDIA_TYPE mImageType;
  public String mMimeType;

  public enum MEDIA_TYPE {
    PNG, JPG, GIF,VIDEO
  }

  public Photo(int id, String path) {
    this.id = id;
    this.path = path;
  }

  public Photo() {
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Photo)) return false;

    Photo photo = (Photo) o;

    return id == photo.id;
  }

  @Override public int hashCode() {
    return id;
  }

  public String getSizeByUnit() {
    double size = getSize();
    if (size == 0) {
      return "0K";
    }
    if (size >= MB) {
      double sizeInM = size / MB;
      return String.format(Locale.getDefault(), "%.1f", sizeInM) + "M";
    }
    double sizeInK = size / 1024;
    return String.format(Locale.getDefault(), "%.1f", sizeInK) + "K";
  }

  public String formatTimeWithMin(long duration) {
    if (duration <= 0) {
      return String.format(Locale.US, "%02d:%02d", 0, 0);
    }
    long totalSeconds = duration / 1000;

    long seconds = totalSeconds % 60;
    long minutes = (totalSeconds / 60) % 60;
    long hours = totalSeconds / 3600;

    if (hours > 0) {
      return String.format(Locale.US, "%02d:%02d", hours * 60 + minutes,
              seconds);
    } else {
      return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }
  }

  public MEDIA_TYPE getImageTypeByMime(String mimeType) {
    if (!TextUtils.isEmpty(mimeType)) {
      if ("image/gif".equals(mimeType)) {
        return MEDIA_TYPE.GIF;
      } else if ("image/png".equals(mimeType)) {
        return MEDIA_TYPE.PNG;
      } else if ("image/jpg".equals(mimeType)){
        return MEDIA_TYPE.JPG;
      } else {
        return MEDIA_TYPE.VIDEO;
      }
    }
    return MEDIA_TYPE.PNG;
  }

  public MEDIA_TYPE getMediaType() {
    if (!TextUtils.isEmpty(mMimeType)) {
      if ("image/gif".equals(mMimeType)) {
        return MEDIA_TYPE.GIF;
      } else if ("image/png".equals(mMimeType)) {
        return MEDIA_TYPE.PNG;
      } else if ("image/jpeg".equals(mMimeType)){
        return MEDIA_TYPE.JPG;
      } else if ("video/mp4".equals(mMimeType)){
        return MEDIA_TYPE.VIDEO;
      }
    }
    return MEDIA_TYPE.PNG;
  }

  public String getDuration() {
    try {
      long duration = Long.parseLong(mDuration);
      return formatTimeWithMin(duration);
    } catch (NumberFormatException e) {
      return "0:00";
    }
  }

  public long getSize() {
    try {
      long result = Long.valueOf(mSize);
      return result > 0 ? result : 0;
    }catch (NumberFormatException size) {
      return 0;
    }
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
