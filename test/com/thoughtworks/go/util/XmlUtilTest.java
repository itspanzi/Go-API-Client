package com.thoughtworks.go.util;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.io.File;

public class XmlUtilTest {

    @Test
    public void shouldReturnAttributeValue() throws Exception {
        Document document = XmlUtil.parse(file("job-1.xml"));
        assertThat(XmlUtil.attrVal(XmlUtil.singleNode(document, "//pipeline"), "name"), is("pipeline"));
    }

    @Test
    public void shouldReturnDefaultValueIfAttributeNotFound() throws Exception {
        Document document = XmlUtil.parse(file("job-1.xml"));
        assertThat(XmlUtil.attrVal(XmlUtil.singleNode(document, "//pipeline"), "foo", "pavan"), is("pavan"));
    }

    @Test
    public void shouldReturnNodeTextInAGivenDocument() throws Exception {
        Document document = XmlUtil.parse(file("job-1.xml"));
        assertThat(XmlUtil.nodeText(document, "//state"), is("Completed"));
    }

    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}
