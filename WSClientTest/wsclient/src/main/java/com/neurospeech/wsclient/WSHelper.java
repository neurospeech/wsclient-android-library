package com.neurospeech.wsclient;

import java.util.Date;
import java.util.Vector;


import org.w3c.dom.*;



public class WSHelper {

	public Document document;
	
	public WSHelper(Document doc) {
		document = doc;
	}
	
	
	static String getString(Document doc)
	{
		
		
		StringWriter sw = new StringWriter();
		
		
		Element e = doc.getDocumentElement();
		
		Vector nsList = new Vector();
		
		writeElement(sw,e,nsList);
		
		
		return sw.toString();
	}
	
	private static boolean contains(Vector list, String ns){
		int count = list.size();
		for(int i=0;i<count;i++){
			String item = (String)list.elementAt(i);
			if(item.equalsIgnoreCase(ns))
				return true;
		}
		return false;
	}
	
	private static void writeElement(StringWriter sw, Element e, Vector nsList)
	{
		int count;
		int i;
		sw.write("<" + e.getNodeName());
		
		NamedNodeMap map = e.getAttributes();
		
		if(map.getLength()>0)
		{
			map = e.getAttributes();
			count = map.getLength();
			for(i=0; i<count; i++)
			{
				Node n = map.item(i);
				String name = n.getNodeName();
				String value = n.getNodeValue();
				sw.write(" " + name + "=\"" + getEncoded(value) + "\"");
				if(name.equalsIgnoreCase("xmlns") || name.startsWith("xmlns:")){
					nsList.addElement(value);
				}
			}
		}
		
		String ns = e.getNamespaceURI();
		if(ns != null && ns.length()>0 && !contains(nsList,ns))
		{
			nsList.addElement(ns);
			String prefix = e.getPrefix();
			if(prefix==null || prefix.length()==0)
				prefix = "";
			else
				prefix = ":" + prefix;
				
			sw.write(" xmlns" + prefix + "=\"" + ns + "\"");
		}
		
		if(e.hasChildNodes())
		{
			sw.write(">");
			NodeList list = e.getChildNodes();
			count = list.getLength();
			for(i=0;i<count;i++)
			{
				Vector childNSList = copyVector(nsList);
				
				Node n = list.item(i);
				if(n instanceof Text)
				{
					sw.write(getEncoded(n.getNodeValue()));
				}
				else
				{
					writeElement(sw, (Element)n,childNSList);
				}
			}
			sw.write("</" + e.getNodeName() + ">");
		}
		else
		{
			sw.write("/>");
		}
	}
	
	private static Vector copyVector(Vector v){
		Vector nv = new Vector();
		int c = v.size();
		for(int i=0;i<c;i++){
			nv.addElement(v.elementAt(i));
		}
		return nv;
	}
	
	public static String replaceAll(String source, String pattern,
            String replacement) {
        if (source == null) {
            return "";
        }
       
        StringBuffer sb = new StringBuffer();
        int idx = -1;
        int patIdx = 0;

        while ((idx = source.indexOf(pattern, patIdx)) != -1) {
            sb.append(source.substring(patIdx, idx));
            sb.append(replacement);
            patIdx = idx + pattern.length();
        }
        sb.append(source.substring(patIdx));
        return sb.toString();

    }	
	
	private static String getEncoded(String text)
	{
		if(text.length()==0)
			return text;
		text = replaceAll(text, "&", "&amp;");
		text = replaceAll(text, "<", "&lt;");
		text = replaceAll(text, ">", "&gt;");
		return text;
	}
	
	public void setText(Element e, String text){
		Text txt = document.createTextNode(text);
		e.appendChild(txt);
	}
	
	public void addChild(Element root, String name, String value, boolean attribute)
	{
		if(value==null)
			return;
		if(attribute){
			root.setAttribute(name, value);
		}
		else
		{
			Element e = createElement(name);
			
			Text txt = document.createTextNode(value);
			e.appendChild(txt);
			root.appendChild(e);
		}
	}
	
	public Element createElement(String name) {
		return XmlDocumentBuilder.createElement(document, name);
	}


	public void addChildNode(Element root, String name, Element child, WSObject obj){
		Element node = createElement(name);
		if(child!=null){
			node.appendChild(child);
		}else{
			if(obj==null)
				return;
			obj.fillXML(this,node);
		}
		root.appendChild(node);
	}
	
