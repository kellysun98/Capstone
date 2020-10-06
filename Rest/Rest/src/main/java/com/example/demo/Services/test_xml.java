package com.example.demo.Services;

import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.supercsv.io.CsvListWriter;


public class test_xml {
    public Graph torontoGraph;


    public static void main(String[] args) throws IOException {
        /**
        Graph torontoGraph = new Graph("./data/toronto.osm", "./data/Cyclists.csv");
        torontoGraph.buildGraph();
        StringWriter output = new StringWriter();
        try (ICsvListWriter listWriter = new CsvListWriter(output, CsvPreference.STANDARD_PREFERENCE)){
            for (HashMap.Entry<Double, MapNode> entry : torontoGraph.routeNodes.entrySet()){
                listWriter.write(entry.getKey(), entry.getValue());
            }
        }
        System.out.println(output);
        }*/
        Graph torontoGraph = new Graph("./data/toronto.osm", "./data/Cyclists.csv");
        torontoGraph.loadFiles("./data/toronto.osm", "./data/Cyclists.csv");
        NodeList nList = torontoGraph.osmDoc.getElementsByTagName("node");
        HashMap<Double, HashMap<String, Double>> test_list = new HashMap<Double, HashMap<String, Double>>();
        for (int temp = 0; temp < 5; temp++) {
            Node node = nList.item(temp);
            System.out.println("");    //Just a separator
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                HashMap<String, Double> temp_hm = new HashMap<>();
                MapNode test_node = new MapNode(eElement);
                temp_hm.put(test_node.id + "_longitude", test_node.longitude);
                temp_hm.put(test_node.id + "_latitude", test_node.latitude);
                test_list.put(test_node.id, temp_hm);
                System.out.println(test_list.get(test_node.id));
            }
        }
        FileWriter output_csv = new FileWriter("./data/test.csv");
        StringWriter output = new StringWriter();
        try (ICsvListWriter listWriter = new CsvListWriter(output_csv, CsvPreference.STANDARD_PREFERENCE)) {
            for (HashMap.Entry<Double, HashMap<String, Double>> entry : test_list.entrySet()) {
                for (Map.Entry<String, Double> nested : entry.getValue().entrySet()){
                    listWriter.write(entry.getKey(), (new ArrayList<Double>(Collections.singleton(nested.getValue()))).get(0), (new ArrayList<Double>(Collections.singleton(nested.getValue()))).get(0));
                }

            }
        }
        finally {
            output.close();
        }
        }


        /**
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
