package com.dream.demo.data;

import com.dream.demo.entitiy.TitleEntity;

import java.lang.reflect.Field;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/21 上午12:23
 * Description: EasyFrameForAndroidDemo
 */
public class TitleParser {

    public static TitleEntity title;

    static {
        try {
            Class<?> clazz          = Class.forName(Title.class.getName());
            Field[]  declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                String   name = field.getName();
                String[] id   = name.split("_");
                if (id[0].equalsIgnoreCase("title")) {
                    TitleEntity entity = (TitleEntity) field.get(clazz);
                    entity.setId(id);
                    switch (id[1]) {
                        case "a":
                            title = entity;
                            break;
                        case "b":
                            title.getList().add(entity);
                            break;
                        case "c":
                            for (TitleEntity a : title.getList()) {
                                if (a.getId()[3].equalsIgnoreCase(entity.getId()[3])) {
                                    a.getList().add(entity);
                                }
                            }
                            break;
                        case "d":
                            for (TitleEntity a : title.getList()) {
                                if (a.getId()[3].equalsIgnoreCase(entity.getId()[3])) {
                                    for (TitleEntity b : a.getList()) {
                                        if (b.getId()[4].equalsIgnoreCase(entity.getId()[4])) {
                                            b.getList().add(entity);
                                        }
                                    }
                                }
                            }
                            break;
                        case "e":
                            for (TitleEntity a : title.getList()) {
                                if (a.getId()[3].equalsIgnoreCase(entity.getId()[3])) {
                                    for (TitleEntity b : a.getList()) {
                                        if (b.getId()[4].equalsIgnoreCase(entity.getId()[4])) {
                                            for (TitleEntity c : b.getList()) {
                                                if (c.getId()[5].equalsIgnoreCase(entity.getId()[5])) {
                                                    c.getList().add(entity);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
