package com.coldradio.benzene.library.pubchem;

import java.util.List;

class AutoComplete_JSON {
    public class dictionary_terms_JSON {
        List<String> compound;
    }
    public class status_JSON {
        int code;
    }
    status_JSON status;
    int total;
    dictionary_terms_JSON dictionary_terms;
}
