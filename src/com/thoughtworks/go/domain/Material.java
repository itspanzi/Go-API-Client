package com.thoughtworks.go.domain;

import com.thoughtworks.go.util.XmlUtil;
import static com.thoughtworks.go.util.XmlUtil.*;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @understands a material instance associated with a Pipeline Instance.
 *
 * This is an aggregation of all the changes that went into a pipeline for the given material tag inside a pipeline.
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

    /**
     * The type of the material. It could be one of HgMaterial, SvnMaterial, GitMaterial, P4Material or DependencyMaterial.
     * Once Go has plugins, it is the type of the material as given by the plugin.
     *
     * @return the type of the material.
     */
    public String getType() {
        return type;
    }

    /**
     * This is the URL of the material. Since not all materials have a notion of a URL, make sure a null check is done.
     *
     * For example a dependency material does not have this field populated.
     *
     * @return The URL of the material or null if it does not apply for the given material type.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns a list of change sets of this material for this pipeline instance. Currently there is no way of figuring
     * out if this change set is new i.e a pipeline was not built with this change set before or not. This means every
     * material has at least one change set.
     *
     * @return a list of change sets for a given material
     */
    public List<Change> getChanges() {
        return changes;
    }

    /**
     * Returns the user name configured for the material. This is generally used by Go to authenticate against the material.
     * This may be null if none is configured or if the material does not have a username.
     *
     * @return The username configured for the material or null if it is not configured or if it does not apply for the given material type.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns if the given SVN material is configured to check svn externals for new modifications.
     *
     * @return if check externals is turned on or not. If this does not apply to the given material type, returns null.
     */
    public Boolean getCheckExternals() {
        return null == checkExternals ? null : Boolean.parseBoolean(checkExternals);
    }

    /**
     * Returns the upstream pipeline name which this dependency material depends on. This applies only to DependencyMaterial type.
     *
     * This method can return null. If the material type is actually DependencyMaterial, then a 'null' means this is the same as the
     * pipeline to which this material belongs to. If the type is not DependencyMaterial, then a 'null' just indicates the type difference. 
     *
     * @return the name of the upstream pipeline which this dependency material depends on or null.
     */
    public String getPipelineName() {
        return pipelineName;
    }

    /**
     * Returns the upstream pipeline's stage name which this dependency material depends on. This applies only to DependencyMaterial type.
     *
     * This method can return null which just indicates the type difference.
     *
     * @return the name of the upstream pipeline's stage which this dependency material depends on or null.
     */
    public String getStageName() {
        return stageName;
    }

    /**
     * @understands Representing a single change set of a given material
     */
    public static class Change {

        /**
         * The user who made this change. This applies mostly to Source Control Repositories where there is a notion of
         * user who made the change. This will be null for materials of type DependencyMaterial.
         */
        public final String user;

        /**
         * This is the time when the change set was made. This is as reported by the material or the time when the upstream
         * stage instance was completed, based on the type of the material
         */
        public final String checkinTime;

        /**
         * This is the revision number of the changeset. Based on what material, the format of this may change.
         *
         * For example, SVN would have a monotonically increasing integer as the revision. Git & Hg use a hash to denote the same.
         * Dependency materials use a revision of the format "pipeline_name/pipeline_counter/stage_name/stage_counter".
         */
        public final String revision;

        /**
         * This is the message associated with the given changeset. Based on the material type, this may or may not be null.
         * For dependency materials, this is null.
         */
        public final String message;

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