	public void addChildNodeNS(Element root, String nsURI, String name, WSObject obj){
		if(obj==null)
			return;
		Element node = document.createElementNS(nsURI, name);
		obj.fillXML(this,node);
		root.appendChild(node);
	}
	
	
	public void addChildArray(Element root, String name, String type, Vector array){
		if(array==null || array.size()==0)
			return;
		Element arrayElement = createElement(name);
		root.appendChild(arrayElement);
		int i;
		if(type==null){
			for(i=0;i<array.size();i++){
				WSObject st = (WSObject)array.elementAt(i);
				arrayElement.appendChild(st.toXMLElement(this,arrayElement));
			}
		}else{
			for(i=0;i<array.size();i++){
				Element e = createElement(type);
				Object obj = array.elementAt(i);
				String val = obj.toString();
				if(obj.getClass().equals(Date.class)){
					val = stringValueOf((Date)obj);
				}
				Text txt = document.createTextNode(val);
				e.appendChild(txt);
				arrayElement.appendChild(e);
			}
		}
	}
	
	public void addChildArrayInline(Element root, String name, String type, Vector array){
		if(array==null || array.size()==0)
			return;
		int i;
		Element arrayElement = root;
		if(type==null){
			for(i=0;i<array.size();i++){
				WSObject st = (WSObject)array.elementAt(i);
				Element e = createElement(name);
				st.fillXML(this,e);
				arrayElement.appendChild(e);
			}
		}else{
			for(i=0;i<array.size();i++){
				Element e = createElement(name);
				Object obj = array.elementAt(i);
				String val = obj.toString();
				if(obj.getClass().equals(Date.class)){
					val = stringValueOf((Date)obj);
				}
				Text txt = document.createTextNode(val);
				e.appendChild(txt);
				arrayElement.appendChild(e);
			}
		}
	}
	
	public static NodeList getChildren(Element root, String name){
		NodeList childList = root.getChildNodes();
		WSNodeList list = new WSNodeList(); 
		int n = childList.getLength();
		for(int i=0;i<n;i++){
			Node node = childList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				if(name==null)
					list.addElement(node);
				else if(node.getLocalName().equals(name))
					list.addElement(node);
			}
		}
		return list;
	}
	
	public static NodeList getElementChildren(Element root, String name){
		Element e = getElement(root, name);
		if(e==null)
			return null;
		return getChildren(e,null);
	}
	

	public static Element getElement(Element root, String name)
	{
		if(name==null)
			return null;
		NodeList list = getChildren(root,name);
		if(list.getLength()>0)
			return (Element)list.item(0);
		return null;
	}
	
	public static Element getElementNS(Element root, String nsURI, String name)
	{
		if(name==null)
			return null;
		NodeList list = root.getElementsByTagNameNS(nsURI,name);
		if(list.getLength()>0)
			return (Element)list.item(0);
		return null;
	}
	
	public static String getString(Element root, String name, boolean isAttribute)
	{
		if(isAttribute)
		{
			return root.getAttribute(name);	
		}
		if(name==null)
		{
			Text txt = (Text)root.getFirstChild();
			if(txt==null)
				return null;
			return txt.getNodeValue();
		}
		Element child = getElement(root, name);
		if(child==null)
			return null;
		Text txt = (Text)child.getFirstChild();
		if(txt==null)
			return null;
		return txt.getNodeValue();
	}
	
	
	public static Boolean getBoolean(Element root, String name, boolean isAttribute)
	{
		String val = getString(root, name, isAttribute);
		if(val==null)
			return new Boolean(false);
		if(val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes"))
			return new Boolean(true);
		return new Boolean(false);
	}
	
	public static Date getDate(Element root, String name, boolean isAttribute) throws Exception
	{
		String val = getString(root, name, isAttribute);
		if(val==null)
			return null;
		return WSDateParser.parse(val);
	}

	public static Long getLong(Element root, String name, boolean isAttribute)
	{
		String val = getString(root, name, isAttribute);
		if(val==null)
			return new Long(0);
		return new Long(Long.parseLong(val));
	}
	
	public static Double getDouble(Element root, String name, boolean isAttribute)
	{
		String val = getString(root, name, isAttribute);
		if(val==null)
			return new Double(0);
		return new Double(Double.parseDouble(val));
	}
	
	public static Integer getInteger(Element root, String name, boolean isAttribute)
	{
		String val = getString(root, name, isAttribute);
		if(val==null)
			return new Integer(0);
		return new Integer(Integer.parseInt(val));
	}
	
	public static Float getFloat(Element root, String name, boolean isAttribute)
	{
		String val = getString(root, name, isAttribute);
		if(val==null)
			return new Float(0);
		return new Float(Float.parseFloat(val));
	}
	
	public String stringValueOf(Date date){
		return WSDateParser.toString(date);
	}
	
	
}
