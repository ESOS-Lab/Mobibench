package esos.MobiBench;


import esos.MobiBench.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;


public class TabMain extends TabActivity {

	private SharedPreferences prefs = null;
	private SharedPreferences.Editor editor = null;
	
	private boolean root_flag;
	private boolean start_flag=false;
	
	private Setting set = new Setting();
	
	private CheckBox CB_SW = null;
	private CheckBox CB_SR = null;
	private CheckBox CB_RW = null;
	private CheckBox CB_RR = null;
	private CheckBox CB_INSERT = null;
	private CheckBox CB_UPDATE = null;
	private CheckBox CB_DELETE = null;
	
	private EditText et_threadnum = null;
	private EditText et_filesize_w = null;
	private EditText et_filesize_r = null;
	private EditText et_io_size = null;
	private EditText et_transaction = null;
	
	private Spinner sp_partition = null;
	private Spinner sp_file_sync= null;
	private Spinner sp_sql_sync= null;
	private Spinner sp_journal= null;
	private MobiBenchExe m_exe = null;	

	static final int PROGRESS_DIALOG = 0;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		if(m_exe == null) {
			m_exe = new MobiBenchExe();
			m_exe.LoadEngine();
			m_exe.SetStoragePath(this.getFilesDir().toString());
		}		
		
		/* For tab layout setting */
		TabHost tabHost = getTabHost();			
		LayoutInflater.from(this).inflate(R.layout.tabmain,tabHost.getTabContentView(), true);			
		tabHost.addTab(tabHost.newTabSpec("measure").setIndicator("",getResources().getDrawable(R.drawable.tab_mea)).setContent(R.id.measure));
		tabHost.addTab(tabHost.newTabSpec("history").setIndicator("",getResources().getDrawable(R.drawable.tab_history)).setContent(R.id.history));
		tabHost.addTab(tabHost.newTabSpec("setting").setIndicator("",getResources().getDrawable(R.drawable.tab_help)).setContent(R.id.help));
		
		
		/* spinner define (total 4 spinner) */
		sp_partition = (Spinner)findViewById(R.id.sp_partition);
		sp_file_sync = (Spinner)findViewById(R.id.sp_file_sync);
		sp_sql_sync = (Spinner)findViewById(R.id.sp_sql_sync);
		sp_journal = (Spinner)findViewById(R.id.sp_journal);
		
		ArrayAdapter ad_partition;
		if(StorageOptions.b_2nd_sdcard == true) {
			ad_partition = ArrayAdapter.createFromResource(this, R.array.partition, android.R.layout.simple_spinner_item);
		} else {	
			ad_partition = ArrayAdapter.createFromResource(this, R.array.partition2, android.R.layout.simple_spinner_item);
		}
		ArrayAdapter ad_file_sync = ArrayAdapter.createFromResource(this, R.array.filesyncmode,R.layout.spinner_item);
		ArrayAdapter ad_sql_sync = ArrayAdapter.createFromResource(this, R.array.sqlsyncmode,R.layout.spinner_item);
		ArrayAdapter ad_journal = ArrayAdapter.createFromResource(this, R.array.journalmode,R.layout.spinner_item);
//		
		ad_partition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ad_file_sync.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ad_sql_sync.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ad_journal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sp_partition.setAdapter(ad_partition);
		sp_file_sync.setAdapter(ad_file_sync);
		sp_sql_sync.setAdapter(ad_sql_sync);
		sp_journal.setAdapter(ad_journal);
		
		et_threadnum = (EditText)findViewById(R.id.threadnum);
		et_filesize_w = (EditText)findViewById(R.id.filesize_w);
		et_filesize_r = (EditText)findViewById(R.id.filesize_r);
		et_io_size = (EditText)findViewById(R.id.io_size);
		et_transaction = (EditText)findViewById(R.id.transcation);
		

		/* Preference Control */
		prefs = getSharedPreferences("Setting", MODE_PRIVATE);
		root_flag = prefs.getBoolean("init_flag", true);
		editor = prefs.edit();
		
