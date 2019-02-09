package com.coldradio.benzene.util.translate;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.Notifier;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PapagoLangDetector implements ILangDetector {
    class PapagoLangDetectorResponse_JSON {
        String langCode;
    }

    class PapagoLangDetectorRequest extends Request<PapagoLangDetectorResponse_JSON> {
        private Response.Listener<String> mListener;
        private final String mText;

        PapagoLangDetectorRequest(String text, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(Method.POST, "https://openapi.naver.com/v1/papago/detectLangs", errorListener);

            mListener = listener;
            mText = text;
        }

        @Override
        protected Map<String, String> getParams() {
            Map<String,String> params = new HashMap<>();

            params.put("query", mText);

            return params;
        }

        @Override
        public Map<String, String> getHeaders() {
            Map<String,String> params = new HashMap<>();

            params.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
            params.put("X-Naver-Client-Id", "7ZS7esNAWAg5jrw9VTpG");
            params.put("X-Naver-Client-Secret", "3Lv5oaeG2l");

            return params;
        }

        @Override
        protected Response<PapagoLangDetectorResponse_JSON> parseNetworkResponse(NetworkResponse response) {
            try {
                String data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                PapagoLangDetectorResponse_JSON json = AppEnv.instance().gson().fromJson(data, PapagoLangDetectorResponse_JSON.class);

                return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JsonSyntaxException e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(PapagoLangDetectorResponse_JSON response) {
            mListener.onResponse(response.langCode);
        }
    }

    @Override
    public void detectLang(String text, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        try {
            PapagoLangDetectorRequest request = new PapagoLangDetectorRequest(text, listener, errorListener);

            AppEnv.instance().addToNetworkQueue(request);
        } catch (Exception e) {
            Notifier.instance().notification(e.toString());
        }
    }
}
