package esos.MobiBench;

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
	
	private void RunMobibench() {
		Setting set = new Setting();
		
		int transaction_num = set.get_transaction_num();
		
		String command = "mobibench -p /sdcard -d 0"+" -n "+transaction_num;
		mobibench_run(command);
	}
	
    native void mobibench_run(String str);
    native int getProgress();
    
    public void LoadEngine() {
    	 System.loadLibrary("mobibench");    
    }
    
    public void RunFileIO() {
    	RunMobibench();
    	System.out.println("mobibench cpu_active : "+ cpu_active);
    	System.out.println("mobibench cpu_idle : "+ cpu_idle);
    	System.out.println("mobibench cpu_iowait : "+ cpu_iowait);
    	System.out.println("mobibench cs_total : "+ cs_total);
    	System.out.println("mobibench cs_voluntary : "+ cs_voluntary);
    	System.out.println("mobibench throughput : "+ throughput);
    	System.out.println("mobibench tps : "+ tps);
    }
    
    public void RunSqlite() {
    	RunMobibench();
    }
    
    public void RunCustom() {
    	
    }
}
