package com.thoughtworks.go.legacy;

import com.thoughtworks.go.AbstractTalkToGo;
import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.http.HttpClientWrapper;

/**
 * @understands Talking to a Go 2.0 server using its APIs
 */
@SuppressWarnings({"unchecked"})
public class TalkToGo2DotOh extends AbstractTalkToGo {

    public TalkToGo2DotOh(String pipeline, HttpClientWrapper httpClient, boolean infiniteCrawler) {
        super(pipeline, httpClient, infiniteCrawler);
    }

    @Override
    protected String feedUrl() {
        return "/api/feeds/stages.xml";
    }
}
