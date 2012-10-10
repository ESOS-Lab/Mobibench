/* mobibench.c for mobile benchmark tool(mobibench)
 *
 * Copyright (C) 2012 ESOS lab, Hanyang University
 *
 * History
 * 2012. 1 created by Kisung Lee <kisunglee@hanyang.ac.kr>
 * 2012. 8 modified by Sooman Jeong <77smart@hanyang.ac.kr>
 *
 */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#define __USE_GNU
#include <fcntl.h>
#include <time.h>
#include <string.h> 

#include <sys/mman.h>
#include <errno.h>
#include <pthread.h>
#include <sys/syscall.h>

#define MERSENNE


#define FILE_POS 0x38A40000 //950272000

#define SIZE_100MB 104857600
#define SIZE_1MB 1048576
#define SIZE_4KB 4096
#define SIZE_1KB 1024
#define MAX_THREADS 100

long long kilo64; //file size
long long reclen;
long long real_reclen;
long long numrecs64;
int g_access, g_sync;
char* maddr;
long long filebytes64;
int num_threads;
char pathname[128] = {0, };


typedef enum
{
  MODE_WRITE,
  MODE_REWRITE,
  MODE_RND_WRITE,
} file_test_mode_t;

typedef enum
{
	NORMAL,
  OSYNC,
  FSYNC,
  ODIRECT,
  SYDI,
  MMAP,
  MMAP_AS,
  MMAP_S,
} file_sync_mode_t;

typedef enum
{
	NONE,
	READY,
	EXEC,
	END,
} thread_status_t;

thread_status_t thread_status[MAX_THREADS] = {0, };
pthread_mutex_t thread_lock = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t thread_cond1 = PTHREAD_COND_INITIALIZER;
pthread_cond_t thread_cond2 = PTHREAD_COND_INITIALIZER;
pthread_cond_t thread_cond3 = PTHREAD_COND_INITIALIZER;

void print_time(struct timeval T1, struct timeval T2)
{
	long sec,usec;
	double time;
	double rate;
	
	time_t t;
	
	if(T1.tv_usec > T2.tv_usec)
	{
		sec = T2.tv_sec - T1.tv_sec -1;
		usec = 1000000 + T2.tv_usec - T1.tv_usec;
	}
	else
	{
		sec = T2.tv_sec - T1.tv_sec;
		usec = T2.tv_usec - T1.tv_usec;
	}

	time = (double)sec + (double)usec/1000000;
	
	rate = kilo64*1024*num_threads/time;
	
	printf("Elapsed Time : %8ld sec %4ldus. %.0f B/sec, %.2f KB/sec, %.2f MB/sec. \n\n",sec,usec, rate, rate/1024, rate/1024/1024);
	
}

#define DEF_PROCSTAT_BUFFER_SIZE (1 << 10) /* 1 KBytes (512 + 512 ^^) */ 

#define START_CPU_CHECK 0
#define END_CPU_CHECK 1

unsigned long s_CPUTick[2][6]; 

