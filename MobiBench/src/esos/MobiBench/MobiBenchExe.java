package esos.MobiBench;

import android.os.Environment;

public class MobiBenchExe {
	MobiBenchExe() {
       
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
	
	public void SetStoragePath(String path) {
		data_path = path;
		sdcard_2nd_path = StorageOptions.determineStorageOptions();
	}
	
	private void RunMobibench(int access_mode, int db_enable, int db_mode) {
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
		
		if(db_enable == 0) {
			if(access_mode == 0 || access_mode == 1) {
				command += " -f "+set.get_filesize_write()*1024;
			} else {
				command += " -f "+set.get_filesize_read()*1024;
			}
			command += " -r "+set.get_io_size();
			command += " -a "+access_mode;
			command += " -y "+set.get_file_sync_mode();
			command += " -t "+set.get_thread_num();
		} else {
			command += " -d "+db_mode;
			command += " -n "+set.get_transaction_num();
		}
		
		System.out.println("mobibench command : "+ command);
		
		mobibench_run(command);
	}
	
    native void mobibench_run(String str);
    native int getProgress();
    
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
    
    public void RunFileIO() {
    	RunMobibench(0, 0, 0);
    	printResult();
    }
    
    public void RunSqlite() {
    	RunMobibench(0, 1, 0);
    	printResult();
    }
    
    public void RunCustom() {
    	RunMobibench(0, 1, 0);
    	printResult();
    }
}