		if( root_flag ){
			set_default();				
			startActivityForResult(new Intent(TabMain.this, First.class), 0);	
		}else{
			load_init();		
		}
		
		
		/* Image button listener*/
		findViewById(R.id.btn_execute).setOnClickListener(mClickListener);
		findViewById(R.id.btn_all).setOnClickListener(mClickListener);
		findViewById(R.id.btn_file).setOnClickListener(mClickListener);
		findViewById(R.id.btn_sqlite).setOnClickListener(mClickListener);
		findViewById(R.id.btn_custom).setOnClickListener(mClickListener);
		
		/* ******************* */
		/*   Spinner Control   */
		/* ******************* */
		// Partition spinner
		sp_partition.setOnItemSelectedListener(
			new OnItemSelectedListener(){
				public void onItemSelected(AdapterView<?> parent,View view, int position, long id){
					switch(position){ 
					case 0:
						editor.putInt("p_target_partition", 0);					
						set.set_target_partition(0);
						break;
					case 1:
						editor.putInt("p_target_partition", 1);	
						set.set_target_partition(1);
						break;
					case 2:
						editor.putInt("p_target_partition", 2);	
						set.set_target_partition(2);
						break;					
					}
					editor.commit();	
				}
				public void onNothingSelected(AdapterView<?> parent){
//	
				}		
			}
		);

		// File synchronization spinner
		sp_file_sync.setOnItemSelectedListener(
			new OnItemSelectedListener(){
				public void onItemSelected(AdapterView<?> parent,View view, int position, long id){
					switch(position){ 
					case 0:
						editor.putInt("p_file_sync_mode", 0);	
						set.set_file_sync_mode(0);
						break;
					case 1:
						editor.putInt("p_file_sync_mode", 1);	
						set.set_file_sync_mode(1);
						break;
					case 2:
						editor.putInt("p_file_sync_mode", 2);	
						set.set_file_sync_mode(2);
						break;
					case 3:
						editor.putInt("p_file_sync_mode", 3);	
						set.set_file_sync_mode(3);
						break;
					case 4:
						editor.putInt("p_file_sync_mode", 4);	
						set.set_file_sync_mode(4);
						break;
					case 5:
						editor.putInt("p_file_sync_mode", 5);	
						set.set_file_sync_mode(5);
						break;
					case 6:
						editor.putInt("p_file_sync_mode", 6);	
						set.set_file_sync_mode(6);
						break;
					case 7:
						editor.putInt("p_file_sync_mode", 7);	
						set.set_file_sync_mode(7);
						break;
					}
					editor.commit();
				}
				public void onNothingSelected(AdapterView<?> parent){
//	
				}
			}
		);		
		
		// SQLite synchronization spinner
		sp_sql_sync.setOnItemSelectedListener(
				new OnItemSelectedListener(){
					public void onItemSelected(AdapterView<?> parent,View view, int position, long id){
						switch(position){ 
						case 0:
							editor.putInt("p_sql_sync_mode", 0);	
							set.set_sql_sync_mode(0);
							break;
						case 1:
							editor.putInt("p_sql_sync_mode", 1);
							set.set_sql_sync_mode(1);
							break;
						case 2:
							editor.putInt("p_sql_sync_mode", 2);
							set.set_sql_sync_mode(2);
							break;
						}
						editor.commit();
					}
					public void onNothingSelected(AdapterView<?> parent){
	//	
					}
				}
			);			
		