void cpuUsage(int startEnd)
{
	const char *s_ProcStat = "/proc/stat"; 
	const char *s_CPUName = "cpu "; 
	int s_Handle, s_Check, s_Count; 
	char s_StatBuffer[ DEF_PROCSTAT_BUFFER_SIZE ]; 
	char *s_String; 
	float s_DiffTotal; 
	unsigned long active_tick, idle_tick, wait_tick;
	
		s_Handle = open(s_ProcStat, O_RDONLY); 
		
		if(s_Handle >= 0) 
		{ 
				s_Check = read(s_Handle, &s_StatBuffer[0], sizeof(s_StatBuffer) - 1); 
			
				s_StatBuffer[s_Check] = '\0'; 

				s_String = strstr(&s_StatBuffer[0], s_CPUName); /* Total CPU entry */ 
				
				//printf("s_String=%s\n", s_String);

				if(s_String) 
				{ 

					s_Check = sscanf(s_String, "cpu %lu %lu %lu %lu %lu", &s_CPUTick[startEnd][0], &s_CPUTick[startEnd][1], &s_CPUTick[startEnd][2], &s_CPUTick[startEnd][3], &s_CPUTick[startEnd][4]); 

					//printf("s_Check=%d\n", s_Check);

					if(s_Check == 5) 
					{ 

						for(s_Count = 0, s_CPUTick[startEnd][5] = 0lu; s_Count < 5;s_Count++)
							s_CPUTick[startEnd][5] += s_CPUTick[startEnd][s_Count]; 
					}
					
					//printf("[CPU] 0=%ld, 1=%ld, 2=%ld, 3=%ld, 4=%ld, \n", s_CPUTick[startEnd][0], s_CPUTick[startEnd][1], s_CPUTick[startEnd][2], s_CPUTick[startEnd][3], s_CPUTick[startEnd][4]);
				}
		}
		
		//printf("[CPU] 0=%ld, 1=%ld, 2=%ld, 3=%ld, 4=%ld, \n", s_CPUTick[startEnd][0], s_CPUTick[startEnd][1], s_CPUTick[startEnd][2], s_CPUTick[startEnd][3], s_CPUTick[startEnd][4]);
		
		if(startEnd == END_CPU_CHECK)
		{
			s_DiffTotal = (float)(s_CPUTick[END_CPU_CHECK][5] - s_CPUTick[START_CPU_CHECK][5]); 
			active_tick = (s_CPUTick[END_CPU_CHECK][0] - s_CPUTick[START_CPU_CHECK][0]) + (s_CPUTick[END_CPU_CHECK][1] - s_CPUTick[START_CPU_CHECK][1]) + (s_CPUTick[END_CPU_CHECK][2] - s_CPUTick[START_CPU_CHECK][2]);
			idle_tick = (s_CPUTick[END_CPU_CHECK][3] - s_CPUTick[START_CPU_CHECK][3]);
			wait_tick = (s_CPUTick[END_CPU_CHECK][4] - s_CPUTick[START_CPU_CHECK][4]);
						
			//printf("[CPU TICK] Active=%ld, Idle=%ld, IoWait=%ld\n",active_tick, idle_tick, wait_tick);
			printf("[CPU] Active,Idle,IoWait : %1.2f %1.2f %1.2f\n",
							(float)( (float)(active_tick * 100lu) / s_DiffTotal ),
							(float)( (float)(idle_tick * 100lu) / s_DiffTotal ), 
							(float)( (float)(wait_tick * 100lu) / s_DiffTotal ) ); 
		}
		
		close(s_Handle);			
}

#define NN 312
#define MM 156
#define MATRIX_A 0xB5026F5AA96619E9ULL
#define UM 0xFFFFFFFF80000000ULL /* Most significant 33 bits */
#define LM 0x7FFFFFFFULL /* Least significant 31 bits */


/* The array for the state vector */
static unsigned long long mt[NN]; 
/* mti==NN+1 means mt[NN] is not initialized */
static int mti=NN+1; 

void init_genrand64(unsigned long long seed)
{
    mt[0] = seed;
    for (mti=1; mti<NN; mti++) 
        mt[mti] =  (6364136223846793005ULL * (mt[mti-1] ^ (mt[mti-1] >> 62)) + mti);
}

