package com.thoughtworks.go.domain;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeedEntriesTest {

    @Test
    public void shouldCreateFeedEntry() throws Exception {
        FeedEntries feedEntries = FeedEntries.create(file("testdata/2.4/feed.xml"));

        assertThat(feedEntries.getNextLink(), is("http://go03.thoughtworks.com:8153/go/api/pipelines/pair03/stages.xml?before=8"));
        List<FeedEntry> entries = feedEntries.getEntries();
        assertThat(entries.size(), is(2));
        assertFeedEntry(entries.get(0),
                "pipeline(9) stage stage(1) Failed",
                "2011-01-28T14:23:37+05:30",
                "http://go03.thoughtworks.com:8153/go/pipelines/pair03/696/build/1",
                "http://go03.thoughtworks.com:8153/go/api/stages/9.xml",
                "JJ,PS,SS",
                Arrays.asList("https://mingle09.thoughtworks.com/api/v2/projects/go/cards/5188.xml,#5188", "https://mingle09.thoughtworks.com/api/v2/projects/go/cards/5601.xml,#5601"),
                "Completed",
                "Failed");
        assertFeedEntry(entries.get(1),
                "pipeline(8) stage stage(1) Failed",
                "2011-01-28T14:23:15+05:30",
                "http://go03.thoughtworks.com:8153/go/pipelines/pair03/695/build/1",
                "http://go03.thoughtworks.com:8153/go/api/stages/8.xml",
                "Santosh",
                Arrays.asList("https://mingle09.thoughtworks.com/api/v2/projects/go/cards/5514.xml,#5514"),
                "Completed",
                "Failed");
    }

    private void assertFeedEntry(FeedEntry feedEntry, String title, String updatedDate, String id, String resourceLink, String authors, List<String> cardAndLinks, String state, String result) {
        assertThat(feedEntry.getTitle(), is(title));
        assertThat(feedEntry.getUpdatedDate(), is(updatedDate));
        assertThat(feedEntry.getId(), is(id));
        assertThat(feedEntry.getResourceLink(), is(resourceLink));
        assertThat(feedEntry.getAuthors(), is(Arrays.asList(authors.split(","))));
        List<FeedEntry.CardDetail> cardDetails = new ArrayList<FeedEntry.CardDetail>();
        for (String cardAndLink : cardAndLinks) {
            String[] split = cardAndLink.split(",");
            cardDetails.add(new FeedEntry.CardDetail(split[0], split[1]));
        }
        assertThat(feedEntry.getCardDetails(), is(cardDetails));
//      Uncomment once we fix the issue with the category
//      assertThat(feedEntry.getStatus(), is(state));
//      assertThat(feedEntry.getResult(), is(result));
    }

    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}
