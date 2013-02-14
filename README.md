Mobile benchmark tool (mobibench)
================================

Written by Kisung Lee <kisunglee@hanyang.ac.kr> and Sooman Jeong <77smart@hanyang.ac.kr>
and Jungwoo Hwang <tearoses@hanyang.ac.kr>

We develop Mobibench to generate the workload, which
represents the smartphone IO. Mobibench generates sequential
and random IO requests to the storage device
in various IO modes; available IO modes are buffered
IO, O SYNC, fsync(), O DIRECT, memory mapped
IO, asynchronous memory mapped IO, and synchronous
memory mapped IO. It also supports multi-threading environment
and SQLite operations

In addition, Mobigench can replay IO trace which is captured by
MobiGen. This function was developed in order to properly analyze
IO characateristics of real applications.


Build shell version
--------------------
    # cd shell && make


Usage (shell version)
----------------------
	# mobibench [-p pathname] [-f file_size_Kb] [-r record_size_Kb] [-a access_mode] [-h]
                    [-y sync_mode] [-t thread_num] [-d db_mode] [-n db_transcations]
                    [-j SQLite_journalmode] [-s SQLite_syncmode] [-g replay_script] [-q]
                                     
                                     
* -p  set path name (default=./mobibench)
* -f  set file size in KBytes (default=1024)
* -r  set record size in KBytes (default=4)
* -a  set access mode (0=Write, 1=Random Write, 2=Read, 3=Random Read) (default=0)
* -y  set sync mode (0=Normal, 1=O_SYNC, 2=fsync, 3=O_DIRECT, 4=Sync+direct,
                     5=mmap, 6=mmap+MS_ASYNC, 7=mmap+MS_SYNC) (default=0)
* -t  set number of thread for test (default=1)
* -d  enable DB test mode (0=insert, 1=update, 2=delete)
* -n  set number of DB transaction (default=10)
* -j  set SQLite journal mode (0=DELETE, 1=TRUNCATE, 2=PERSIST, 3=WAL, 4=MEMORY, 
                               5=OFF) (default=1)
* -s  set SQLite synchronous mode (0=OFF, 1=NORMAL, 2=FULL) (default=2)
* -g  set replay script (output of MobiGen)
* -q  do not display progress(%) message                                                           			