unsigned long long genrand64_int64(void)
{
    int i;
    unsigned long long x;
    static unsigned long long mag01[2]={0ULL, MATRIX_A};

    if (mti >= NN) { /* generate NN words at one time */

        /* if init_genrand64() has not been called, */
        /* a default initial seed is used     */
        if (mti == NN+1) 
            init_genrand64(5489ULL); 

        for (i=0;i<NN-MM;i++) {
            x = (mt[i]&UM)|(mt[i+1]&LM);
            mt[i] = mt[i+MM] ^ (x>>1) ^ mag01[(int)(x&1ULL)];
        }
        for (;i<NN-1;i++) {
            x = (mt[i]&UM)|(mt[i+1]&LM);
            mt[i] = mt[i+(MM-NN)] ^ (x>>1) ^ mag01[(int)(x&1ULL)];
        }
        x = (mt[NN-1]&UM)|(mt[0]&LM);
        mt[NN-1] = mt[MM-1] ^ (x>>1) ^ mag01[(int)(x&1ULL)];

        mti = 0;
    }
  
    x = mt[mti++];

    x ^= (x >> 29) & 0x5555555555555555ULL;
    x ^= (x << 17) & 0x71D67FFFEDA60000ULL;
    x ^= (x << 37) & 0xFFF7EEE000000000ULL;
    x ^= (x >> 43);

    return x;
}

void init_by_array64(unsigned long long init_key[],
		     unsigned long long key_length)
{
    unsigned long long i, j, k;
    init_genrand64(19650218ULL);
    i=1; j=0;
    k = (NN>key_length ? NN : key_length);
    for (; k; k--) {
        mt[i] = (mt[i] ^ ((mt[i-1] ^ (mt[i-1] >> 62)) * 3935559000370003845ULL))
          + init_key[j] + j; /* non linear */
        i++; j++;
        if (i>=NN) { mt[0] = mt[NN-1]; i=1; }
        if (j>=key_length) j=0;
    }
    for (k=NN-1; k; k--) {
        mt[i] = (mt[i] ^ ((mt[i-1] ^ (mt[i-1] >> 62)) * 2862933555777941757ULL))
          - i; /* non linear */
        i++;
        if (i>=NN) { mt[0] = mt[NN-1]; i=1; }
    }

    mt[0] = 1ULL << 63; /* MSB is 1; assuring non-zero initial array */ 
}

void MERSENNE_test()
{

    unsigned long long init[4]={0x12345ULL, 0x23456ULL, 0x34567ULL, 0x45678ULL};
    unsigned long long length=4;
    long long kilo64;
    long long reclen;
		long long numrecs64;
		
    long long *recnum= 0;
    long long i;
    unsigned long long big_rand;
    
    kilo64=1024;
    reclen=1024;
    
    numrecs64 = (kilo64*1024)/reclen;	
    
    init_by_array64(init, length);
    
    recnum = (long long *)malloc(sizeof(*recnum)*numrecs64);
    
    if (recnum){
             /* pre-compute random sequence based on 
		Fischer-Yates (Knuth) card shuffle */
            for(i = 0; i < numrecs64; i++){
                recnum[i] = i;
            }
            for(i = 0; i < numrecs64; i++) {
                long long tmp;

      	       big_rand=genrand64_int64();

               big_rand = big_rand%numrecs64;
               tmp = recnum[i];
               recnum[i] = recnum[big_rand];
               recnum[big_rand] = tmp;
            }
        }
	else
	{
		fprintf(stderr,"Random uniqueness fallback.\n");
	}

	#if 0
	for(i = 0; i < numrecs64; i++) {
		
		printf("%lld\n",	recnum[i]);
	}
	#endif

}

char j_p_path[100]=  "/proc/";
char j_p_name[100];
unsigned int j_nr_switches[2];
int storage_switches[2][1000];
int storage_count=0;

void get_path(pid_t j_pid, pid_t j_tid)
{
//	printf("%s, %d\n", __func__, j_pid);
	sprintf(j_p_name, "%d/task/%d", j_pid, j_tid);
	strcat(j_p_path,j_p_name);
	strcat(j_p_path,"/sched");
}

void get_con_switches()
{
	storage_switches[0][storage_count] = j_nr_switches[0];
	storage_switches[1][storage_count] = j_nr_switches[1];
	storage_count++;
}

