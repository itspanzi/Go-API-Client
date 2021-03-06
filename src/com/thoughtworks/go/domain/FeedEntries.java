package com.thoughtworks.go.domain;

import static com.thoughtworks.go.util.XmlUtil.attrVal;
import static com.thoughtworks.go.util.XmlUtil.parse;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @understands a collection of a FeedEntry
 */
@SuppressWarnings("unchecked")
public class FeedEntries {
    private final List<FeedEntry> entries;
    private final String nextLink;

    private FeedEntries(List<FeedEntry> entries, String nextLink) {
        this.entries = entries;
        this.nextLink = nextLink;
    }

    public static FeedEntries create(String feedXml) {
        Document feedDoc = parse(feedXml);
        Element nextLinkElement = (Element) feedDoc.selectSingleNode(".//a:link[@rel='next']");
        return new FeedEntries(entries(feedDoc), nextLinkElement == null ? null : attrVal(nextLinkElement, "href"));
    }

    private static List<FeedEntry> entries(Document feedDoc) {
        List<FeedEntry> entries = new ArrayList<FeedEntry>();
        List<Element> elements = (List<Element>)feedDoc.selectNodes("//a:entry");
        for (Element element : elements) {
            entries.add(FeedEntry.create(element));
        }
        return entries;
    }

    /**
     * The entries from the feed.
     * @return list of feeds
     */
    public List<FeedEntry> getEntries() {
        return entries;
    }

    /**
     * The link to the next page of the feeds. Use this if you want to continue crawling the feeds.
     * @return the link to the next page of the feeds.
     */
    public String getNextLink() {
        return nextLink;
    }
}
