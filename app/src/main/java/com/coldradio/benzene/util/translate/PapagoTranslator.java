package com.coldradio.benzene.util.translate;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.coldradio.benzene.util.AppEnv;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PapagoTranslator implements ITranslator {
    class PapagoTranslateResponse_JSON {
        class PapagoMessage_JSON {
            class Result_JSON {
                String srcLangType;
                String tarLangType;
                String translatedText;
            }
            Result_JSON result;
        }
        PapagoMessage_JSON message;
    }

    class PapagoTranslateRequest extends Request<PapagoTranslateResponse_JSON> {
        private Response.Listener<String> mListener;
        private final String mText;
        private final String mLangType;

        PapagoTranslateRequest(String text, String langType, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(Method.POST, "https://openapi.naver.com/v1/papago/n2mt", errorListener);

            mListener = listener;
            mText = text;
            mLangType = langType;
        }

        @Override
        protected Map<String, String> getParams() {
            Map<String,String> params = new HashMap<>();

            params.put("source", mLangType);
            params.put("target", "en");
            params.put("text", mText);

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
        protected Response<PapagoTranslateResponse_JSON> parseNetworkResponse(NetworkResponse response) {
            try {
                String data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                PapagoTranslateResponse_JSON json = AppEnv.instance().gson().fromJson(data, PapagoTranslateResponse_JSON.class);

                return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JsonSyntaxException e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(PapagoTranslateResponse_JSON response) {
            mListener.onResponse(response.message.result.translatedText);
        }
    }

    @Override
    public void translateToEnglish(String text, String langType, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        PapagoTranslateRequest request = new PapagoTranslateRequest(text, langType, listener, errorListener);
        AppEnv.instance().addToNetworkQueue(request);
    }
}