void print_con_switches()
{
	printf("PRINT CON SWITCHES\n");
	
	int output_fd;
	int i;
	for(i =0 ; i< storage_count ; i++)
		printf("%d [th] \t\t N1 %u \t\t N2 %u \n",i/2,storage_switches[0][i],storage_switches[1][i]);

	printf( "CS_COUNT %d %d",storage_switches[0][1] - storage_switches[0][0], storage_switches[1][1] - storage_switches[1][0]);
	printf( "\n");
}


int single_get_nr_switches(void)
{
	int j_context_fd;
	char j_buf[3072], *j_token[2];
	char j_dummy[128];
	int ret;
	
	j_context_fd = open(j_p_path, O_RDONLY);
	
	if(j_context_fd < 0)
	{
		printf("\n\n FD: %d\n\n", j_context_fd);
		perror("Fail to open ");
		return -1;
	}

	ret = read(j_context_fd, j_buf, sizeof(j_buf));
	if(ret < 0)
	{
		perror("Fail to read");
		close(j_context_fd);
		return -2;
	}

	j_token[0] = strstr(j_buf, "nr_switches");
	j_token[1] = strstr(j_buf, "nr_voluntary_switches");

	sscanf(j_token[0], "%s %s %u", j_dummy, j_dummy, &j_nr_switches[0]);
	sscanf(j_token[1], "%s %s %u", j_dummy, j_dummy, &j_nr_switches[1]);

	close(j_context_fd);

	return 0;
}


/************************************************************************/
/* Initialize a file that will be used by mmap.				*/
/************************************************************************/
char *
initfile(int fd, long long filebytes,int flag,int prot, int reclen)
{
	 char *pa;
	 int mflags=0;
	 long long x;
	 char *tmp,*stmp;
	 int file_flags;
	 long long recs;
	 long long i;
	 int dflag = 0;

	 if(flag)
	 {

	 	/* 
		  * Allocate a temporary buffer to meet any alignment 
		  * contraints of any method.
		  */
		 tmp=(char *)malloc((size_t)reclen * 2);
		 stmp=tmp;
		 /* 
		  * Align to a reclen boundary.
		  */
		 tmp = (char *)((((long)tmp + (long)reclen))& ~(((long)reclen-1)));
		/* 
		 * Special case.. Open O_DIRECT, and going to be mmap() 
		 * Under Linux, one can not create a sparse file using 
		 * a file that is opened with O_DIRECT 
		 */
	 	file_flags=fcntl(fd,F_GETFL);

		dflag = O_DIRECT;

		{
			/* Save time, just seek out and touch at the end */
		 	lseek(fd,(filebytes-reclen),SEEK_SET);
			x=write(fd,tmp,(size_t)reclen);
			if(x < 1)
			{
				printf("Unable to write file\n");
				exit(181);
			}
		}
	 	free(stmp);
	 	lseek(fd,0,SEEK_SET);
	 }

	if((prot & PROT_WRITE)==PROT_WRITE)
		mflags=MAP_FILE|MAP_SHARED;
	else
		mflags=MAP_FILE|MAP_PRIVATE;

	 pa = (char *)mmap( ((char *)0),filebytes, prot, 
	 		mflags, fd, 0);

	if(pa == (char *)-1)
	{
		printf("Mapping failed, errno %d\n",errno);
		exit(166);
	}

	return(pa);
}

/************************************************************************/
/* Release the mmap area.						*/
/************************************************************************/
void
mmap_end( char *buffer, long long size)
{
	if(munmap(buffer,(size_t)size)<0)
		printf("munmap failed.\n");	
}

