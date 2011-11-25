package com.thoughtworks.go.domain;

import com.thoughtworks.go.util.XmlUtil;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @understands a feed entry
 *
 *     <entry>
        <title><![CDATA[pipeline(9) stage stage(1) Failed]]></title>
        <updated>2011-01-28T14:23:37+05:30</updated>
        <id>http://go03.thoughtworks.com:8153/go/pipelines/pair03/696/build/1</id>
        <author>
            <name><![CDATA[JJ]]></name>
        </author>
        <link title="build Stage Detail" href="http://go03.thoughtworks.com:8153/go/api/stages/9.xml" rel="alternate" type="application/vnd.go+xml"/>
        <link title="build Stage Detail" href="http://go03.thoughtworks.com:8153/go/pipelines/pair03/696/build/1" rel="alternate" type="text/html"/>

        <link title="pair03 Pipeline Detail" href="http://go03.thoughtworks.com:8153/go/api/pipelines/pair03/9.xml"
              rel="http://www.thoughtworks-studios.com/ns/relations/go/pipeline" type="application/vnd.go+xml"/>
        <link title="pair03 Pipeline Detail"
              href="http://go03.thoughtworks.com:8153/go/pipelines/pair03/696/build/1/pipeline"
              rel="http://www.thoughtworks-studios.com/ns/relations/go/pipeline" type="text/html"/>

        <link href="https://mingle09.thoughtworks.com//api/v2/projects/go/cards/5188.xml"
              rel="http://www.thoughtworks-studios.com/ns/go#related" type="application/vnd.mingle+xml" title="#5188"/>

        <category scheme="http://www.thoughtworks-studios.com/ns/categories/go" term="stage" label="Stage"/>
        <category scheme="http://www.thoughtworks-studios.com/ns/categories/go" term="completed" label="Completed"/>
        <category scheme="http://www.thoughtworks-studios.com/ns/categories/go" term="failed" label="Failed"/>
    </entry>

 */
public class FeedEntry {
    private String title;
    private String updatedDate;
    private String id;
    private String resoureLink;
    private List<String> authors;
    private List<CardDetail> cardDetails;
    private String status;
    private String result;

    public FeedEntry(String title, String updatedDate, String id, String resoureLink, List<String> names, List<CardDetail> cardDetails) {
        this.title = title;
        this.updatedDate = updatedDate;
        this.id = id;
        this.resoureLink = resoureLink;
        this.authors = names;
        this.cardDetails = cardDetails;
    }

    public String getTitle() {
        return title;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public String getId() {
        return id;
    }

    public String getResourceLink() {
        return resoureLink;
    }

    public static FeedEntry create(Element element) {
        String title = XmlUtil.nodeText(element, ".//a:title");
        String updatedDate = XmlUtil.nodeText(element, ".//a:updated");
        String id = XmlUtil.nodeText(element, ".//a:id");
        String resoureLink = XmlUtil.attrVal(link(element), "href");
        return new FeedEntry(title, updatedDate, id, resoureLink, authors(element), cardDetails(element));
    }

    private static List<CardDetail> cardDetails(Element element) {
        List<CardDetail> cardDetails = new ArrayList<CardDetail>();
        for (Element card : XmlUtil.nodes(element, ".//a:link[@type='application/vnd.mingle+xml']")) {
            cardDetails.add(new CardDetail(card.attributeValue("href"), card.attributeValue("title")));
        }
        return cardDetails;
    }

    private static List<String> authors(Element element) {
        List<String> names = new ArrayList<String>();
        for (Element node : XmlUtil.nodes(element, ".//a:author/a:name")) {
            names.add(node.getTextTrim());
        }
        return names;
    }

    private static Element link(Element element) {
        return (Element) element.selectSingleNode(".//a:link[@rel='alternate']");
    }

    public boolean matchesStage(String stageName) {
        return getTitle().matches(stageNameRegex(stageName));
    }

    private String stageNameRegex(String stageName) {
        return String.format("^.*?/\\d+/%s/\\d+", stageName);
    }

    public boolean matchesStage(String pipeline, String stage) {
        //"pipeline(9) stage stage(1) Failed"
        return getTitle().matches(feedTitleRegex(pipeline, stage));
    }

    private String feedTitleRegex(String pipeline, String stage) {
        return String.format("^%s\\(\\d+\\) stage %s\\(\\d+\\) .*", pipeline, stage);
    }

    public boolean matchesPipeline(String pipelineName) {
        //"pipeline(9) stage stage(1) Failed"
        return getTitle().matches(feedTitleRegex(pipelineName, ".*"));
    }

    public List<String> getAuthors() {
        return authors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeedEntry feedEntry = (FeedEntry) o;

        if (!authors.equals(feedEntry.authors)) return false;
        if (!cardDetails.equals(feedEntry.cardDetails)) return false;
        if (!id.equals(feedEntry.id)) return false;
        if (!resoureLink.equals(feedEntry.resoureLink)) return false;
        if (!title.equals(feedEntry.title)) return false;
        if (!updatedDate.equals(feedEntry.updatedDate)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + updatedDate.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + resoureLink.hashCode();
        result = 31 * result + authors.hashCode();
        result = 31 * result + cardDetails.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FeedEntry{" +
                "title='" + title + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                ", id='" + id + '\'' +
                ", resoureLink='" + resoureLink + '\'' +
                ", authors=" + authors +
                ", cardDetails=" + cardDetails +
                '}';
    }

    public List<CardDetail> getCardDetails() {
        return cardDetails;
    }

    public String getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }

    public static class CardDetail {

        private final String cardLink;
        private final String cardTitle;

        public CardDetail(String cardLink, String cardTitle) {
            this.cardLink = cardLink;
            this.cardTitle = cardTitle;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CardDetail that = (CardDetail) o;

            if (cardLink != null ? !cardLink.equals(that.cardLink) : that.cardLink != null) return false;
            if (cardTitle != null ? !cardTitle.equals(that.cardTitle) : that.cardTitle != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = cardLink != null ? cardLink.hashCode() : 0;
            result = 31 * result + (cardTitle != null ? cardTitle.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "CardDetail{" +
                    "cardLink='" + cardLink + '\'' +
                    ", cardTitle='" + cardTitle + '\'' +
                    '}';
        }
    }
}
