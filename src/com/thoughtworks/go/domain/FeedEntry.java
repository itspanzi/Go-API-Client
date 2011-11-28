package com.thoughtworks.go.domain;

import com.thoughtworks.go.util.XmlUtil;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @understands a feed entry
 *
 * <pre>
 *     This is a sample feed entry from the Go server.
 * <entry>
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
   </pre>
 */
public class FeedEntry {
    private String title;
    private String updatedDate;
    private String id;
    private String resoureLink;
    private List<String> authors;
    private List<CardDetail> cardDetails;

    public FeedEntry(String title, String updatedDate, String id, String resoureLink, List<String> names, List<CardDetail> cardDetails) {
        this.title = title;
        this.updatedDate = updatedDate;
        this.id = id;
        this.resoureLink = resoureLink;
        this.authors = names;
        this.cardDetails = cardDetails;
    }

    public static FeedEntry create(Element element) {
        String title = XmlUtil.nodeText(element, ".//a:title");
        String updatedDate = XmlUtil.nodeText(element, ".//a:updated");
        String id = XmlUtil.nodeText(element, ".//a:id");
        String resoureLink = XmlUtil.attrVal(link(element), "href");
        return new FeedEntry(title, updatedDate, id, resoureLink, authors(element), cardDetails(element));
    }

    /**
     * The title of the feed. This is of the format: "pipeline_name(pipeline_counter) stage stage_name(1_counter) stage_result". The spaces
     * and the special characters are important.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * The updated time of the feed entry. This is the stage completion time.
     *
     * @return the stage completion time in ISO8601 format.
     */
    public String getUpdatedDate() {
        return updatedDate;
    }

    /**
     * This is the ID of the stage instance. Its a URI of the stage instance.
     *
     * @return the URI of the stage instance that this feed entry represents.
     */
    public String getId() {
        return id;
    }

    /**
     * This is the link to the stage instance UI.
     *
     * @return the link to the stage instance UI.
     */
    public String getResourceLink() {
        return resoureLink;
    }

    /**
     * Returns if this feed entry is for a stage instance that is from the given pipeline and stage
     *
     * @param pipeline the name of a pipeline
     * @param stage the name of a stage
     *
     * @return if this feed entry is for the right stage instance
     */
    public boolean matchesStage(String pipeline, String stage) {
        //"pipeline(9) stage stage(1) Failed"
        return getTitle().matches(feedTitleRegex(pipeline, stage));
    }

    /**
     * Returns if this feed entry is for a stage instance that is from the given stage.
     *
     * @param stageName the name of the stage
     *
     * @return if the feed entry is for the right stage.
     */
    public boolean matchesStage(String stageName) {
        return getTitle().matches(feedTitleRegex(".*", stageName));
    }

    /**
     * Returns if this feed entry is for a stage instance that is from the given pipeline.
     *
     * @param pipelineName the name of the pipeline
     *
     * @return if the feed entry is for the right pipeline.
     */
    public boolean matchesPipeline(String pipelineName) {
        //"pipeline(9) stage stage(1) Failed"
        return getTitle().matches(feedTitleRegex(pipelineName, ".*"));
    }

    /**
     * Returns the list of all people who have committed code to cause the pipeline that the stage this feed entry represents belongs to.
     * This is constructed from the names used in the commits.
     *
     * @return the list of all people who have checked in code.
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * Returns the list of all cards that were worked upon in the pipeline instance to which this stage belongs to.
     *
     * @return a list of all {link CardDetail}
     */
    public List<CardDetail> getCardDetails() {
        return cardDetails;
    }

    private String feedTitleRegex(String pipeline, String stage) {
        return String.format("^%s\\(\\d+\\) stage %s\\(\\d+\\) .*", pipeline, stage);
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

    /**
     * @understands The details of a single Mingle Card.
     */
    public static class CardDetail {

        private final String cardLink;
        private final String cardTitle;

        public CardDetail(String cardLink, String cardTitle) {
            this.cardLink = cardLink;
            this.cardTitle = cardTitle;
        }

        /**
         * Returns the link to the mingle card.
         *
         * @return The link to the mingle card
         */
        public String getCardLink() {
            return cardLink;
        }

        /**
         * This is the match from the checkin comment that was parsed and identified as the mingle card number.
         *
         * For example: #5155 is the card title identified from the checkin comment: "worked on #5155 and fixed it."
         *
         * @return the title of the card i.e. the match obtained from the checkin comment that was identified as the card number.
         */
        public String getCardTitle() {
            return cardTitle;
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
