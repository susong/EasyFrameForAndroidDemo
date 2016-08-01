package com.dream.demo.entitiy;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/13 下午3:59
 * Description: EasyFrameForAndroidDemo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitleEntity implements Serializable {

    private String[] id;
    private String   title;
    private int      layoutResId;
    private Class    activityClazz;
    private Class    contentViewClazz;
    private ArrayList<TitleEntity> list = new ArrayList<>();

    public TitleEntity(String title, int layoutResId, Class activityClazz) {
        this.title = title;
        this.layoutResId = layoutResId;
        this.activityClazz = activityClazz;
    }

    public TitleEntity(String title, Class contentViewClazz, Class activityClazz) {
        this.title = title;
        this.contentViewClazz = contentViewClazz;
        this.activityClazz = activityClazz;
    }
}
