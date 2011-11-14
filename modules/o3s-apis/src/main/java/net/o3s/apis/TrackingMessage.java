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
 * $Id$
 * --------------------------------------------------------------------------
 */
package net.o3s.apis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class TrackingMessage implements Serializable {


	/**
	 * Logger
	 */
    private static Logger logger = Logger.getLogger(TrackingMessage.class.getName());


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

	private class EventXmlHandler extends DefaultHandler {

		private String text;
		private MyElement current = null;
		private Vector<MyElement> vector = new Vector<MyElement>();
		public static final String EVENT_XML_ELEMENT_LABEL = "label";
		public static final String EVENT_XML_ELEMENT_CTIME = "creationTime";
		public static final String EVENT_XML_ELEMENT_ETIME = "elapsedTime";
		public static final String EVENT_XML_ELEMENT_ORIGIN = "origin";
		public static final String EVENT_XML_ELEMENT_TYPE = "type";

		public EventXmlHandler() {
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

	public static final int EVENT_INT_TYPE_ARRIVAL_CTIME = 0;
	public static final String EVENT_STR_TYPE_ARRIVAL_CTIME = "ArrivalByCreationTime";
	public static final int EVENT_INT_TYPE_ARRIVAL_ETIME = 1;
	public static final String EVENT_STR_TYPE_ARRIVAL_ETIME = "ArrivalByElapsedTime";

	private String labelValue;

	private Date creationTime;

	private int elapsedTime;

	// where the event comes from
	private String origin;

	private int type;

	private EventXmlHandler xmlHandler = new EventXmlHandler();

	public TrackingMessage() {

	}

	public String getLabelValue() {
		return labelValue;
	}

	public void setLabelValue(String labelValue) {
		this.labelValue = labelValue;
	}

	public int getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(int elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return "TrackingMessage [" +
		        this.getType2String() + ", " +
		        this.getLabelValue() + ", " +
		        this.getOrigin() + ", " +
		        this.getCreationTime2String() + ", " +
		        this.getElapsedTime() + ", " +
		        "]";
	}

	public String toXML() {

		String xml = "<event>" + "\n" +
		             "  <" + EventXmlHandler.EVENT_XML_ELEMENT_LABEL + ">" + this.getLabelValue().trim() + "</" + EventXmlHandler.EVENT_XML_ELEMENT_LABEL + ">" + "\n" +
		             "  <" + EventXmlHandler.EVENT_XML_ELEMENT_ORIGIN + ">" + this.getOrigin().trim() + "</" + EventXmlHandler.EVENT_XML_ELEMENT_ORIGIN + ">" + "\n" +
		             "  <" + EventXmlHandler.EVENT_XML_ELEMENT_TYPE + ">" + this.getType2String().trim() + "</" + EventXmlHandler.EVENT_XML_ELEMENT_TYPE + ">" + "\n";
		if (this.getCreationTime() != null) {
			xml += "  <" + EventXmlHandler.EVENT_XML_ELEMENT_CTIME + ">" + this.getCreationTime2String().trim() + "</" + EventXmlHandler.EVENT_XML_ELEMENT_CTIME + ">" + "\n";
		}
		if (this.getElapsedTime() > 0) {
			xml += "  <" + EventXmlHandler.EVENT_XML_ELEMENT_ETIME + ">" + this.getElapsedTime() + "</" + EventXmlHandler.EVENT_XML_ELEMENT_ETIME + ">" + "\n";
		}
		xml += "</event>";
		return xml;

	}

	private String getCreationTime2String() {

		try {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
			if (this.getCreationTime() != null) {
				return df.format(this.getCreationTime());
			} else {
				return "";
			}
		} catch (Exception e) {
			logger.severe("error parsing date - " + this.getCreationTime());
			return "";
		}
	}

	private String getType2String() {
		if (getType() == TrackingMessage.EVENT_INT_TYPE_ARRIVAL_CTIME) {
			return TrackingMessage.EVENT_STR_TYPE_ARRIVAL_CTIME;
		} else {
			if (getType() == TrackingMessage.EVENT_INT_TYPE_ARRIVAL_ETIME) {
				return TrackingMessage.EVENT_STR_TYPE_ARRIVAL_ETIME;
			}
		}
		return "Unknown";
	}

	public void parseXml(String xmlString) throws TrackingMessageException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(false);
		SAXParser saxParser;
		try {
			saxParser = spf.newSAXParser();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			throw new TrackingMessageException("Error when parsing the XML string <" + xmlString + ">", e1);

		} catch (SAXException e1) {
			e1.printStackTrace();
			throw new TrackingMessageException("Error when parsing the XML string <" + xmlString + ">", e1);

		}
		// create an XML reader
		XMLReader reader;
		try {
			reader = saxParser.getXMLReader();
		} catch (SAXException e1) {
			e1.printStackTrace();
			throw new TrackingMessageException("Error when parsing the XML string <" + xmlString + ">", e1);

		}

		// set handler
		xmlHandler.init();
		reader.setContentHandler(xmlHandler);

		InputStream is = null;
		try {
			is = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new TrackingMessageException("Error when parsing the XML string <"
					+ xmlString + ">", e);
		}

		// call parse on an input source
		try {
			reader.parse(new InputSource(is));
		} catch (IOException e) {
			e.printStackTrace();
			throw new TrackingMessageException("Error when parsing the XML string <"
					+ xmlString + ">", e);

		} catch (SAXException e) {
			e.printStackTrace();
			throw new TrackingMessageException("Error when parsing the XML string <"
					+ xmlString + ">", e);

		}

		Vector<MyElement> vector = xmlHandler.getVector();

		for(int index=0; index < vector.size(); index++) {

			MyElement myElement = (MyElement) vector.get(index);

			if (myElement.getQname().equals(EventXmlHandler.EVENT_XML_ELEMENT_LABEL)) {
				this.setLabelValue(myElement.getValue());
			}

			if (myElement.getQname().equals(EventXmlHandler.EVENT_XML_ELEMENT_CTIME)) {
		    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");

				try {
					this.setCreationTime(df.parse(myElement.getValue()));
				} catch (ParseException e) {
					e.printStackTrace();
					throw new TrackingMessageException("Error when parsing the XML string <"
							+ xmlString + ">", e);
				}
			}

			if (myElement.getQname().equals(EventXmlHandler.EVENT_XML_ELEMENT_ETIME)) {

				try {
					this.setElapsedTime(Integer.parseInt(myElement.getValue()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					throw new TrackingMessageException("Error when parsing the XML string <"
							+ xmlString + ">", e);
				}
			}

			if (myElement.getQname().equals(EventXmlHandler.EVENT_XML_ELEMENT_ORIGIN)) {
				this.setOrigin(myElement.getValue());
			}

			if (myElement.getQname().equals(EventXmlHandler.EVENT_XML_ELEMENT_TYPE)) {
				if (myElement.getValue().equals(TrackingMessage.EVENT_STR_TYPE_ARRIVAL_CTIME)) {
					this.setType(TrackingMessage.EVENT_INT_TYPE_ARRIVAL_CTIME);
				} else {
					if (myElement.getValue().equals(TrackingMessage.EVENT_STR_TYPE_ARRIVAL_ETIME)) {
						this.setType(TrackingMessage.EVENT_INT_TYPE_ARRIVAL_ETIME);
					}
				}
			}

		}

	}

}
