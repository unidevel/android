/*
 * Copyright 2014 IBM Corp. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.unidevel.whereareyou;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMObjectResult;
import com.ibm.mobile.services.data.IBMQuery;
import com.ibm.mobile.services.data.IBMQueryResult;

public class MainActivity extends Activity {

	List<Item> itemList;
	BlueListApplication blApplication;
	ArrayAdapter<Item> lvArrayAdapter;
	ActionMode mActionMode = null;
	int listItemPosition;
	public static final String CLASS_NAME="MainActivity";
	
	@Override
	/**
	 * onCreate called when main activity is created.
	 * 
	 * Sets up the itemList, application, and sets listeners.
	 *
	 * @param savedInstanceState
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*use application class to maintain global state*/
		blApplication = (BlueListApplication) getApplication();
		itemList = blApplication.getItemList();
		
		/*set up the array adapter for items list view*/
		ListView itemsLV = (ListView)findViewById(R.id.itemsList);
		lvArrayAdapter = new ArrayAdapter<Item>(this, R.layout.list_item_1, itemList);
		itemsLV.setAdapter(lvArrayAdapter);
		
		/*refresh the list*/
		listItems(); 

		/*set long click listener*/
		itemsLV.setOnItemLongClickListener(new OnItemLongClickListener() {
		    /* Called when the user long clicks on the textview in the list*/
		    public boolean onItemLongClick(AdapterView<?> adapter, View view, int position,
	                long rowId) {
		    	listItemPosition = position;
				if (mActionMode != null) {
		            return false;
		        }
		        /* Start the contextual action bar using the ActionMode.Callback*/
		        mActionMode = MainActivity.this.startActionMode(mActionModeCallback);
		        return true;
		    }
		});
		EditText itemToAdd = (EditText) findViewById(R.id.itemToAdd);
		/*set key listener for edittext (done key to accept item to list)*/
		itemToAdd.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    createItem(v);
                    return true;
                }
                return false;
            }
        });
	}

	/**
	 * Removes text on click of x button
	 *
	 * @param  v the edittext view.
	 */
	public void clearText(View v) {
		EditText itemToAdd = (EditText) findViewById(R.id.itemToAdd);
		itemToAdd.setText("");
	}
	
	/**
	 * Refreshes itemList from data service.
	 * 
	 * An IBMQuery is used to find all the list items
	 */
	public void listItems() {
		try {
			IBMQuery<Item> query = IBMQuery.queryForClass(Item.class);
			/**
			 * IBMQueryResult is used to receive array of objects from server.
			 * 
			 * onResult is called when it successfully retrieves the objects associated with the 
			 * query, and will reorder these items based on creation time.
			 * 
			 * onError is called when an error occurs during the query.
			 */
			query.findObjectsInBackground(new IBMQueryResult<Item>() {
				public void onResult(final List<Item> objects) {
					if (!isFinishing()) {
						runOnUiThread(new Runnable() {
							public void run() {
								//clear local itemList, as we'll be reordering & repopulating from DataService.
								itemList.clear();
								for(IBMDataObject item:objects) {
									itemList.add((Item) item);
								}
								sortItems(itemList);
								lvArrayAdapter.notifyDataSetChanged();
							}
						});
					}
				}
				public void onError(IBMDataException error) {
					Log.e(CLASS_NAME, "Exception : " + error.getMessage());
				}
			}); 
		}  catch (IBMDataException error) {
			Log.e(CLASS_NAME, "Exception : " + error.getMessage());
		}
	}
	
	/**
	 * on return from other activity, check result code to determine behavior
	 */
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
		switch (resultCode)
		{
		/*if an edit has been made, notify that the data set has changed.*/
		case BlueListApplication.EDIT_ACTIVITY_RC:
			sortItems(itemList);
			lvArrayAdapter.notifyDataSetChanged();
    		break;
		}
    }
	
	/**
	 * called on done and will add item to list.
	 *
	 * @param  v edittext View to get item from.
	 * @throws IBMDataException 
	 */
	public void createItem(View v) {
		EditText itemToAdd = (EditText) findViewById(R.id.itemToAdd);
		String toAdd = itemToAdd.getText().toString();
		Item item = new Item();
		if (!toAdd.equals("")) {
			item.setName(toAdd);
			/**
			 * IBMObjectResult is used to handle the response from the server after 
			 * either creating or saving an object.
			 * 
			 * onResult is called if the object was successfully saved
			 * onError is called if an error occurred saving the object 
			 */
			item.saveInBackground(new IBMObjectResult<Item>() {
				/**
				 * If the result succeeds, onResult gets called with the object that was created.
				 */
				public void onResult(Item object) {
					if (!isFinishing()) {
						listItems();
					}
				}
				/**
				 * If the result failed, onError is called with an exception that describes the error.
				 */
				public void onError(IBMDataException error) {
					Log.e(CLASS_NAME, "Exception : " + error.getMessage());
				}
			});
			//set text field back to empty after item added
			itemToAdd.setText("");
		}
	}
	
	/**
	 * will delete an item from the list
	 *
	 * @param  Item item to be deleted
	 */
	public void deleteItem(Item item) {
		itemList.remove(listItemPosition);
		//This will attempt to delete the item on the server
		item.deleteInBackground(new IBMObjectResult<Item>() {
			//Called if the object was successfully deleted
			public void onResult(Item item) {
				if (!isFinishing()) {
					runOnUiThread(new Runnable() {
						public void run() {
							lvArrayAdapter.notifyDataSetChanged();
						}
					});
				}
			}
			//Called if there was an error deleting the item
			public void onError(IBMDataException error) {
				Log.e(CLASS_NAME, "Exception : " + error.getMessage());
				//add error handling here.
			}
		});
		lvArrayAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Will call new activity for editing item on list
	 * @parm String name - name of the item.
	 */
	public void updateItem(String name) {
		Intent editIntent = new Intent(getBaseContext(), EditActivity.class);
    	editIntent.putExtra("ItemText", name);
    	editIntent.putExtra("ItemLocation", listItemPosition);
    	startActivityForResult(editIntent, BlueListApplication.EDIT_ACTIVITY_RC);
	}
	
	/**
	 * sort a list of Items
	 * @param List<Item> theList
	 */
	private void sortItems(List<Item> theList) {
		//sort collection by case insensitive alphabetical order
		Collections.sort(theList, new Comparator<Item>() {
			public int compare(Item lhs,
					Item rhs) {
				String lhsName = lhs.getName();
				String rhsName = rhs.getName();
				return lhsName.compareToIgnoreCase(rhsName);
			}
		});
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        /* Inflate a menu resource with context menu items*/
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.editaction, menu);
	        return true;
	    }

	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false;
	    }

		/**
		 * called when user clicks on contextual action bar menu item
		 * 
		 * Determined which item was clicked, and then determine behavior appropriately
		 *
		 * @param ActionMode mode and MenuItem item clicked
		 */
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    	Item lItem = itemList.get(listItemPosition);
	    	/*switch dependent on which action item was clicked*/
	    	switch (item.getItemId()) {
	    		/*on edit, get all info needed & send to new, edit activity.*/
	            case R.id.action_edit:
	            	updateItem(lItem.getName());
	                mode.finish(); /* Action picked, so close the CAB*/
	                return true;
	            /*on delete, remove list item & update.*/
	            case R.id.action_delete:
	            	deleteItem(lItem);
	                mode.finish(); /* Action picked, so close the CAB*/
	            default:
	                return false;
	        }
	    }

	    /* Called on exit of action mode*/
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};
}
