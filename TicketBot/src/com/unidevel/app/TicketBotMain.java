package com.unidevel.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.unidevel.app.Tickets.Ticket;

public class TicketBotMain extends Activity implements OnMenuItemClickListener,
		TicketBotConstants, OnClickListener {
	Site[] sites = null;
	Tickets tickets = null;

	class Site {
		String name;
		String url;
		Class<?> activity;

		public Site(String name, String url, Class<?> activity) {
			this.name = name;
			this.url = url;
			this.activity = activity;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticket_main);
		tickets = new Tickets();
		try {
			tickets.load(this);
		} catch (Throwable e) {
			Log.e("TicketBotMain", e.getMessage(), e);
		}

		refreshTickets();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ticket_main, menu);

		// Map<String, Site[]> siteMap = new LinkedHashMap<String, Site[]>();
		sites = new Site[] { new Site(getString(R.string.site_bmnh),
				"http://www.bmnh.org.cn/", TicketBotBMNH.class) };
		// siteMap.put(getString(R.string.city_beijing), sites);

		MenuItem item;
		item = menu.findItem(R.id.menu_about);
		item.setOnMenuItemClickListener(this);

		item = menu.findItem(R.id.menu_exit);
		item.setOnMenuItemClickListener(this);

		item = menu.findItem(R.id.menu_clear);
		item.setOnMenuItemClickListener(this);

		item = menu.findItem(R.id.menu_setting);
		item.setOnMenuItemClickListener(this);

		try {
			item = menu.findItem(R.id.menu_book);
			BookHandle handle = new BookHandle();
			for (int i = 0; i < sites.length; ++i) {
				Site site = sites[i];
				MenuItem subItem = item.getSubMenu().add(0, i, 0, site.name);
				subItem.setOnMenuItemClickListener(handle);
			}
		} catch (Throwable ex) {
			Toast.makeText(this, ex.getClass().getName() + ":"
					+ ex.getMessage(), 3);
		}
		return true;
	}

	class BookHandle implements OnMenuItemClickListener {

		public boolean onMenuItemClick(MenuItem item) {
			int index = item.getItemId();
			Site site = sites[index];
			Intent intent = new Intent(TicketBotMain.this, site.activity);
			startActivityForResult(intent, 0);
			return true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null)
			return;
		{
			String name = data.getStringExtra(KEY_NAME);
			String date = data.getStringExtra(KEY_DATE);
			int tickets = data.getIntExtra(KEY_TICKETS, 0);
			String where = data.getStringExtra(KEY_WHERE);
			SimpleDateFormat sdf = new SimpleDateFormat(FMT_DATE);
//			sdf.setTimeZone(TimeZone.getDefault());
			String ticketId = data.getStringExtra(KEY_TICKET_ID);
			try {
				this.tickets.add(ticketId, name, where, sdf.parse(date), tickets);
			} catch (ParseException e) {
				Log.e("TicketBotMain", e.getMessage(), e);
			}
			saveTickets();
		}
	}

	public boolean onMenuItemClick(MenuItem item) {
		if (item.getItemId() == R.id.menu_exit) {
			this.finish();
		} else if (item.getItemId() == R.id.menu_about) {
			Intent intent = new Intent(this, TicketBotAbout.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.menu_clear) {
			tickets.clear(new Date());
			refreshTickets();
			saveTickets();
		} else if (item.getItemId() == R.id.menu_setting) {
			Intent intent = new Intent();
			intent.setClass(this, TicketBotSettings.class);
			startActivity(intent);
		} else if ( item.getItemId() == R.id.menu_add_calendar ) {
			if ( selectedTicket == null ) return true;
			try {
				Calendar cal = Calendar.getInstance();
				Date date = selectedTicket.date;
				cal.setTime(date);
				cal.set(Calendar.HOUR, 8);
				
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra("beginTime", cal.getTimeInMillis());
				intent.putExtra("allDay", true);
//				intent.putExtra("rrule", "FREQ=YEARLY");
				intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);				
				intent.putExtra("title", String.format("%s, %d %s, %s", selectedTicket.ticketId,  selectedTicket.tickets, getString(R.string.msg_tickets), selectedTicket.where));
				startActivity(intent);
			}
			catch(Throwable ex){
				Toast.makeText(this, ex.getLocalizedMessage(), 3).show();
				Log.e("Add Calendar", ex.getMessage(), ex);
			}
			finally {
				selectedTicket = null;
			}
		} else if ( item.getItemId() == R.id.menu_delete ) {
			if ( selectedTicket == null ) return true;
			tickets.delete(selectedTicket);
			selectedTicket = null;
			refreshTickets();
		} else if ( item.getItemId() == R.id.menu_send_sms ) {
			if ( selectedTicket == null ) return true;
			try {
				String body = String.format("%tF, %s %s, %s %s, %s, %s", 
						selectedTicket.date, getString(R.string.msg_ticket_id), selectedTicket.ticketId, 
						selectedTicket.tickets, getString(R.string.msg_tickets),
						selectedTicket.where, selectedTicket.name);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setType("vnd.android-dir/mms-sms");
				intent.putExtra("sms_body", body);
				startActivity(intent);
			} catch(Throwable ex){
				Toast.makeText(this, ex.getLocalizedMessage(), 3).show();
				Log.e("Send SMS", ex.getMessage(), ex);
			}
			finally {
				selectedTicket = null;
			}
			selectedTicket = null;
		}
		return true;
	}

	private void saveTickets(){
		try {
			this.tickets.save(this);
			refreshTickets();
		} catch (Throwable e) {
			Toast.makeText(this, getString(R.string.error_save_ticket), 2)
					.show();
			Log.e("TicketBotMain", e.getMessage(), e);
		}
	}
	
	public void refreshTickets() {
		ListView list = (ListView) findViewById(R.id.ticket_list);
		TicketAdapter adapter = new TicketAdapter();
		list.setAdapter(adapter);
		list.setOnCreateContextMenuListener(this);
	}

	class TicketAdapter extends BaseAdapter {
		LayoutInflater inflater = (LayoutInflater) TicketBotMain.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		SimpleDateFormat formatter = new SimpleDateFormat(FMT_DATE);
		public int getCount() {
			return tickets.count();
		}

		public Object getItem(int index) {
			return tickets.getTickets().get(index);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = inflater.inflate(R.layout.ticket_item, null);
			}
			Ticket ticket = tickets.getTickets().get(position);
			String info1 = String.format("%tF - %s", ticket.date, ticket.where);
			String info2 = String.format("%s:%s, %d %s, %s", getString(R.string.msg_ticket_id), ticket.ticketId, ticket.tickets, getString(R.string.msg_tickets), ticket.name);
			{
				TextView child = (TextView) v.findViewById(R.id.ticket_info1);
				child.setText(info1);
				child.setTag(ticket);
				child.setOnCreateContextMenuListener(TicketBotMain.this);
				child.setOnClickListener(TicketBotMain.this);
			}
			{
				TextView child = (TextView) v.findViewById(R.id.ticket_info2);
				child.setText(info2);
				child.setTag(ticket);
				child.setOnCreateContextMenuListener(TicketBotMain.this);
				child.setOnClickListener(TicketBotMain.this);
			}
			return v;
		}
	}
	
	Ticket selectedTicket;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
//		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.ticket_info1 || v.getId() == R.id.ticket_info2) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.ticket_list_menu, menu);
			for ( int i = 0; i < menu.size(); ++i ) {
				menu.getItem(i).setOnMenuItemClickListener(this);
			}
			selectedTicket = (Ticket)v.getTag();
		}
		else super.onCreateContextMenu(menu, v, menuInfo);
	}

	public void onClick(View view) {
		Ticket ticket = (Ticket)view.getTag();
		Intent intent = new Intent(this, TicketBotDetail.class);
		
		String msg = String.format("%s\n%tF\n%s %s\n%d %s\n%s", ticket.where,
				ticket.date, getString(R.string.msg_ticket_id), ticket.ticketId,
				ticket.tickets, getString(R.string.msg_tickets), ticket.name);
		intent.putExtra(EXTRA_INFO, msg);
		startActivity(intent);
	}
}