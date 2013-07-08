package com.smfandroid.sleektodo.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.smfandroid.sleektodo.TodoItem;

public class XmlTodoDeSerialize {

	private XmlTodoDeSerialize() {
		
	}
	
	private XmlTodoDeSerialize(XmlTodoDeSerialize e) {
		
	}
	
	public static class TodoReadException extends Exception {
		private static final long serialVersionUID = 2L;
		public TodoReadException(String msg) {
			super(msg);
		}
	}
	
	public static String getValueIfItExists(Node n) {
		if(n != null)
			return n.getNodeValue();
		else
			return "";
	}
	
    public static List<TodoItem> parse(String fileName) throws IOException, TodoReadException {
    	FileInputStream fileInputS = new FileInputStream(fileName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();


        List<TodoItem> todoItems = new ArrayList<TodoItem>();

        try {
            DocumentBuilder builder;
    		builder = factory.newDocumentBuilder();
    		Document dom = builder.parse(fileInputS);	
	        Element root = dom.getDocumentElement();
	        NodeList items = root.getElementsByTagName(XmlContract.TAG_TODOITEM);
	
	        for (int i=0;i<items.getLength();i++){
	            TodoItem ti = new TodoItem();
	            Node item = items.item(i);
	            NodeList properties = item.getChildNodes();
	            
	            for (int j=0;j<properties.getLength();j++){
	                Node property = properties.item(j);
	                String name = property.getNodeName();
	                if (name.equalsIgnoreCase(XmlContract.TAG_CHECKED)){
	                    ti.mIsChecked = (getValueIfItExists(property.getFirstChild()).equals("true"));
	                } else if (name.equalsIgnoreCase(XmlContract.TAG_DATE)){
	                    ti.mDate = getValueIfItExists(property.getFirstChild());
	                } else if (name.equalsIgnoreCase(XmlContract.TAG_TEXT)){
	                    ti.mText = getValueIfItExists(property.getFirstChild());
	                } else if (name.equalsIgnoreCase(XmlContract.TAG_LONGTEXT)){
	                    ti.mLongText = getValueIfItExists(property.getFirstChild());
	                } else if (name.equalsIgnoreCase(XmlContract.TAG_FLAG)){
	                    ti.mFlag = Integer.valueOf(getValueIfItExists(property.getFirstChild()));
	                }
	            }
	            todoItems.add(ti);
	        }
        
		} catch (SAXException e) {
			throw new TodoReadException("internal error : " + e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new TodoReadException("internal error : " + e.getMessage());
		} finally {
	        fileInputS.close();
		}
		

        return todoItems;
    }
}
