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
    public static boolean bHasResult[] = new boolean[7];
    public static void ClearResult() {
    	for(int i=0; i < 7; i++) {
    		bHasResult[i]=false;
    		ResultCPU_act[i]=null;
    		ResultCPU_iow[i]=null;
    		ResultCPU_idl[i]=null;
    		ResultCS_tot[i]=null;
    		ResultCS_vol[i]=null;
    		ResultThrp[i]=null;
    		ResultExpName[i]=null;
    	}
    }
    
    public static String ResultCPU_act[] = new String[7];
    public static String ResultCPU_iow[] = new String[7];
    public static String ResultCPU_idl[] = new String[7];
    public static String ResultCS_tot[] = new String[7];
    public static String ResultCS_vol[] = new String[7];
    public static String ResultThrp[] = new String[7];
    public static String ResultExpName[] = new String[7];
      
    
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
	        
	        int resID[] = new int[7];
	        resID[0]=R.drawable.icon_sw;
	        resID[1]=R.drawable.icon_sr;
	        resID[2]=R.drawable.icon_rw;
	        resID[3]=R.drawable.icon_rr;
	        resID[4]=R.drawable.icon_insert;
	        resID[5]=R.drawable.icon_update;
	        resID[6]=R.drawable.icon_delete;
	        
	        
	        for(int idx = 0; idx < 7; idx++) {
		        if(bHasResult[idx]) {
		        	adapter.addItem(new IconTextItem(res.getDrawable(resID[idx]), ResultCPU_act[idx], ResultCPU_iow[idx], ResultCPU_idl[idx], 
		        			ResultCS_tot[idx], ResultCS_vol[idx], ResultThrp[idx], ResultExpName[idx]));
		        }
	        }
	        
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
