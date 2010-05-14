/**
 * O3S: Open Source Sport Software
 * Copyright (C) 2010 Benoit Pelletier
 * Contact: btpelletier@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 * --------------------------------------------------------------------------
 * $Id: pelletib $
 * --------------------------------------------------------------------------
 */
package net.o3s.apis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class NotificationMessage implements Serializable {

	private static final long serialVersionUID = 4944647457727378441L;

	public class MyElement implements java.io.Serializable {

		private static final long serialVersionUID = 1591239819107954444L;

		String uri;
		String localName;
		String qName;
		String value = null;
		Attributes attributes;

		public MyElement(String uri, String localName, String qName, Attributes attributes) {
			this.uri = uri;
			this.localName = localName;
			this.qName = qName;
			this.attributes = attributes;
		}

		public String getUri() {
			return uri;
		}

		public String getLocalName() {
			return localName;
		}

		public String getQname() {
			return qName;
		}

		public Attributes getAttributes() {
			return attributes;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	private class NotificationXmlHandler extends DefaultHandler {

		private String text;
		private MyElement current = null;
		private Vector<MyElement> vector = new Vector<MyElement>();
		public static final String NOTIFICATION_XML_ELEMENT_REGISTEREDID = "registeredId";
		public static final String NOTIFICATION_XML_ELEMENT_COMPETITIONID = "competitionId";
		public static final String NOTIFICATION_XML_ELEMENT_TYPE = "type";

		public NotificationXmlHandler() {
		}

		public void init() {
			vector.clear();
		}
		public void startElement(String uri, String name, String qName,
				Attributes atts) {
			current = new MyElement(uri, name, qName, atts);
			vector.addElement(current);
			text = new String();

		}

		public void endElement(String uri, String name, String qName) {
			if (current != null && text != null) {
				current.setValue(text.trim());
			}
			current = null;

		}

		// receive notification of character data
		public void characters(char ch[], int start, int length) {
			if (current != null && text != null) {
				String value = new String(ch, start, length);
				text += value;
			}
		}

		public Vector<MyElement> getVector() {
			return vector;
		}

	}

	public static final int NOTIFICATION_INT_TYPE_ARRIVAL = 0;
	public static final String NOTIFICATION_STR_TYPE_ARRIVAL = "Arrivee";
	public static final int NOTIFICATION_INT_TYPE_DEPARTURE = 1;
	public static final String NOTIFICATION_STR_TYPE_DEPARTURE = "Depart";

	private int registeredId;
	private int competitionId;

	public int getCompetitionId() {
		return competitionId;
	}

	public void setCompetitionId(int competitionId) {
		this.competitionId = competitionId;
	}

	private int type;

	private NotificationXmlHandler xmlHandler = new NotificationXmlHandler();

	public NotificationMessage() {

	}

	public int getRegisteredId() {
		return this.registeredId;
	}

	public void setRegisteredId(int registeredId) {
		this.registeredId = registeredId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return "NotificationMessage [" +
		        this.getType2String() + ", " +
		        this.getRegisteredId() + ", " +
		        this.getCompetitionId() + ", " +
		        "]";
	}

	public String toXML() {

		String xml = "<notification>" + "\n" +
		             "  <" + NotificationXmlHandler.NOTIFICATION_XML_ELEMENT_REGISTEREDID + ">" + this.getRegisteredId() + "</" + NotificationXmlHandler.NOTIFICATION_XML_ELEMENT_REGISTEREDID + ">" + "\n" +
		             "  <" + NotificationXmlHandler.NOTIFICATION_XML_ELEMENT_COMPETITIONID + ">" + this.getCompetitionId() + "</" + NotificationXmlHandler.NOTIFICATION_XML_ELEMENT_COMPETITIONID + ">" + "\n" +
		             "  <" + NotificationXmlHandler.NOTIFICATION_XML_ELEMENT_TYPE + ">" + this.getType2String().trim() + "</" + NotificationXmlHandler.NOTIFICATION_XML_ELEMENT_TYPE + ">" + "\n" +
		             "</notification>";
		return xml;

	}


	private String getType2String() {
		if (getType() == NotificationMessage.NOTIFICATION_INT_TYPE_ARRIVAL) {
			return NotificationMessage.NOTIFICATION_STR_TYPE_ARRIVAL;
		}
		if (getType() == NotificationMessage.NOTIFICATION_INT_TYPE_DEPARTURE) {
			return NotificationMessage.NOTIFICATION_STR_TYPE_DEPARTURE;
		}
		return "Unknown";
	}

	public synchronized void parseXml(String xmlString) throws NotificationMessageException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(false);
		SAXParser saxParser;
		try {
			saxParser = spf.newSAXParser();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			throw new NotificationMessageException("Error when parsing the XML string <" + xmlString + ">", e1);

		} catch (SAXException e2) {
			e2.printStackTrace();
			throw new NotificationMessageException("Error when parsing the XML string <" + xmlString + ">", e2);

		} catch (IllegalStateException e3) {
			e3.printStackTrace();
			throw new NotificationMessageException("Unexpected error <" + xmlString + ">", e3);

		}
		// create an XML reader
		XMLReader reader;
		try {
			reader = saxParser.getXMLReader();
		} catch (SAXException e1) {
			e1.printStackTrace();
			throw new NotificationMessageException("Error when parsing the XML string <" + xmlString + ">", e1);

		}

		// set handler
		xmlHandler.init();
		reader.setContentHandler(xmlHandler);

		InputStream is = null;
		try {
			is = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new NotificationMessageException("Error when parsing the XML string <"
					+ xmlString + ">", e);
		}

		// call parse on an input source
		try {
			reader.parse(new InputSource(is));
		} catch (IOException e) {
			e.printStackTrace();
			throw new NotificationMessageException("Error when parsing the XML string <"
					+ xmlString + ">", e);

		} catch (SAXException e) {
			e.printStackTrace();
			throw new NotificationMessageException("Error when parsing the XML string <"
					+ xmlString + ">", e);

		}

		Vector<MyElement> vector = xmlHandler.getVector();

		for(int index=0; index < vector.size(); index++) {

			MyElement myElement = (MyElement) vector.get(index);

			if (myElement.getQname().equals(NotificationXmlHandler.NOTIFICATION_XML_ELEMENT_REGISTEREDID)) {
				this.setRegisteredId(Integer.valueOf(myElement.getValue()).intValue());
			}

			if (myElement.getQname().equals(NotificationXmlHandler.NOTIFICATION_XML_ELEMENT_COMPETITIONID)) {
				this.setCompetitionId(Integer.valueOf(myElement.getValue()).intValue());
			}

			if (myElement.getQname().equals(NotificationXmlHandler.NOTIFICATION_XML_ELEMENT_TYPE)) {
				if (myElement.getValue().equals(NotificationMessage.NOTIFICATION_STR_TYPE_ARRIVAL)) {
					this.setType(NotificationMessage.NOTIFICATION_INT_TYPE_ARRIVAL);
				}
				if (myElement.getValue().equals(NotificationMessage.NOTIFICATION_STR_TYPE_DEPARTURE)) {
					this.setType(NotificationMessage.NOTIFICATION_INT_TYPE_DEPARTURE);
				}
			}

		}

	}

}
