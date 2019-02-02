package com.coldradio.benzene.library.pubchem;

import java.util.List;

public class Description_JSON {
    public class InformationList_JSON {
        List<Information_JSON> Information;
    }
    public class Information_JSON {
        String Description;
        String DescriptionSourceName;
    }
    InformationList_JSON InformationList;
}
