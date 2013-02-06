package esos.ResultListControl;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import esos.MobiBench.R;
import esos.MobiBench.TabMain;
import esos.Database.*;
import esos.MobiBench.R.layout;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;


public class DialogActivity extends Activity{
    DataListView list;
    IconTextListAdapter adapter;
	private SharedPreferences db_prefs = null;
	private SharedPreferences.Editor pref_editor = null;   
    private static int db_index = 0;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd. HH:mm:ss");
    private static final String TAG_DA="datedebug";
    private static NotesDbAdapter db;
    public static int bHasResult[] = new int[7];
    private static final String DEBUG_TAG="dialogactivity";
    public static void ClearResult(NotesDbAdapter database) {
    	db = database;
    	for(int i=0; i < 7; i++) {
    		bHasResult[i]=0;
    		ResultCPU_act[i]=null;
    		ResultCPU_iow[i]=null;
    		ResultCPU_idl[i]=null;
    		ResultCS_tot[i]=null;
    		ResultCS_vol[i]=null;
    		ResultThrp[i]=null;
    		ResultExpName[i]=null;
    		ResultType[i] = null;
    	}
    }
    
    
    public static int index_db;
    public static String ResultCPU_act[] = new String[7];
    public static String ResultCPU_iow[] = new String[7];
    public static String ResultCPU_idl[] = new String[7];
    public static String ResultCS_tot[] = new String[7];
    public static String ResultCS_vol[] = new String[7];
    public static String ResultThrp[] = new String[7];
    public static String ResultExpName[] = new String[7];
    public static String ResultType[] = new String[7];
	public static int check_using_db = 0;
    
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
			db_prefs = getSharedPreferences("Setting", MODE_PRIVATE);

			pref_editor = db_prefs.edit();
			
	        // window feature for no title - must be set prior to calling setContentView.
	        //requestWindowFeature(Window.FEATURE_NO_TITLE);
	 
	        // create a DataGridView instance
	        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
	        list = new DataListView(this);
	 
	        // create an DataAdapter and a MTable
	        adapter = new IconTextListAdapter(this);
	 
	        // add items
	        Resources res = getResources();
	        
	        int resID[] = new int[7];
	        resID[0]=R.drawable.icon_sw;
	        resID[1]=R.drawable.icon_sr;
	        resID[2]=R.drawable.icon_rw;
	        resID[3]=R.drawable.icon_rr;
	        resID[4]=R.drawable.icon_insert;
	        resID[5]=R.drawable.icon_update;
	        resID[6]=R.drawable.icon_delete;
	                
	    	String tmp_str_date = dateFormat.format(calendar.getTime()); // for data base date
	    	db_index = db_prefs.getInt("database_index", 0); // data base indexing
	    	
	        for(int idx = 0; idx < 7; idx++) {
		        if(bHasResult[idx] != 0) {
		        	adapter.addItem(new IconTextItem(res.getDrawable(resID[idx]), ResultCPU_act[idx], ResultCPU_iow[idx], ResultCPU_idl[idx], 
		        			ResultCS_tot[idx], ResultCS_vol[idx], ResultThrp[idx], ResultExpName[idx]));
		        	Log.d(DEBUG_TAG, "addItem : idx/expname " + idx + " " + ResultExpName[idx]);	
		        	if(check_using_db == 1){
		        		Log.d(DEBUG_TAG, "addItem / checkusing is 1 : idx/expname " + idx + " " + ResultExpName[idx]);	
			        	db.insert_DB(db_index, tmp_str_date, ResultType[idx], 1, ResultCPU_act[idx], ResultCPU_iow[idx], ResultCPU_idl[idx],
			        			ResultCS_tot[idx], ResultCS_vol[idx],  ResultThrp[idx], ResultExpName[idx]);
		        	}
     	
		        }
	        }
        	if(check_using_db == 1){
        		db_index++;
    	        pref_editor.putInt("database_index", db_index);
            	pref_editor.commit();
        	}

	        //db.insert_DB(32, "3011", true, "30", "40", "30", "1000", "700", "2000", "seq write");

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


