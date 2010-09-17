package com.thoughtworks.go.domain;

import org.dom4j.Element;
import org.dom4j.Node;
import com.thoughtworks.go.util.XmlUtil;

/**
 * @understands a feed entry
 */
public class FeedEntry {
    private String title;
    private String updatedDate;
    private long id;
    private String resoureLink;

    public FeedEntry(String title, String updatedDate, long id, String resoureLink) {
        this.title = title;
        this.updatedDate = updatedDate;
        this.id = id;
        this.resoureLink = resoureLink;
    }

    public String getTitle() {
        return title;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public long getId() {
        return id;
    }

    public String getResourceLink() {
        return resoureLink;
    }

    public static FeedEntry create(Element element) {
        String title = XmlUtil.nodeText(element, ".//a:title");
        String updatedDate = XmlUtil.nodeText(element, ".//a:updated");
        long id = Long.parseLong(XmlUtil.nodeText(element, ".//a:id"));
        String resoureLink = XmlUtil.attrVal(link(element), "href");
        return new FeedEntry(title, updatedDate, id, resoureLink);
    }

    private static Element link(Element element) {
        return (Element) element.selectSingleNode(".//a:link[@rel='alternate']");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeedEntry feedEntry = (FeedEntry) o;

        if (id != feedEntry.id) return false;
        if (resoureLink != null ? !resoureLink.equals(feedEntry.resoureLink) : feedEntry.resoureLink != null)
            return false;
        if (title != null ? !title.equals(feedEntry.title) : feedEntry.title != null) return false;
        if (updatedDate != null ? !updatedDate.equals(feedEntry.updatedDate) : feedEntry.updatedDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (updatedDate != null ? updatedDate.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (resoureLink != null ? resoureLink.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FeedEntry{" +
                "title='" + title + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                ", id=" + id +
                ", resoureLink='" + resoureLink + '\'' +
                '}';
    }

    public boolean matchesStage(String stageName) {
        return getTitle().matches(stageNameRegex(stageName));
    }

    private String stageNameRegex(String stageName) {
        return String.format("^.*?/\\d+/%s/\\d+", stageName);
    }
}
