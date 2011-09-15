package com.waikato.kimt;

import android.os.Bundle;
import com.phonegap.*;
import com.strumsoft.websocket.phonegap.WebSocketFactory;

public class KeepingInMusicTouchActivity extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
        
        // Attach WebSocket factory
        appView.addJavascriptInterface(new WebSocketFactory(appView), "WebSocketFactory");
    }
}