package com.dream.demo.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import com.dream.demo.BuildConfig;


/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/4 下午7:52
 * Description: ApplianceServer
 */
public class Utils {
    /**
     * 获取VersionCode
     * http://stackoverflow.com/questions/4616095/how-to-get-the-build-version-number-of-your-android-application
     * If you're using the Gradle plugin/Android Studio,
     * version code and version name are available statically in BuildConfig:
     * <p>
     * int versionCode = BuildConfig.VERSION_CODE;
     * String versionName = BuildConfig.VERSION_NAME;
     * No Context object needed!
     * <p>
     * Just make sure to specify them in your build.gradle file instead of the AndroidManifest.xml.
     * <p>
     * defaultConfig {
     * ...
     * versionCode 1
     * versionName "1.0"
     * }
     * 注意：如果在Module中使用此方法，会获取Module中设置的对应的值。
     *
     * @return VersionCode
     */
    public static int getAppVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    /**
     * 获取VersionName
     * http://stackoverflow.com/questions/4616095/how-to-get-the-build-version-number-of-your-android-application
     * If you're using the Gradle plugin/Android Studio,
     * version code and version name are available statically in BuildConfig:
     * <p>
     * int versionCode = BuildConfig.VERSION_CODE;
     * String versionName = BuildConfig.VERSION_NAME;
     * No Context object needed!
     * <p>
     * Just make sure to specify them in your build.gradle file instead of the AndroidManifest.xml.
     * <p>
     * defaultConfig {
     * ...
     * versionCode 1
     * versionName "1.0"
     * }
     * 注意：如果在Module中使用此方法，会获取Module中设置的对应的值。
     *
     * @return VersionName
     */
    public static String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * 获取VersionCode
     *
     * @return VersionCode
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取VersionName
     *
     * @return VersionName
     */
    public static String getAppVersionName(Context context) {
        String versionName = "1.0";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
