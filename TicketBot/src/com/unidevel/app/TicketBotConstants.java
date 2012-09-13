package com.unidevel.app;

public interface TicketBotConstants {
	String VERSION = "1";
	String TAG_TICKETS = "tickets";
	String TAG_TICKET = "ticket";
	String FMT_DATE = "yyyy-MM-dd";
	String KEY_NAME = "name";
	String KEY_ID = "id";
	String KEY_VERSION = "version";
	String KEY_TICKETS = "tickets";
	String KEY_TICKET_ID = "ticket";
	String KEY_DATE = "date";
	String KEY_PHONE = "phone";
	String KEY_WHERE = "where";
	String KEY_RESULT = "result";
	
	String EXTRA_INFO = "info";
	
	int DATE_TOMORROW = 0;
	int DATE_THIS_SATURDAY = 1;
	int DATE_THIS_SUNDAY = 2;
	
	int MAX_TICKETS = 0;
	
	String METHOD_GET = "GET";
	String METHOD_POST = "POST";
}
