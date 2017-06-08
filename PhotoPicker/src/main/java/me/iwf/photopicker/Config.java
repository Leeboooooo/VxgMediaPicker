package me.iwf.photopicker;

import java.util.ArrayList;

/**
 * Created by mobao.libo on 2017-06-07-0007.
 */

public class Config {
    private boolean showCamera;
    private boolean showGif;
    private boolean previewEnable;
    private int column;
    private int maxCount;
    private ArrayList<String> originalPhotos;
    private boolean isCrop;
    private boolean openCamera;
    private int mediaType;

    public boolean isShowCamera() {
        return showCamera;
    }

    public Config setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
        return this;
    }

    public boolean isShowGif() {
        return showGif;
    }

    public Config setShowGif(boolean showGif) {
        this.showGif = showGif;
        return this;
    }

    public boolean isPreviewEnable() {
        return previewEnable;
    }

    public Config setPreviewEnable(boolean previewEnable) {
        this.previewEnable = previewEnable;
        return this;
    }

    public int getColumn() {
        return column;
    }

    public Config setColumn(int column) {
        this.column = column;
        return this;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public Config setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        return this;
    }

    public ArrayList<String> getOriginalPhotos() {
        return originalPhotos;
    }

    public Config setOriginalPhotos(ArrayList<String> originalPhotos) {
        this.originalPhotos = originalPhotos;
        return this;
    }

    public boolean isCrop() {
        return isCrop;
    }

    public Config setCrop(boolean crop) {
        isCrop = crop;
        return this;
    }

    public boolean isOpenCamera() {
        return openCamera;
    }

    public Config setOpenCamera(boolean openCamera) {
        this.openCamera = openCamera;
        return this;
    }

    public int getMediaType() {
        return mediaType;
    }

    public Config setMediaType(int mediaType) {
        this.mediaType = mediaType;
        return this;
    }
}
