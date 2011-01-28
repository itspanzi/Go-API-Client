package com.thoughtworks.go.domain;

import com.thoughtworks.go.util.XmlUtil;
import static com.thoughtworks.go.util.XmlUtil.*;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @understands a material associated with a Pipeline Instance. This means, this does not represent the flyweight of a material. Instead, its an aggregation of all the changes that went into a pipeline.
 */
public class Material {
    private final String type;
    private final String url;
    private final String username;
    private final String pipelineName;
    private final String stageName;
    private final List<Change> changes;
    private String checkExternals;

    private Material(String type, String url, String username, String checkExternals, String pipelineName, String stageName, List<Change> changes) {
        this.type = type;
        this.url = url;
        this.username = username;
        this.checkExternals = checkExternals;
        this.pipelineName = pipelineName;
        this.stageName = stageName;
        this.changes = changes;
    }

    public static Material create(String materialXml) {
        Document doc = parse(materialXml);
        String type = attrVal(singleNode(doc, "/material"), "type");
        String username = XmlUtil.attrVal(singleNode(doc, "/material"), "username", null);
        String pipelineName = attrVal(singleNode(doc, "/material"), "pipelineName", null);
        String stageName = attrVal(singleNode(doc, "/material"), "stageName", null);
        String checkExternals = attrVal(singleNode(doc, "/material"), "checkExternals", null);
        String url = attrVal(singleNode(doc, "/material"), "url", null);
        List<Change> changes = new ArrayList<Change>();
        List<Element> changeElements = XmlUtil.nodes(doc, "//changeset");
        for (Element changeElement : changeElements) {
            changes.add(new Change(nodeText(changeElement, ".//user"), nodeText(changeElement, ".//checkinTime"), nodeText(changeElement, ".//revision"), nodeText(changeElement, ".//message")));
        }
        return new Material(type, url, username, checkExternals, pipelineName, stageName, changes);
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public String getUsername() {
        return username;
    }

    public String getCheckExternals() {
        return checkExternals;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public String getStageName() {
        return stageName;
    }

    public static class Change {

        String user;
        String checkinTime;
        String revision;
        String message;

        public Change(String user, String checkinTime, String revision, String message) {
            this.user = user;
            this.checkinTime = checkinTime;
            this.revision = revision;
            this.message = message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Change change = (Change) o;

            if (checkinTime != null ? !checkinTime.equals(change.checkinTime) : change.checkinTime != null)
                return false;
            if (message != null ? !message.equals(change.message) : change.message != null) return false;
            if (revision != null ? !revision.equals(change.revision) : change.revision != null) return false;
            if (user != null ? !user.equals(change.user) : change.user != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = user != null ? user.hashCode() : 0;
            result = 31 * result + (checkinTime != null ? checkinTime.hashCode() : 0);
            result = 31 * result + (revision != null ? revision.hashCode() : 0);
            result = 31 * result + (message != null ? message.hashCode() : 0);
            return result;
        }

        public String getUser() {
            return user;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Material material = (Material) o;

        if (changes != null ? !changes.equals(material.changes) : material.changes != null) return false;
        if (checkExternals != null ? !checkExternals.equals(material.checkExternals) : material.checkExternals != null)
            return false;
        if (pipelineName != null ? !pipelineName.equals(material.pipelineName) : material.pipelineName != null)
            return false;
        if (stageName != null ? !stageName.equals(material.stageName) : material.stageName != null) return false;
        if (type != null ? !type.equals(material.type) : material.type != null) return false;
        if (url != null ? !url.equals(material.url) : material.url != null) return false;
        if (username != null ? !username.equals(material.username) : material.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (pipelineName != null ? pipelineName.hashCode() : 0);
        result = 31 * result + (stageName != null ? stageName.hashCode() : 0);
        result = 31 * result + (changes != null ? changes.hashCode() : 0);
        result = 31 * result + (checkExternals != null ? checkExternals.hashCode() : 0);
        return result;
    }
}