		// SQL journaling spinner
		sp_journal.setOnItemSelectedListener(
				new OnItemSelectedListener(){
					public void onItemSelected(AdapterView<?> parent,View view, int position, long id){
						switch(position){ 
						case 0:
							editor.putInt("p_journal_mode", 0);
							set.set_journal_mode(0);
							break;
						case 1:
							editor.putInt("p_journal_mode", 1);
							set.set_journal_mode(1);
							break;
						case 2:
							editor.putInt("p_journal_mode", 2);
							set.set_journal_mode(2);
							break;
						case 3:
							editor.putInt("p_journal_mode", 3);
							set.set_journal_mode(3);
							break;
						case 4:
							editor.putInt("p_journal_mode", 4);
							set.set_journal_mode(4);
							break;
						case 5:
							editor.putInt("p_journal_mode", 5);
							set.set_journal_mode(5);
							break;						
						}
						editor.commit();
					}
					public void onNothingSelected(AdapterView<?> parent){
	//	
					}
				}
			);	
		
		
		/* ******************* */
		/*  Check box control  */
		/* ******************* */
		// Sequential Write Check box
        CB_SW = (CheckBox)findViewById(R.id.cb_sw);
        CB_SW.setOnCheckedChangeListener(
        		new OnCheckedChangeListener(){
        			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        				if(isChecked == true){
        					set.set_seq_write(true);
        				}else{
        					set.set_seq_write(false);        					
        				}
        			}   	
        });	
		
		// Sequential Read Check box
        CB_SR = (CheckBox)findViewById(R.id.cb_sr);
        CB_SR.setOnCheckedChangeListener(
        		new OnCheckedChangeListener(){
        			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        				if(isChecked == true){
        					set.set_seq_read(true);
        				}else{
        					set.set_seq_read(false);
        				}
        			}   	
        });	
		// Random Write Check box
        CB_RW = (CheckBox)findViewById(R.id.cb_rw);
        CB_RW.setOnCheckedChangeListener(
        		new OnCheckedChangeListener(){
        			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        				if(isChecked == true){
        					set.set_ran_write(true);
        				}else{
        					set.set_ran_write(false);
        				}
        			}   	
        });	
        
		// Random Read Check box
        CB_RR = (CheckBox)findViewById(R.id.cb_rr);
        CB_RR.setOnCheckedChangeListener(
        		new OnCheckedChangeListener(){
        			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        				if(isChecked == true){
        					set.set_ran_read(true);
        				}else{
        					set.set_ran_read(false);
        				}
        			}   	
        });	
        
		// SQLite Insert Check box
        CB_INSERT = (CheckBox)findViewById(R.id.cb_insert);
        CB_INSERT.setOnCheckedChangeListener(
        		new OnCheckedChangeListener(){
        			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        				if(isChecked == true){
        					set.set_insert(true);
        				}else{
        					set.set_insert(false);
        				}
        			}   	
        });	
        
		// Sequential Write Check box
        CB_UPDATE = (CheckBox)findViewById(R.id.cb_update);
        CB_UPDATE.setOnCheckedChangeListener(
        		new OnCheckedChangeListener(){
        			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        				if(isChecked == true){
        					set.set_update(true);
        				}else{
        					set.set_update(false);
        				}
        			}   	
        });	
        
		// Sequential Write Check box
        CB_DELETE = (CheckBox)findViewById(R.id.cb_delete);
        CB_DELETE.setOnCheckedChangeListener(
        		new OnCheckedChangeListener(){
        			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        				if(isChecked == true){
        					set.set_delete(true);
        				}else{
        					set.set_delete(false);
        				}
        			}   	
        });	

	}
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0) {
        	if(resultCode == RESULT_CANCELED) {
        		this.finish();
        	}
        }
    }
    
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
		      setResult(RESULT_CANCELED);
		      storeValue();
		      this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/* Image button listener*/
	Button.OnClickListener mClickListener = new View.OnClickListener() {
		public void onClick(View v) {
	//		Intent intent;		
			switch(v.getId()){
			case R.id.btn_execute:
				set_default();
				load_init();
			//	print_values();		
				break;
			case R.id.btn_all:
				m_exe.RunFileIO();
				m_exe.RunSqlite();
				// do something here
				break;
			case R.id.btn_file:
				m_exe.RunFileIO();
				// do something here
				break;
			case R.id.btn_sqlite:
				m_exe.RunSqlite();
				// do something here
				break;
			case R.id.btn_custom:
				m_exe.RunCustom();
				// do something here
				break;
			}
		}
	};
	
	/* Load : stored in preferences */
	/* Load preferenced values */
	private void load_init() {
		if((StorageOptions.b_2nd_sdcard == false) && prefs.getInt("p_target_partition", 0) == 2) {
			sp_partition.setSelection(0);
		} else {
			sp_partition.setSelection(prefs.getInt("p_target_partition", 0));
		}
		et_threadnum.setText(String.valueOf(prefs.getInt("p_threadnum", 1)));
		et_filesize_w.setText(String.valueOf(prefs.getInt("p_filesize_w", 1)));
		et_filesize_r.setText(String.valueOf(prefs.getInt("p_filesize_r", 256)));
		et_io_size.setText(String.valueOf(prefs.getInt("p_io_size", 4)));
		sp_file_sync.setSelection(prefs.getInt("p_file_sync_mode", 0));
		et_transaction.setText(String.valueOf(prefs.getInt("p_transaction", 1)));
		sp_sql_sync.setSelection(prefs.getInt("p_sql_sync_mode", 0));
		sp_journal.setSelection(prefs.getInt("p_journal_mode", 0));
		
		if((StorageOptions.b_2nd_sdcard == false) && prefs.getInt("p_target_partition", 0) == 2) {
			set.set_target_partition(0);
		} else {
			set.set_target_partition(prefs.getInt("p_target_partition", 0));
		}
		set.set_thread_num(prefs.getInt("p_threadnum", 1));	
		set.set_filesize_write(prefs.getInt("p_filesize_w", 1));
		set.set_filesize_read(prefs.getInt("p_filesize_r", 256));		
		set.set_io_size(prefs.getInt("p_io_size", 4));
		set.set_file_sync_mode(prefs.getInt("p_file_sync_mode", 0));
		set.set_transaction_num(prefs.getInt("p_transaction", 1));	
		set.set_sql_sync_mode(prefs.getInt("p_sql_sync_mode", 0));
		set.set_journal_mode(prefs.getInt("p_journal_mode", 0));
	}
	
	/* Store values : To the preference */
	public void storeValue() {				
		editor.putInt("p_threadnum", Integer.parseInt(et_threadnum.getText().toString()));
		editor.putInt("p_filesize_w", Integer.parseInt(et_filesize_w.getText().toString()));
		editor.putInt("p_filesize_r", Integer.parseInt(et_filesize_r.getText().toString()));
		editor.putInt("p_io_size", Integer.parseInt(et_io_size.getText().toString()));
		editor.putInt("p_transaction", Integer.parseInt(et_transaction.getText().toString()));
		editor.commit();
	}

	
	public void set_default() {
		editor.putInt("p_target_partition", 0);
		set.set_target_partition(0);
		editor.putInt("p_threadnum", 1);
		set.set_thread_num(1);	
		
		editor.putInt("p_filesize_w", 1);
		set.set_filesize_write(1);
		editor.putInt("p_filesize_r", 256);
		set.set_filesize_read(256);		
		editor.putInt("p_io_size", 4);
		set.set_io_size(4);
		editor.putInt("p_file_sync_mode", 0);
		set.set_file_sync_mode(0);
		
		editor.putInt("p_transaction", 100);
		set.set_transaction_num(100);	
		editor.putInt("p_sql_sync_mode", 2);
		set.set_sql_sync_mode(1);
		editor.putInt("p_journal_mode", 1);
		set.set_journal_mode(1);		
	
		
		editor.commit();			
	}


	/* For Debug */
	public void print_values() {
		Toast.makeText(this, "target_partition "+ set.get_target_partition(), Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "thread_num "+ set.get_thread_num(), Toast.LENGTH_SHORT).show();	
		Toast.makeText(this, "filesize_write "+ set.get_filesize_write(), Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "filesize_read "+ set.get_filesize_read(), Toast.LENGTH_SHORT).show();
		
		Toast.makeText(this, "recode_size "+ set.get_io_size(), Toast.LENGTH_SHORT).show();
		
		Toast.makeText(this, "file_sync_mode "+ set.get_file_sync_mode(), Toast.LENGTH_SHORT).show();		
			
		Toast.makeText(this, "transaction_num "+ set.get_transaction_num(), Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "journal_mode "+ set.get_journal_mode(), Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "sql_sync_mode "+ set.get_sql_sync_mode(), Toast.LENGTH_SHORT).show();
		
	}
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
    	Intent intent;
    	switch(item.getItemId()){

    	case R.id.menu_info:
			intent = new Intent(TabMain.this, Info.class);
			startActivity(intent);
    		return true;
    	}
    	return false;
    }
    


}
