package com.unidevel.SMSAssist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SMSAdapter extends BaseAdapter{
	List<SMS> items;
	List<SMS> filteredItems;
	Context context;
	LayoutInflater inflater;
	SimpleDateFormat format;
	public SMSAdapter(List<SMS> items, Context context){
		this.items = items;
		this.filteredItems = items;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.format = new SimpleDateFormat("MM/dd hh:mm");
	}
	
	public int getCount() {
		return filteredItems.size();
	}

	public Object getItem(int index) {
		return filteredItems.get(index);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null){
			convertView = inflater.inflate(R.layout.sms_item, null);
			holder = new ViewHolder();
			holder.address = (TextView)convertView.findViewById(R.id.address);
			holder.date = (TextView)convertView.findViewById(R.id.date);
			holder.body = (TextView)convertView.findViewById(R.id.body); 
			holder.select = (CheckBox)convertView.findViewById(R.id.select);
			holder.select.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					CheckBox checkBox = (CheckBox)view;
					int position = (Integer)view.getTag();
					SMS sms = (SMS)getItem(position);
					sms.setSelected(checkBox.isChecked());
				}
			});
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		SMS sms = (SMS)getItem(position);
		holder.address.setText(sms.address);
		holder.date.setText(format.format(new Date(sms.date)));
		holder.body.setText(sms.body);
		holder.select.setChecked(sms.isSelected());
		holder.select.setTag(position);
		return convertView;
	}

	
	class ViewHolder {
		TextView address;
		TextView date;
		TextView body;
		CheckBox select;
	}
	
	public void selectNumberStarts(String filter){
		this.filteredItems = new ArrayList<SMS>();
		for ( SMS sms: items) {
			String address = sms.address;
			address.replace("+", "");
			if ( sms.address.startsWith(filter) ) {
				filteredItems.add(sms);
				continue;
			}
		}
		this.notifyDataSetInvalidated();
	}
	
	public void selectNumberEnds(String filter){
		this.filteredItems = new ArrayList<SMS>();
		for ( SMS sms: items) {
			String address = sms.address;
			if ( address.endsWith(filter) ) {
				filteredItems.add(sms);
				continue;
			}
		}
		this.notifyDataSetInvalidated();
	}

	public void selectNumberContains(String filter){
		this.filteredItems = new ArrayList<SMS>();
		for ( SMS sms: items) {
			if ( sms.address.contains(filter) ) {
				filteredItems.add(sms);
				continue;
			}
		}
		this.notifyDataSetInvalidated();
	}

	public void selectByFullTextSearch(String filter){
		this.filteredItems = new ArrayList<SMS>();
		for ( SMS sms: items) {
			if ( sms.address.contains(filter) ) {
				filteredItems.add(sms);
			}
			else if ( sms.body.contains(filter) ) {
				filteredItems.add(sms);
			}
		}
		this.notifyDataSetInvalidated();
	}

	public void selectAll(boolean selected){
		for (SMS sms: filteredItems ) {
			sms.setSelected(selected);
		}
		this.notifyDataSetChanged();
		return;
	}
	
	public List<SMS> getSelected(){
		List<SMS> selected  = new ArrayList<SMS>();
		for (SMS sms: filteredItems){
			if ( sms.isSelected() ) {
				selected.add(sms);
			}
		}
		return selected;
	}
	
	public void selectByThread(int thread){
		for (SMS sms: filteredItems){
			if (sms.threadId==thread){
				sms.setSelected(true);
			}
			else sms.setSelected(false);
		}
		this.notifyDataSetChanged();
	}
	
	public void selectByAddress(String address){
		for (SMS sms: filteredItems){
			if (address.equals(sms.address)){
				sms.setSelected(true);
			}
			else sms.setSelected(false);
		}
		this.notifyDataSetInvalidated();
	}
	
	public void selectByDate(long date){
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		Date d = new Date(date);
		start.set(d.getYear()+1900, d.getMonth(), d.getDate(), 0, 0, 0);
		end.set(d.getYear()+1900, d.getMonth(), d.getDate());
		end.add(Calendar.DATE, 1);
		selectByTime(start, end);
	}
	
	public void selectByMonth(long date){
		Calendar start = Calendar.getInstance();
		Date d = new Date(date);
		start.set(d.getYear()+1900, d.getMonth(), 1, 0, 0, 0);
		Calendar end = (Calendar)start.clone();
		end.add(Calendar.MONTH,1);
		selectByTime(start, end);
	}
	
	public void selectByYear(long date){
		Calendar start = Calendar.getInstance();
		Date d = new Date(date);
		start.set(d.getYear()+1900, 0, 0, 0, 0, 0);
		Calendar end = (Calendar)start.clone();
		end.add(Calendar.YEAR, 1);
		selectByTime(start, end);				
	}
	
	public void selectBefore(long date){
		Calendar start = Calendar.getInstance();
		start.set(0,0,0,0,0,0);
		Calendar end = Calendar.getInstance();
		Date d = new Date(date);
		end.setTime(d);
		selectByTime(start, end);
	}

	public void selectAfter(long date){
		Calendar start = Calendar.getInstance();
		Date d = new Date(date);
		start.setTime(d);
		Calendar end = Calendar.getInstance();
		end.add(Calendar.YEAR, 1);
		selectByTime(start, end);
	}

	private void selectByTime(Calendar start, Calendar end){
		for (SMS sms: filteredItems){
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(sms.date));
			if ( cal.before(start) || cal.after(end) ){
				sms.setSelected(false);
				continue;
			}
			sms.setSelected(true);
		}
		this.notifyDataSetChanged();		
	}
	
	public void selectExcept(int id){
		for (SMS sms: filteredItems){
			if ( sms.id == id ){
				sms.setSelected(false);				
			}
			else {
				sms.setSelected(true);
			}
		}
		this.notifyDataSetChanged();		
	}
	
	public void invertSelection(){
		for (SMS sms: filteredItems){
			sms.setSelected(!sms.isSelected());				
		}
		this.notifyDataSetChanged();		
	}
	
	public void showSelected(){
		filteredItems = getSelected();
		this.notifyDataSetChanged();
	}
	
	public void showAll(){
		filteredItems = items;
		this.notifyDataSetChanged();
	}
	
	public void deleteSelected(){
		List<SMS> items = getSelected();
		filteredItems.removeAll(items);
		items.removeAll(items);
		notifyDataSetChanged();
	}
}
