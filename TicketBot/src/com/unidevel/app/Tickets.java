package com.unidevel.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

public class Tickets implements TicketBotConstants {
	public class Ticket {
		String name;
		String where;
		String ticketId;
		Date date;
		int tickets;
	}

	List<Ticket> ticketList = new ArrayList<Ticket>();

	public void save(Context context) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		FileWriter writer = new FileWriter(getFile(context));
		XmlSerializer serializer = Xml.newSerializer();
		try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", TAG_TICKETS);
	        serializer.attribute("", KEY_VERSION, VERSION);
	        for (Ticket ticket : ticketList) {
	            serializer.startTag("", TAG_TICKET);
	            serializer.attribute("", KEY_NAME, ticket.name);
	            serializer.attribute("", KEY_WHERE, ticket.where);
	            serializer.attribute("", KEY_DATE, sdf.format(ticket.date));
	            serializer.attribute("", KEY_TICKET_ID, ticket.ticketId);
	            serializer.attribute("", KEY_TICKETS, String.valueOf(ticket.tickets));
	            serializer.endTag("", TAG_TICKET);
	        }
	        serializer.endTag("", TAG_TICKETS);
	        serializer.endDocument();
	    } finally {
			try { writer.close(); } catch(Throwable ex){}
		}
	}

	public void load(Context context) throws ParserConfigurationException, FactoryConfigurationError, SAXException, IOException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(getFile(context));
		Element root = doc.getDocumentElement();
		NodeList nodes = root.getElementsByTagName(TAG_TICKET);
		ticketList = new ArrayList<Ticket>();
		for ( int i = 0; i < nodes.getLength(); ++ i ) {
			Element item = (Element)nodes.item(i);
			Ticket ticket = new Ticket();
			ticket.name = item.getAttribute(KEY_NAME);
			ticket.where = item.getAttribute(KEY_WHERE);
			ticket.ticketId = item.getAttribute(KEY_TICKET_ID);
			ticket.date = sdf.parse(item.getAttribute(KEY_DATE));
			ticket.tickets = toInt(item.getAttribute(KEY_TICKETS));
			ticketList.add(ticket);
		}
	}
	
	private static int toInt(String val){
		try{
			return Integer.valueOf(val);
		}
		catch(Throwable ex){}
		return 0;
	}
	
	private static File getFile(Context context) {
		String name = "TicketBot";
		File dir;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			dir = new File(Environment.getExternalStorageDirectory(), name);
		} else {
			dir = context.getFilesDir();
		}
		if (!dir.exists())
			dir.mkdirs();
		return new File(dir, "tickets.xml");
	}
	
	public void clear(Date now) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		now = cal.getTime();
		for (int i = ticketList.size()-1; i>=0; --i ) {
			Ticket ticket = ticketList.get(i);
			if ( ticket.date.before(now) ) ticketList.remove(i);
		}
	}
	
	public void delete(Ticket ticket){
		ticketList.remove(ticket);
	}

	public void add(String ticketId, String name, String where, Date date, int tickets) {
		Ticket ticket = new Ticket();
		ticket.ticketId = ticketId;
		ticket.name = name;
		ticket.where = where;
		ticket.date = date;
		ticket.tickets = tickets;
		int i;
		for ( i = 0; i < ticketList.size(); ++ i ) {
			if ( ticket.date.before(ticketList.get(i).date) ) break;
		}
		ticketList.add(ticket);
	}
	
	public int count(){
		return ticketList.size();
	}

	public List<Ticket> getTickets(){
		return ticketList;
	}
}
