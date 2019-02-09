package com.coldradio.benzene.util.translate;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class Translator {
    private static ITranslator msTranslator = new PapagoTranslator();
    private static ILangDetector msLangDetector = new PapagoLangDetector();

    public static boolean isEnglish(String text) {
        for (int ii = 0; ii < text.length(); ii++) {
            if (text.charAt(ii) > 127) {
                return false;
            }
        }
        return true;
    }

    public static void translateToEnglish(final String text, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        msLangDetector.detectLang(text, new Response.Listener<String>() {
            @Override
            public void onResponse(String langType) {
                msTranslator.translateToEnglish(text, langType, listener, errorListener);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResponse(error);
            }
        });
    }
}
