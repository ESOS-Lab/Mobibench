package esos.MobiBench;

import esos.ResultListControl.DialogActivity;
import android.os.Environment;

public class MobiBenchExe {
	MobiBenchExe() {
       
    }
	
	public enum eAccessMode {
		WRITE,
		RANDOM_WRITE,
		READ,
		RANDOM_READ
	}
	
	public enum eDbMode {
		INSERT,
		UPDATE,
		DELETE
	}
	
	public enum eDbEnable {
		DB_DISABLE,
		DB_ENABLE
	}
	
	public float cpu_active = 0;
	public float cpu_idle = 0;
	public float cpu_iowait = 0;
	public int cs_total = 0;
	public int cs_voluntary = 0;
	public float throughput = 0;
	public float tps = 0;
	
	public static String data_path = null;
	public static String sdcard_2nd_path = null;
	
	static String ExpName[] = {
		"Seq.Write",
		"Seq.read", 
		"Rand.Write",
		"Rand.Read",
		"SQLite.Insert", 
		"SQLite.Update",
		"SQLite.Delete"
	};
	
	public void SetStoragePath(String path) {
		data_path = path;
		sdcard_2nd_path = StorageOptions.determineStorageOptions();
	}
	
	private void RunMobibench(eAccessMode access_mode, eDbEnable db_enable, eDbMode db_mode) {
		Setting set = new Setting();
		int part = set.get_target_partition();
		 
		String partition;
		
		if(part == 0) {
			partition = data_path;
		} else if(part == 1) {	
			partition = Environment.getExternalStorageDirectory().getPath();
		} else {
			partition = sdcard_2nd_path;
		}
					
		String command = "mobibench";
		command += " -p "+partition+"/mobibench";
		
		if(db_enable == eDbEnable.DB_DISABLE) {
			if(access_mode == eAccessMode.WRITE || access_mode == eAccessMode.RANDOM_WRITE) {
				command += " -f "+set.get_filesize_write()*1024;
			} else {
				command += " -f "+set.get_filesize_read()*1024;
			}
			command += " -r "+set.get_io_size();
			command += " -a "+access_mode.ordinal();
			command += " -y "+set.get_file_sync_mode();
			command += " -t "+set.get_thread_num();
		} else {
			command += " -d "+db_mode.ordinal();
			command += " -n "+set.get_transaction_num();
			//command += " -n "+1000;
			command += " -j "+set.get_journal_mode();
			command += " -s "+set.get_sql_sync_mode();
		}
		
		System.out.println("mobibench command : "+ command);
		
		mobibench_run(command);
	}
	
    native void mobibench_run(String str);
    native int getProgress();
    native int getState();
    
    public void LoadEngine() {
    	 System.loadLibrary("mobibench");    
    }
    
    public void printResult() {
    	System.out.println("mobibench cpu_active : "+ cpu_active);
    	System.out.println("mobibench cpu_idle : "+ cpu_idle);
    	System.out.println("mobibench cpu_iowait : "+ cpu_iowait);
    	System.out.println("mobibench cs_total : "+ cs_total);
    	System.out.println("mobibench cs_voluntary : "+ cs_voluntary);
    	System.out.println("mobibench throughput : "+ throughput);
    	System.out.println("mobibench tps : "+ tps);   	
    }
    
    public void SendResult(int result_id) {
    	printResult();
    	
    	DialogActivity.ResultCPU_act[result_id] = String.format("%.1f", cpu_active);
    	DialogActivity.ResultCPU_iow[result_id] = String.format("%.1f", cpu_iowait);
    	DialogActivity.ResultCPU_idl[result_id] = String.format("%.1f", cpu_idle);
    	DialogActivity.ResultCS_tot[result_id] = ""+cs_total;
    	DialogActivity.ResultCS_vol[result_id] = ""+cs_voluntary;
    	if(result_id < 4) {
    		DialogActivity.ResultThrp[result_id] = String.format("%.2f KB/s", throughput);
    	} else {
        	DialogActivity.ResultThrp[result_id] = String.format("%.2f TPS", tps);
    	}
    	DialogActivity.ResultExpName[result_id] = ExpName[result_id];
    	DialogActivity.bHasResult[result_id]=true;
    }
    
    public void RunFileIO() {
    	RunMobibench(eAccessMode.WRITE, eDbEnable.DB_DISABLE, eDbMode.INSERT);
    	SendResult(0);
    	RunMobibench(eAccessMode.READ, eDbEnable.DB_DISABLE, eDbMode.INSERT);
    	SendResult(1);
    	RunMobibench(eAccessMode.RANDOM_WRITE, eDbEnable.DB_DISABLE, eDbMode.INSERT);
    	SendResult(2);
    	RunMobibench(eAccessMode.RANDOM_READ, eDbEnable.DB_DISABLE, eDbMode.INSERT);
    	SendResult(3);
    }
    
    public void RunSqlite() {
    	RunMobibench(eAccessMode.WRITE, eDbEnable.DB_ENABLE, eDbMode.INSERT);
    	SendResult(4);
    	RunMobibench(eAccessMode.WRITE, eDbEnable.DB_ENABLE, eDbMode.UPDATE);
    	SendResult(5);
    	RunMobibench(eAccessMode.WRITE, eDbEnable.DB_ENABLE, eDbMode.DELETE);
    	SendResult(6);
    }
    
    public void RunCustom() {
    	Setting set = new Setting();
    	if(set.get_seq_write() == true) {
    		RunMobibench(eAccessMode.WRITE, eDbEnable.DB_DISABLE, eDbMode.INSERT);
    		SendResult(0);
    	}  	
    	if(set.get_seq_read() == true) {
    		RunMobibench(eAccessMode.READ, eDbEnable.DB_DISABLE, eDbMode.INSERT);
    		SendResult(1);
    	}
    	if(set.get_ran_write() == true) {
    		RunMobibench(eAccessMode.RANDOM_WRITE, eDbEnable.DB_DISABLE, eDbMode.INSERT);
    		SendResult(2);
    	}     	
    	if(set.get_ran_read() == true) {
    		RunMobibench(eAccessMode.RANDOM_READ, eDbEnable.DB_DISABLE, eDbMode.INSERT);
    		SendResult(3);
    	}  
    	if(set.get_insert() == true) {
    		RunMobibench(eAccessMode.WRITE, eDbEnable.DB_ENABLE, eDbMode.INSERT);
    		SendResult(4);
    	}
    	if(set.get_update() == true) {
    		RunMobibench(eAccessMode.WRITE, eDbEnable.DB_ENABLE, eDbMode.UPDATE);
    		SendResult(5);
    	}  
    	if(set.get_delete() == true) {
    		RunMobibench(eAccessMode.WRITE, eDbEnable.DB_ENABLE, eDbMode.DELETE);
    		SendResult(6);
    	}    	
    }
}
