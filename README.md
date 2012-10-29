Mobile benchmark tool (mobibench)
================================

Written by Kisung Lee <kisunglee@hanyang.ac.kr> and Sooman Jeong <77smart@hanyang.ac.kr>

We develop Mobibench to generate the workload, which
represents the smartphone IO. Mobibench generates sequential
and random IO requests to the storage device
in various IO modes; available IO modes are buffered
IO, O SYNC, fsync(), O DIRECT, memory mapped
IO, asynchronous memory mapped IO, and synchronous
memory mapped IO. It also supports multi-threading environment
and SQLite operations


Build
-----
    # cd shell && make


Usage
-----
	# mobibench [-p pathname] [-f file_size_Kb] [-r record_size_Kb] [-a access_mode] [-h]
                [-y sync_mode] [-t thread_num] [-d db_mode]
 
* -p  set path name
* -f  set file size in KBytes (default=1024)
* -r  set record size in KBytes (default=4)
* -a  set access mode (0=Write, 1=Random Write, 2=Read, 3=Random Read)
* -y  set sync mode (0=Normal, 1=O_SYNC, 2=fsync, 3=O_DIRECT, 4=Sync+direct,
                     5=mmap, 6=mmap+MS_ASYNC, 7=mmap+MS_SYNC)
* -t  set number of thread for test (default=1)
* -d  enable DB test mode (0=insert, 1=update, 2=delete)
* -n  set number of DB transaction


2012-09-24, Kisung Lee <kisunglee@hanyang.ac.kr> and Sooman Jeong <77smart@hanyang.ac.kr>