void wait_thread_status(int thread_num, thread_status_t stat, pthread_cond_t* cond)
{
	int ret = 0;
	int i;
	int wait = 0;

//	printf("%s, %d, %d start\n", __func__, thread_num, stat);
	
	ret = pthread_mutex_lock(&thread_lock);
	if(ret < 0)
	{
		perror("pthread_mutex_lock failed");
		exit(EXIT_FAILURE);
	}
	
	while(1)
	{
		
		if(thread_num < 0)
		{
			for(i = 0; i < num_threads; i++)
			{
				if(thread_status[i] != stat)
				{
					wait = 1;
					//printf("thread[%d]:%d\n", i, thread_status[i]);
				}
			}

			if(wait)
				wait = 0;
			else
				break;			
		}
		else
		{
			if(thread_status[thread_num] == stat)
				break;
		}

		ret = pthread_cond_wait(cond, &thread_lock);
		if(ret < 0)
		{
			perror("pthread_cond_wait failed");
			exit(EXIT_FAILURE);
		}
	}
	
	ret = pthread_mutex_unlock(&thread_lock);
	if(ret < 0)
	{
		perror("pthread_mutex_unlock failed");
		exit(EXIT_FAILURE);
	}

//	printf("%s, %d, %d end\n", __func__, thread_num, stat);

	return;
}

void signal_thread_status(int thread_num, thread_status_t stat, pthread_cond_t* cond)
{
	int ret = 0;
	int i;

//	printf("%s, %d, %d start\n", __func__, thread_num, stat);
	
	ret = pthread_mutex_lock(&thread_lock);
	if(ret < 0)
	{
		perror("pthread_mutex_lock failed");
		exit(EXIT_FAILURE);
	}

	if(thread_num < 0)
	{
		for(i = 0; i < num_threads; i++)
		{
			thread_status[i] = stat;
		}
		ret = pthread_cond_broadcast(cond);			
		if(ret < 0)
		{
			perror("pthread_cond_broadcast failed");
			exit(EXIT_FAILURE);
		}		
	}
	else
	{		
		thread_status[thread_num] = stat;
		ret = pthread_cond_signal(cond);	
		if(ret < 0)
		{
			perror("pthread_cond_signal failed");
			exit(EXIT_FAILURE);
		}		
	}

	ret = pthread_mutex_unlock(&thread_lock);
	if(ret < 0)
	{
		perror("pthread_mutex_unlock failed");
		exit(EXIT_FAILURE);
	}		

//	printf("%s, %d, %d end\n", __func__, thread_num, stat);

	return;
}

