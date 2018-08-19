
#CROSS=arm-none-linux-gnueabi-
#CROSS=

EXENAME = mobibench

SRCS = mobibench.c \
	  sqlite3.c

INSTALL = install

prefix = /usr/local
bindir = $(prefix)/bin

CFLAGS = -lpthread -ldl

#CFLAGS += -DDEBUG_SCRIPT

#for sqltie3
CFLAGS += -DNDEBUG=1 \
     -DHAVE_USLEEP=1 \
     -DSQLITE_HAVE_ISNAN \
     -DSQLITE_THREADSAFE=2 \
     -DSQLITE_TEMP_STORE=3 \
     -DSQLITE_POWERSAFE_OVERWRITE=1 \
     -DSQLITE_DEFAULT_FILE_FORMAT=4 \
     -DSQLITE_DEFAULT_AUTOVACUUM=1 \
     -DSQLITE_ENABLE_MEMORY_MANAGEMENT=1 \
     -DSQLITE_ENABLE_FTS3 \
     -DSQLITE_ENABLE_FTS3_BACKWARDS \
     -DSQLITE_ENABLE_FTS4 \
     -DSQLITE_OMIT_BUILTIN_TEST \
     -DSQLITE_OMIT_COMPILEOPTION_DIAGS \
     -DSQLITE_OMIT_LOAD_EXTENSION \
     -DSQLITE_DEFAULT_FILE_PERMISSIONS=0666 \
     -DUSE_PREAD64 \
     --static

all :
	$(CROSS)gcc -o $(EXENAME) $(SRCS) $(CFLAGS)

clean :
	@rm -rf mobibench

install : $(EXENAME)
	@$(INSTALL) -m 755 -d $(DESTDIR)$(bindir)
	@$(INSTALL) $(EXENAME) $(DESTDIR)$(bindir)
