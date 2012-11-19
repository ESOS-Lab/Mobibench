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
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	 
	        // create a DataGridView instance
	        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
	        list = new DataListView(this);
	 
	        // create an DataAdapter and a MTable
	        adapter = new IconTextListAdapter(this);
	 
	        // add items
	        Resources res = getResources();
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon05), "추억의 테트리스", "30,000 다운로드", "900 원"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon05), "고스톱 - 강호동 버전", "26,000 다운로드", "1500 원"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon05), "친구찾기 (Friends Seeker)", "300,000 다운로드", "900 원"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon05), "강좌 검색", "120,000 다운로드", "900 원"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon05), "지하철 노선도 - 서울", "4,000 다운로드", "1500 원"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon05), "지하철 노선도 - 도쿄", "6,000 다운로드", "1500 원"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon05), "지하철 노선도 - LA", "8,000 다운로드", "1500 원"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon05), "지하철 노선도 - 워싱턴", "7,000 다운로드", "1500 원"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon05), "지하철 노선도 - 파리", "9,000 다운로드", "1500 원"));
	        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.icon05), "지하철 노선도 - 베를린", "38,000 다운로드", "1500 원"));
	 
	        // call setAdapter()
	        list.setAdapter(adapter);
	 
	 
	        // use adapter.notifyDataSetChanged() to apply changes after adding items dynamically
	        // adapter.notifyDataSetChanged();
	 
	 
	        // set listener
	        list.setOnDataSelectionListener(new OnDataSelectionListener() {
	            public void onDataSelected(AdapterView parent, View v, int position, long id) {
	                IconTextItem curItem = (IconTextItem) adapter.getItem(position);
	                String[] curData = curItem.getData();
	 
	                Toast.makeText(getApplicationContext(), "Selected : " + curData[0], 2000).show();
	            }
	        });
	 
	 
	        // display as the main layout
	        setContentView(list, params);
	    }
}
