package com.blackseapps.heskodkontrol.webview;

import android.content.Context;

public interface WebInterface {
    void LoginResponse(Context context, boolean responseStatus, String lisans, String tc, String password);
}


