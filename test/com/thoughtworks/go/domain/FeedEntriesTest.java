package com.thoughtworks.go.domain;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.File;
import java.util.List;

public class FeedEntriesTest {

    @Test
    public void shouldCreateFeedEntry() throws Exception {
        FeedEntries feedEntries = FeedEntries.create(file("testdata/criteria-feed.xml"));

        assertThat(feedEntries.getNextLink(), is("http://go03.thoughtworks.com:8153/go/api/feeds/stages.xml?before=8"));
        List<FeedEntry> entries = feedEntries.getEntries();
        assertThat(entries.size(), is(2));
        assertFeedEntry(entries.get(0), "pipeline/9/stage-1/1", "2010-07-22T14:44:56+05:30", 410273L, "http://go03.thoughtworks.com:8153/go/api/stages/11.xml");
        assertFeedEntry(entries.get(1), "pipeline/8/stage-2/1", "2010-07-22T14:44:42+05:30", 410256L, "http://go03.thoughtworks.com:8153/go/api/stages/10.xml");
    }

//    <entry>
//        <title><![CDATA[pipeline/9/stage-1/1]]></title>
//        <updated>2010-07-22T14:44:56+05:30</updated>
//        <id>410273</id>
//        <link href="http://go03.thoughtworks.com:8153/go/api/stages/11.xml" rel="alternate"/>

    //    </entry>
    private void assertFeedEntry(FeedEntry feedEntry, String title, String updatedDate, long id, String resourceLink) {
        assertThat(feedEntry.getTitle(), is(title));
        assertThat(feedEntry.getUpdatedDate(), is(updatedDate));
        assertThat(feedEntry.getId(), is(id));
        assertThat(feedEntry.getResourceLink(), is(resourceLink));
    }


    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}