int thread_main(void* arg)
{
	void *buf;
	int ret = 0;
	int fd;

	unsigned long long init[4]={0x12345ULL, 0x23456ULL, 0x34567ULL, 0x45678ULL};
	unsigned long long length=4;

	long long *recnum= 0;
	long long i;
	unsigned long long big_rand;
	long long offset;

	char* wmaddr;
	int thread_num;
	char filename[128] = {0, };
	int* p_thread_num = (int*)arg;

	thread_num = (int)*p_thread_num;

	if(num_threads == 1)
	{
		//get_path(getpid());
		get_path(getpid(), syscall(SYS_gettid));
	}


	//printf("thread start\n");

	sprintf(filename, "%s/test.db%d", pathname, thread_num);

	init_by_array64(init, length);

	recnum = (long long *)malloc(sizeof(*recnum)*numrecs64);

	if (recnum){
		/* pre-compute random sequence based on 
		Fischer-Yates (Knuth) card shuffle */
		for(i = 0; i < numrecs64; i++){
			recnum[i] = i;
		}
		for(i = 0; i < numrecs64; i++) {
			long long tmp;

			big_rand=genrand64_int64();

			big_rand = big_rand%numrecs64;
			tmp = recnum[i];
			recnum[i] = recnum[big_rand];
			recnum[big_rand] = tmp;
		}
	}
	else
	{
		fprintf(stderr,"Random uniqueness fallback.\n");
	}

	if(g_access == MODE_WRITE)
	{
		ret = unlink(filename);
//		if(ret != 0)
//			printf("Unlink %s failed\n", filename);
	}


	if(g_sync == OSYNC)
		fd = open(filename,  O_RDWR | O_CREAT | O_SYNC);
	else if(g_sync == ODIRECT)
		fd = open(filename,  O_RDWR | O_CREAT | O_DIRECT);
	else if(g_sync == SYDI )
		fd = open(filename,  O_RDWR | O_CREAT | O_SYNC | O_DIRECT);
	else
		fd = open(filename,  O_RDWR | O_CREAT);
		
	if(fd <0)
	{
		printf("Open failed");
		exit(ret);
	}

	if(g_sync == MMAP || g_sync == MMAP_AS || g_sync == MMAP_S)
	{
		maddr=(char *)initfile(fd,filebytes64,1,PROT_READ|PROT_WRITE, real_reclen);
	}

	if ( g_sync == ODIRECT || g_sync == SYDI){
		if (( ret = posix_memalign( &buf, SIZE_4KB, real_reclen )))
		{
			printf("Memalign failed\n");
			exit(ret);
		} 
	}
	else
	{
		buf = malloc(real_reclen);
	}
 
	memset(buf, 0xcafe, real_reclen);

//	printf("T%d ready\n", thread_num);

	signal_thread_status(thread_num, READY, &thread_cond1);

	wait_thread_status(thread_num, EXEC, &thread_cond2);
//	printf("T%d start\n", thread_num);

	if(num_threads == 1)
	{
		single_get_nr_switches();
		get_con_switches();		
	}

	if((g_access == MODE_WRITE || g_access == MODE_REWRITE) && g_sync==FSYNC)
	{
//		printf("SEQ WRITE & FSYNC!!!\n");

		for(i=0; i<numrecs64; i++)
		 {
		 	if(write(fd, buf, real_reclen)<0)
				printf("File write error!!!\n");

				fsync(fd); 	 	 		
				//fdatasync(fd); 	 	 		
		 }	
	}
	else if((g_access == MODE_WRITE || g_access == MODE_REWRITE))
	{
//		printf("SEQ WRITE!!!\n");
		
		for(i=0; i<numrecs64; i++)
		{
		 	if(g_sync == MMAP || g_sync == MMAP_AS || g_sync == MMAP_S)
		 	{
				wmaddr = &maddr[i*reclen];
				bcopy((long long*)buf,(long long*)wmaddr,(long long)real_reclen);
				if(g_sync == MMAP_AS)
					msync(wmaddr,(size_t)reclen,MS_ASYNC);
				else if(g_sync == MMAP_S)
					 msync(wmaddr,(size_t)reclen,MS_SYNC);
		 	}
			else
		 	{
			 	if(write(fd, buf, real_reclen)<0)
					printf("File write error!!!\n");
		 	}
		}	

		if(g_sync == MMAP || g_sync == MMAP_AS || g_sync == MMAP_S)
			msync(maddr,(size_t)filebytes64,MS_SYNC);
		else
			fsync(fd);
	}	
	else if((g_access == MODE_RND_WRITE) && g_sync==FSYNC)
	{
//		printf("RANDOM WRITE & FSYNC!!!\n");
		
		for(i=0; i<numrecs64; i++)
		 {
		 	 offset = (long long)recnum[i]*1024*reclen;
		 	 
		 	 //printf("%lld ", offset);
		 	 
		 	 if(lseek(fd, offset, SEEK_SET)==-1)
		 	 {
					printf("lseek error!!!\n");
						exit(ret);
				}
					
		 	if(write(fd, buf, real_reclen)<0)
				printf("File write error!!!\n");

				fsync(fd); 	 	 		
				//fdatasync(fd); 	 	 		
		 }
	}
	else if((g_access == MODE_RND_WRITE))
	{
//		printf("RANDOM WRITE!!!\n");
		 
		for(i=0; i<numrecs64; i++)
		 {
		 	 offset = (long long)recnum[i]*1024*reclen;

		 	// printf("%lld ", offset);
		 	 
		 	 if(lseek(fd, offset, SEEK_SET)==-1)
		 	 {
					printf("lseek error!!!\n");
						exit(ret);
				}
					
		 	if(write(fd, buf, real_reclen)<0)
				printf("File write error!!!\n");
		 }
		fsync(fd);
	}	
	else{
		printf("Bad combination!!!\n");
		exit(ret);
	}

	if(num_threads == 1)
	{
		single_get_nr_switches();
		get_con_switches();		
	}	

//	printf("T%d end\n", thread_num);
	signal_thread_status(thread_num, END, &thread_cond3);

	if(g_sync == MMAP || g_sync == MMAP_AS || g_sync == MMAP_S)
		mmap_end(maddr,(unsigned long long)filebytes64);

	 close(fd);

	 free(buf);
 
 	if(recnum)
		free(recnum);

//	printf("thread end\n");

	return 0;

}


