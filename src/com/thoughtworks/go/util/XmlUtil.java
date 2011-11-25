package com.thoughtworks.go.util;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @understands some common API used to deal with XML
 */
public class XmlUtil {

    public static Document parse(String xml) {
        try {
            registerNameSpace();
            SAXReader reader = new SAXReader();
            return reader.read(new StringReader(xml));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerNameSpace() {
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("a", "http://www.w3.org/2005/Atom");
        DocumentFactory.getInstance().setXPathNamespaceURIs(namespaces);
    }

    public static Element singleNode(Document doc, String xpath) {
        return (Element) doc.selectSingleNode(xpath);
    }

    public static String attrVal(Element element, String attrName) {
        return attr(element, attrName).getValue();
    }

    private static Attribute attr(Element element, String attrName) {
        return element.attribute(attrName);
    }

    public static String nodeText(Document doc, String xpath) {
        return singleNode(doc, xpath).getText();
    }

    @SuppressWarnings({"unchecked"})
    public static List<Element> nodes(Document doc, String xpath) {
        return doc.selectNodes(xpath);
    }

    @SuppressWarnings({"unchecked"})
    public static List<Element> nodes(Element element, String xpath) {
        return element.selectNodes(xpath);
    }

    public static String attrVal(Element element, String attrName, String defaultValue) {
        Attribute attribute = attr(element, attrName);
        return attribute == null ? defaultValue : attribute.getValue();
    }

    public static String nodeText(Element element, String xpath) {
        Node node = element.selectSingleNode(xpath);
        return node == null ? null : node.getText();
    }
}
