package com.dream.library.interf;

import android.app.Dialog;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        15/10/27 下午3:45
 * Description: EasyFrame
 */
public interface IProgressDialogControl {

    Dialog showProgressDialog();

    Dialog showNonCancelableProgressDialog();

    Dialog showProgressDialog(int resId);

    Dialog showNonCancelableProgressDialog(int resId);

    Dialog showProgressDialog(String text);

    Dialog showNonCancelableProgressDialog(String text);

    Dialog showProgressDialog(String text, boolean isCancelable);

    void hideProgressDialog();
}