int main( int argc, char **argv)
{
	int ret = 0;
	int count;
	struct timeval T1, T2;
	long long i = 0;
	char* maddr;
	pthread_t	thread_id[MAX_THREADS];
	void* res;
	int thread_info[MAX_THREADS];	
	
	if(argc!=7)
	{
		printf("Usage: mobibench File path, File size, reclen, AccessMode, Sync Mode, ThreadNum\n");
		printf("  File path\tAbsolute path name(exclude filename)\n");
		printf("  File size\tsize of file(KByte)\n");
		printf("  reclen \tsize of reclen(KByte)\n");
		printf("  AccessMode\t0:WRITE, 1:REWRITE, 2:RANDOM\n");
		printf("  Sync Mode\t0:Normal, 1:O_SYNC, 2:fsync, 3:O_DIRECT, 4:Sync+direct, 5:mmap, 6:mmap+MS_ASYNC, 7:mmap+MS_SYNC\n");
		printf("  ThreadNum\t# of Threads\n\n");
		return;
	}	
  
    strcpy(pathname, argv[1]);
	kilo64 = atoi(argv[2]);
	reclen = atoi(argv[3]);
	g_access = atoi(argv[4]);
	g_sync = atoi(argv[5]);
	num_threads = atoi(argv[6]);

	if(num_threads > MAX_THREADS)
		num_threads = MAX_THREADS;
	else if(num_threads < 1)
		num_threads = 1;
		
	real_reclen = reclen*SIZE_1KB;
  
    numrecs64 = kilo64/reclen;	
	filebytes64 = numrecs64*real_reclen; 

	 printf("File size %lld KB, Reclen %lld KB, Write count %lld \n", kilo64, reclen, numrecs64);
	 printf("Access Mode %d, Sync Mode %d\n", g_access, g_sync);	
	 printf("# of Threads : %d\n", num_threads);
 
	/* Creating threads */
	for(i = 0; i < num_threads; i++)
	{
		thread_info[i]=i;
		ret = pthread_create((pthread_t *)&thread_id[i], NULL, (void*)thread_main, &thread_info[i]);
		if(ret < 0)
		{
			perror("pthread_create failed");
			exit(EXIT_FAILURE);
		}
		//printf("pthread_create id : %d\n", (int)thread_id[i]);
	}
	
	/* Wait until all threads are ready to perform */
	wait_thread_status(-1, READY, &thread_cond1);

	/* Start measuring data for performance */
	gettimeofday(&T1,NULL);
	cpuUsage(START_CPU_CHECK);

	/* Send signal to all threads to start */
	signal_thread_status(-1, EXEC, &thread_cond2);

	/* Wait until all threads done */
	wait_thread_status(-1, END, &thread_cond3);

	cpuUsage(END_CPU_CHECK);
	gettimeofday(&T2,NULL);
	print_time(T1, T2);

	if(num_threads == 1)
	{
		print_con_switches();	
	}
	
	/* Join threads */
	for(i = 0; i < num_threads; i++)
	{
		ret = pthread_join(thread_id[i], &res);
		if(ret < 0)
		{
			perror("pthread_join failed");
			exit(EXIT_FAILURE);
		}
		free(res);
	}

}
