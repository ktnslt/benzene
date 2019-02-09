package com.coldradio.benzene.util.translate;

import com.android.volley.Response;

public interface ILangDetector {
    void detectLang(String text, Response.Listener<String> listener, Response.ErrorListener errorListener);
}
