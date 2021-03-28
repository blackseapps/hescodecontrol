package com.blackseapps.heskodkontrol.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

public class KeyboardEvents {

    private static boolean keyBoardVisible = false;

    public static void hide(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isVisible(final View view) {


        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect rec = new Rect();
                view.getWindowVisibleDisplayFrame(rec);

                //finding screen height
                int screenHeight = view.getRootView().getHeight();

                //finding keyboard height
                int keypadHeight = screenHeight - rec.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    keyBoardVisible = true;
                } else {
                    keyBoardVisible = false;
                }
            }
        });

        return keyBoardVisible;
    }



}
