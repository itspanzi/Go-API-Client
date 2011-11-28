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

    /**
     * Parse the given string which is an XML. The default namespace registered is "a". Hence all XPaths need to use this as their namespace.
     * @param xml xml as string
     * @return the Document object representing this XML
     */
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

    /**
     * Returns the value of the attribute for this element.
     *
     * @param element element
     * @param attrName name of the attribute
     * @return the value of the said attribute
     */
    public static String attrVal(Element element, String attrName) {
        return attr(element, attrName).getValue();
    }

    /**
     * Returns the value of the attribute for this element. If this attribute is not found, return the default value that
     * is passed in.
     *
     * @param element element
     * @param attrName name of the attribute
     * @param defaultValue the default value to be returned if the attribute is not found
     * @return the value of the said attribute or the passed in default value
     */
    public static String attrVal(Element element, String attrName, String defaultValue) {
        Attribute attribute = attr(element, attrName);
        return attribute == null ? defaultValue : attribute.getValue();
    }

    private static Attribute attr(Element element, String attrName) {
        return element.attribute(attrName);
    }

    /**
     * Returns the first element that matches this xpath. The xpath should use appropriate namespace.
     * For example: ".//a:stage" says that "stage" is a node in the namespace with alias "a".
     *
     * @param doc the Document object
     * @param xpath the xpath
     * @return the first match that matches this xpath
     */
    public static Element singleNode(Document doc, String xpath) {
        return (Element) doc.selectSingleNode(xpath);
    }

    /**
     * Returns all the elements that match a given xpath in the passed in document.
     *
     * @param doc document
     * @param xpath xpath with the right namespaces.
     * @return list of all the elements
     */
    @SuppressWarnings({"unchecked"})
    public static List<Element> nodes(Document doc, String xpath) {
        return doc.selectNodes(xpath);
    }

    /**
     * Returns all the elements that match a given xpath in the passed in document.
     *
     * @param element element
     * @param xpath xpath with the right namespaces.
     * @return list of all the elements
     */
    @SuppressWarnings({"unchecked"})
    public static List<Element> nodes(Element element, String xpath) {
        return element.selectNodes(xpath);
    }

    /**
     * Returns the text node of the first element in the document which matches the given xpath.
     * @param doc document
     * @param xpath xpath with the right namespaces
     * @return the text node of the matched element
     */
    public static String nodeText(Document doc, String xpath) {
        return singleNode(doc, xpath).getText();
    }

    /**
     * Returns the text node of the first element in the document which matches the given xpath.
     * @param element element
     * @param xpath xpath with the right namespaces
     * @return the text node of the matched element
     */
    public static String nodeText(Element element, String xpath) {
        Node node = element.selectSingleNode(xpath);
        return node == null ? null : node.getText();
    }
}
