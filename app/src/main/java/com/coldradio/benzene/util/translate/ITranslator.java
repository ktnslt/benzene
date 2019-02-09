package com.coldradio.benzene.util.translate;

import com.android.volley.Response;

public interface ITranslator {
    void translateToEnglish(String text, String langType, Response.Listener<String> listener, Response.ErrorListener errorListener);
}
