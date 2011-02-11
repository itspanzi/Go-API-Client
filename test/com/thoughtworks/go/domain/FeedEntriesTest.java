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
        FeedEntries feedEntries = FeedEntries.create(file("testdata/2.1/criteria-feed.xml"));

        assertThat(feedEntries.getNextLink(), is("http://blrstdcrspair03.thoughtworks.com:8153/go/api/pipelines/5/stages.xml?before=37"));
        List<FeedEntry> entries = feedEntries.getEntries();
        assertThat(entries.size(), is(2));
        assertFeedEntry(entries.get(0), "5/2/AutoStage1/1", "2011-01-28T14:23:37+05:30", "http://blrstdcrspair03.thoughtworks.com:8153/go/pipelines/5/2/AutoStage1/1", "http://blrstdcrspair03.thoughtworks.com:8153/go/api/stages/9.xml");
        assertFeedEntry(entries.get(1), "5/1/AutoStage4/1", "2011-01-28T14:23:15+05:30", "http://blrstdcrspair03.thoughtworks.com:8153/go/pipelines/5/1/AutoStage4/1", "http://blrstdcrspair03.thoughtworks.com:8153/go/api/stages/8.xml");
    }

//    <entry>
//            <title><![CDATA[5/2/AutoStage1/1]]></title>
//            <updated>2011-01-28T14:23:37+05:30</updated>
//            <id>http://blrstdcrspair03.thoughtworks.com:8153/go/pipelines/5/2/AutoStage1/1</id>
//            <link href="http://blrstdcrspair03.thoughtworks.com:8153/go/api/stages/36.xml" rel="alternate"/>
//            <link href="http://blrstdcrspair03.thoughtworks.com:8153/go/pipelines/5/2/AutoStage1/1" rel="alternate" type="text/html"/>
//            <link href="http://blrstdcrspair03.thoughtworks.com:8153/go/api/pipelines/5/33.xml" rel="http://studios.thoughtworks.com/ns/relations/go/pipeline" type="text/xml"/>
//            <category scheme="http://studios.thoughtworks.com/ns/categories/go" term="stage" label="Stage" />
//            <category scheme="http://studios.thoughtworks.com/ns/categories/go" term="completed" label="Completed" />
//            <category scheme="http://studios.thoughtworks.com/ns/categories/go" term="failed" label="Failed" />
//    </entry>
    private void assertFeedEntry(FeedEntry feedEntry, String title, String updatedDate, String id, String resourceLink) {
        assertThat(feedEntry.getTitle(), is(title));
        assertThat(feedEntry.getUpdatedDate(), is(updatedDate));
        assertThat(feedEntry.getId(), is(id));
        assertThat(feedEntry.getResourceLink(), is(resourceLink));
    }


    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}
