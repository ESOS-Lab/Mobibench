package esos.ResultListControl;

import esos.MobiBench.R;
import esos.MobiBench.R.layout;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import android.view.View;


public class DialogActivity extends Activity{
    DataListView list;
    IconTextListAdapter adapter;
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	 
	        // window feature for no title - must be set prior to calling setContentView.
	        //requestWindowFeature(Window.FEATURE_NO_TITLE);
	 
	        // create a DataGridView instance
	        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
	        list = new DataListView(this);
	 
	        // create an DataAdapter and a MTable
	        adapter = new IconTextListAdapter(this);
	 
	        // add items
	        Resources res = getResources();
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon_sw), "21", "11", "12","1000","500","30","Seq. write"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon_sr),"20", "10", "11","999","499","29", "Seq. read"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon_rw), "14", "15", "16","929","495","21","Ran. write"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon_rr),"14", "15", "16","929","495","21", "Ran. read"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon_insert), "14", "15", "16","929","495","21","SQLite. insert"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon_delete), "14", "15", "16","929","495","21","SQLite. delete"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon_update),"14", "15", "16","929","495","21", "SQLite. update"));
	        
	        // call setAdapter()
	        list.setAdapter(adapter);
	 
	 
	        // use adapter.notifyDataSetChanged() to apply changes after adding items dynamically
	        // adapter.notifyDataSetChanged();
	 
	 
	        // set listener
	        list.setOnDataSelectionListener(new OnDataSelectionListener() {
	            public void onDataSelected(AdapterView parent, View v, int position, long id) {
	                IconTextItem curItem = (IconTextItem) adapter.getItem(position);
	                String[] curData = curItem.getData();

	                Toast.makeText(getApplicationContext(), "Selected : " + curData[6], 2000).show();
	            }
	        });
	 
	 
	        // display as the main layout
	        setContentView(list, params);
	    }
}
