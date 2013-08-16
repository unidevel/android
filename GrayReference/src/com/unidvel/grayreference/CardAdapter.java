package com.unidvel.grayreference;

import java.util.ArrayList;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.jess.ui.TwoWayGridView;

public class CardAdapter extends BaseAdapter implements ListAdapter
{
	TwoWayGridView view;
	List<Card> cards;
	
	LayoutInflater inflater;
	class ViewHolder
	{
		ImageView icon;
		TextView label;
	}
	
	public CardAdapter(TwoWayGridView view)
	{
		this.view = view;
		this.cards = new ArrayList<Card>();
		this.inflater = LayoutInflater.from( this.view.getContext() );
		createDefaultCards();
	}
	
	public void createDefaultCards()
	{
		Card card = new Card("Add", -1);
		this.cards.add( card );
		card = new Card("All", -1);
		this.cards.add( card );
		card = new Card("Dark gray", 63);
		this.cards.add( card );
		card = new Card("Gray", 127);
		this.cards.add( card );
		card = new Card("Light gray", 195);
		this.cards.add( card );
		card = new Card("White", 255);
		this.cards.add( card );
//		this.view.setNumColumns( cards.size() );
		this.view.setNumRows( 1 );
	}
	
	@Override
	public int getCount()
	{
		return cards.size();
	}
	
	public void addItem(String name, int level)
	{
		this.cards.add( new Card(name, level) );
//		this.view.setNumColumns( this.cards.size() );
//		this.view.setNumRows( 1 );
		this.notifyDataSetChanged();
	}

	@Override
	public Object getItem( int position )
	{
		return this.cards.get( position );
	}

	@Override
	public long getItemId( int position )
	{
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		ViewHolder holder;
		if ( convertView == null )
		{
			convertView = this.inflater.inflate( R.layout.card, null );
			holder = new ViewHolder();
			convertView.setTag( holder );
			holder.label = (TextView)convertView.findViewById( R.id.label );
			holder.icon = (ImageView)convertView.findViewById( R.id.icon );
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		Card card = this.cards.get( position );
		holder.label.setText(card.getName());
		if ( position >= 2 )
			holder.icon.setBackgroundColor( card.getColor() );
		else
			holder.icon.setImageResource( R.drawable.ic_launcher);
		return convertView;
	}

	

}
