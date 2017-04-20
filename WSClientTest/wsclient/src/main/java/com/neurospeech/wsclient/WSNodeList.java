package com.neurospeech.wsclient;

import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings({ "serial"})
public class WSNodeList extends Vector<Node> implements NodeList
{

	@Override
	public int getLength() {
		return this.size();
	}

	@Override
	public Node item(int index) {
		return this.elementAt(index);
	}
	
}
