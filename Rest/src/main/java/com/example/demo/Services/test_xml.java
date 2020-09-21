package com.example.demo.Services;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class test_xml {
    public Graph torontoGraph;
/**
    public static void main(String[] args) {
        //an instance of factory that gives a document builder
        Graph torontoGraph = new Graph("./data/toronto.osm", "./data/Cyclists.csv");
        torontoGraph.loadFiles("./data/toronto.osm", "./data/Cyclists.csv");
        NodeList nList = torontoGraph.osmDoc.getElementsByTagName("node");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            System.out.println("");    //Just a separator
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                System.out.println(eElement.getAttribute("lon"));
            }
            /**
             DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
             //an instance of builder to parse the specified xml file
             DocumentBuilder db;

             {
             try {
             db = dbf.newDocumentBuilder();
             Document document = db.parse(new File("./data/toronto.osm"));

             XPathFactory xpathFactory = XPathFactory.newInstance();
             XPath xpath = xpathFactory.newXPath();
             NodeList nodelist = (NodeList) xpath.evaluate("osm/node", document, XPathConstants.NODE);
             for(int i = 0; i < nodelist.getLength(); i++){
             Node nNode = nodelist.item(i);
             System.out.println("\nCurrent Element :" + nNode.getNodeName());
             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
             Element eElement = (Element) nNode;
             System.out.println("latitude :" + eElement.getAttribute("lat"));
             }
             }

             document.getDocumentElement().normalize();
             Element root = document.getDocumentElement();
             System.out.println(root.getNodeName());
             NodeList nList = document.getElementsByTagName("node");
             System.out.println("============================");
             for (int temp = 0; temp < 5; temp++)
             {
             Node node = nList.item(temp);
             System.out.println("");    //Just a separator
             if (node.getNodeType() == Node.ELEMENT_NODE)
             {
             Element eElement = (Element) node;
             System.out.println(eElement.getAttribute("lon"));
             }
             }
             } catch (ParserConfigurationException e) {
             e.printStackTrace();
             } catch (SAXException e) {
             e.printStackTrace();
             } catch (IOException e) {
             e.printStackTrace();
             }
             }
        }
    }**/
}
