package me.iwf.photopicker.event;

import me.iwf.photopicker.entity.Photo;

/**
 * Created by mobao.libo on 2017-06-08-0008.
 */

public interface OnItemVideoClickListener {
    /**
     * 选中视频
     * @param position
     * @param path
     * @return
     */
    boolean onClick(int position, Photo path);
}
