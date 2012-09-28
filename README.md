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


Build
-----
    # arm-none-linux-gnueabi-gcc -o mobibench mobibench.c -lpthread


Usage
-----
    # mobibench File size, reclen, AccessMode, Sync Mode, ThreadNum
    # mobibench 1024, 4, 2, 2, 10
    
* File size : size of file(KByte)
* reclen : size of reclen(KByte)
* AccessMode : 0:WRITE, 1:REWRITE, 2:RANDOM
* Sync Mode : 0:Normal, 1:O_SYNC, 2:fsync, 3:O_DIRECT, 4:Sync+direct, 5:mmap, 6:mmap+MS_ASYNC, 7:mmap+MS_SYNC
* ThreadNum : # of Threads


2012-09-24, Kisung Lee <kisunglee@hanyang.ac.kr> and Sooman Jeong <77smart@hanyang.ac.kr>
