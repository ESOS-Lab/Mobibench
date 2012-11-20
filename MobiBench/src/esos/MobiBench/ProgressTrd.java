package esos.MobiBench;

import android.os.Handler;
import android.os.Message;

public class ProgressTrd extends Thread{
	private static int part_flag = 0;
	private int switch_flag= 7;
	private int count_flag = 0;	
	private boolean run_flag = true;
	private boolean wait = false;
	private Handler mHandler;
	
	private Message msg = null;
	
	ProgressTrd(Handler handler){
		mHandler = handler;
	}
	
	public void run(){
		switch(switch_flag){
		
			//p5, p6, p7선택 시
			case 0:
				count_flag = 0;
				msg = Message.obtain(mHandler, 1); 
				mHandler.sendMessage(msg);// progress bar의 max를 23으로 한다.
				while(run_flag) {          
				//무한 반복하면서 1초에 1씩 증가
					synchronized(this){
						if(wait){
							try{
								wait();
							}catch(Exception e){
								//
							}
						}
					}
					try {

						Thread.sleep(1000); // 1초를 쉰 뒤, 
						++count_flag;
						// Part 5일 경우 - switch_flag가 1이다. 
						switch(part_flag){
						case 0:
							if( (count_flag%23) == 0){
								msg = Message.obtain(mHandler, 2); 
								mHandler.sendMessage(msg);// progress를 0으로 초기화 시킨다. 
							}
							if( (count_flag % 920) == 0){
								//run_flag = false;
								msg = Message.obtain(mHandler, 3); 
								mHandler.sendMessage(msg);//progress bar를 30으로 설정하여 part6이 실행되도록 한다.
								msg = Message.obtain(mHandler, 6); 
								mHandler.sendMessage(msg);// Title bar를 part 6 으로 바꿔준다.
								count_flag = 0;
								part_flag = 1;
							}
							break;
							

						}
						if(!wait){
							msg = Message.obtain(mHandler, 0); 
							mHandler.sendMessage(msg);// progress bar를 1 증가 시킨다. 

						}
						
					}catch(InterruptedException e) {
						//
					}

				}
				msg = Message.obtain(mHandler, 8); 
				mHandler.sendMessage(msg);// progress를 0으로 초기화 시킨다. 
				break;
		
		}
	}
	
	public void pauseThread(){
		wait = true;
		synchronized(this){
			this.notify();
		}
	}
	
	public void resumeThread(){
		wait = false;
		synchronized(this){
			this.notify();
		}
	}
	
	public void stopThread(){
		run_flag = false;
		part_flag = 0;
		synchronized(this){
			this.notify();
		}
	}
	
	public void setTrd(boolean p5, boolean p6, boolean p7){
		if( p5 && p6 && p7){
			switch_flag = 0;
		}else if( p5 && p6){
			switch_flag = 1;
		}else if(p6 && p7){
			switch_flag = 2;
		}else if(p5 && p7){
			switch_flag = 3;
		}else if(p5){
			switch_flag = 4;
		}else if(p6){
			switch_flag = 5;
		}else if(p7){
			switch_flag = 6;
		}
	}

}
