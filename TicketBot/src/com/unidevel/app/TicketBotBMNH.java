package com.unidevel.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TicketBotBMNH extends Activity implements OnClickListener,
		TicketBotConstants, OnDismissListener, OnCancelListener {
	TicketBotUser user;
	Handler handler;
	ProgressDialog progressDialog;
	Thread bookThread;
	SiteBMNH site;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticket_bmnh);
		handler = new Handler();
		try {
			user = TicketBotUser.load(this);
		} catch (Throwable e) {
			user = new TicketBotUser();
		}

		{
			int tickets = user.wantTickets;
			if (tickets > 3 || tickets < 1)
				tickets = 3;
			Spinner view = (Spinner) findViewById(R.id.tickets);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, new String[] { "1",
							"2", "3" });
			adapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			view.setAdapter(adapter);
			view.setSelection(tickets - 1);
		}
		{
			TextView view = (TextView) findViewById(R.id.name);
			view.setText(user.name);
		}
		{
			TextView view = (TextView) findViewById(R.id.id);
			view.setText(user.id);
		}
		{
			DatePicker view = (DatePicker) findViewById(R.id.date);
			Date date = getDate(new Date(), user.wantDate);
			view.updateDate(date.getYear()+1900, date.getMonth(), date.getDate());
		}

		Button button = (Button) findViewById(R.id.submit);
		button.setOnClickListener(this);
	}

	private Date getDate(Date now, int wantDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		if (DATE_THIS_SUNDAY == wantDate) {
			if ( cal.getFirstDayOfWeek() == Calendar.SUNDAY ) {
				cal.add(Calendar.DAY_OF_WEEK, 7);
			}
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		} else if (DATE_THIS_SATURDAY == wantDate) {
			if ( cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ) {
				cal.add(Calendar.DAY_OF_WEEK, 7);
			}
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		} else {
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return cal.getTime();
	}

	public class SiteBMNH extends TicketSite implements Runnable {
		String name;
		String id;
		String date;
		int tickets;
		String ticketNo;

		public SiteBMNH(String name, String id, Date date, int tickets) {
			SimpleDateFormat sdf = new SimpleDateFormat(FMT_DATE);
			this.date = sdf.format(date);
			this.id = id;
			this.tickets = tickets;
			this.name = name;
		}

		@Override
		protected String getBody() {
			StringBuffer buf = new StringBuffer();
			buf.append("name=").append(encode(name)).append("&certificate=")
					.append(encode("身份证")).append("&certificateno=").append(
							encode(id)).append("&bookDate=").append(date)
					.append("&bookTicketNum=").append(tickets).append(
							"&bookType=%B8%F6%C8%CB");
			return buf.toString();
		}

		@Override
		protected String getEncoding() {
			return "GBK";
		}

		@Override
		protected Map<String, String> getHeaders() {
			Map<String, String> headers = new LinkedHashMap<String, String>();
			headers.put("Host", "www.bmnh.org.cn");
			headers
					.put(
							"User-Agent",
							"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 ( .NET CLR 3.5.30729)");
			headers.put("Accept",
					"text/html,application/xhtml+xml,application/xml");
			headers.put("Content-Type", "application/x-www-form-urlencoded");
			headers.put("Content-Length", "");
			return headers;
		}

		@Override
		protected String getMethod() {
			return "POST";
		}

		@Override
		protected String getURL() {
			return "http://211.103.239.83:8090/tb/app/add.jsp";
		}

		public String getTicket() {
			return ticketNo;
		}

		public void showError(final String msg){
			handler.post(new Runnable(){
				public void run() {
					Toast.makeText(TicketBotBMNH.this, msg, 3).show();
				}
			});
		}
		
		public void showError(final int msgId){
			handler.post(new Runnable(){
				public void run() {
					Toast.makeText(TicketBotBMNH.this, getString(msgId), 3).show();
				}
			});
		}
		
		public void run() {
			ticketNo = null;
			try {
				String response = submit();
				if (response.indexOf("预约成功") > 0) {
					int pos = response.indexOf("预约编号");
					int posStart = response.indexOf("<td", pos);
					int posEnd = response.indexOf("</td>", posStart);
					if (pos < 0 || posStart < 0 || posEnd < 0) {
						showError(R.string.error_get_ticket);
						return;
					} else {
						String s = response.substring(posStart, posEnd);
						s = s.replaceAll("<[^>]+>", "");
						Pattern pattern = Pattern.compile("(\\d+)");
						Matcher m = pattern.matcher(s);
						if (m.find()) {
							ticketNo = m.group(1);
							return;
						}
						showError(R.string.error_get_ticket);
						return;
					}
				} else {
					int pos = response.indexOf("<p>");
					int pos2 = response.indexOf('<', pos + 1);
					if (pos < 0 || pos2 < 0) {
						showError(R.string.error_get_ticket);
						return;
					}
					showError(response.substring(pos + 3, pos2).trim());
					return;
				}
			} catch (Throwable ex) {
				showError(ex.getLocalizedMessage());
				Log.e("TicketBotBMNH", ex.getMessage(), ex);
			} finally{
				handler.post(new Runnable(){
					public void run() {
						if ( progressDialog != null ) {
							progressDialog.dismiss();
							progressDialog = null;
						}
					}
				});
			}
		}
	}

	public void onClick(View v) {
		progressDialog = ProgressDialog.show(this, getString(R.string.title_book_ticket), getString(R.string.site_bmnh));
		progressDialog.setCancelable(true);
		progressDialog.setOnDismissListener(this);
		progressDialog.setOnCancelListener(this);
		String name = ((TextView)findViewById(R.id.name)).getText().toString();
		String id = ((TextView)findViewById(R.id.id)).getText().toString();
		int tickets = Integer.valueOf(((Spinner)findViewById(R.id.tickets)).getSelectedItem().toString());
		DatePicker view = (DatePicker)findViewById(R.id.date);
		Date date = new Date(view.getYear()-1900, view.getMonth(), view.getDayOfMonth());
		site = new SiteBMNH(name, id, date, tickets);
		bookThread = new Thread(site);
		bookThread.start();
	}


	public void onDismiss(DialogInterface dialog) {
		try {
			bookThread.join();
		}
		catch(Throwable ex){}
		bookThread = null;
		if ( site == null || site.getTicket() == null ) return;
		
		Intent intent = new Intent();
		
		String name = ((TextView)findViewById(R.id.name)).getText().toString();
		String id = ((TextView)findViewById(R.id.id)).getText().toString();
		int tickets = Integer.valueOf(((Spinner)findViewById(R.id.tickets)).getSelectedItem().toString());
		DatePicker view = (DatePicker)findViewById(R.id.date);
		Date date = new Date(view.getYear()-1900, view.getMonth(), view.getDayOfMonth());
		SimpleDateFormat sdf = new SimpleDateFormat(FMT_DATE);
		intent.putExtra(KEY_TICKET_ID, site.getTicket());
		intent.putExtra(KEY_NAME, name);
		intent.putExtra(KEY_DATE, sdf.format(date));
		intent.putExtra(KEY_TICKETS, tickets);
		intent.putExtra(KEY_ID, id);
		intent.putExtra(KEY_WHERE, getString(R.string.site_bmnh));
		this.setResult(0, intent);
		Toast.makeText(this, getString(R.string.msg_success), 3);
		this.finish();
	}

	public void onCancel(DialogInterface dialog) {
		try {
			bookThread.stop();
		}
		catch(Throwable ex){}
		bookThread = null;
		Toast.makeText(this, getString(R.string.msg_terminate_ticket), 2).show();
	}
}
