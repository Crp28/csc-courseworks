HELP = crc16.o helper.o
DEPENDENCIES = xmodemserver.h crc16.h helper.h
PORT=56808
CFLAGS= -DPORT=\$(PORT) -g -Wall -Werror -fsanitize=address


all : xmodemserver client1

xmodemserver : ${HELP} xmodemserver.o
	gcc ${CFLAGS} -o $@ $^

client1 : ${HELP} client1.o
	gcc ${CFLAGS} -o $@ $^

%.o : %.c ${DEPENDENCIES}
	gcc ${CFLAGS} -c $<

clean:
	rm -f *.o xmodemserver client1
