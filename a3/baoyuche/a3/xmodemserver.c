#include "xmodemserver.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/select.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include "crc16.h"
#include "helper.h"

#ifndef PORT
  #define PORT 56880
#endif

int main(){
	// create listening socket here
	int listen_soc = socket(AF_INET, SOCK_STREAM, 0);
	if (listen_soc == -1){
		perror("server: socket");
		exit(1);
	}
	
	// make listening socket reuseable
	int yes = 1;
	if((setsockopt(listen_soc, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int))) == -1){
		perror("setsockopt");
	}
	
	// initialize server and bind socket
	struct sockaddr_in addr;
	addr.sin_family = AF_INET;
	addr.sin_port = htons(PORT);
	memset(&addr.sin_zero, 0, 8);
	addr.sin_addr.s_addr = INADDR_ANY;
	if (bind(listen_soc, (struct sockaddr *) &addr, sizeof(struct sockaddr_in)) == -1){
		perror("server: bind");
		close(listen_soc);
		exit(1);
	}

	// start listening...
	if (listen(listen_soc, 10) < 0){
		perror("listen");
		close(listen_soc);
		exit(1);
	}

	// initialize fdset	
	fd_set fds;
	fd_set fds_cpy;
	FD_ZERO(&fds);
	int rdy;
	int max = listen_soc;  // this keeps track of the largest fd in the fd_set
	struct client *first;
	first = NULL;
	struct client *curr;
	int new;
	char temp;
	// add the listening socket in fdset
	FD_SET(listen_soc, &fds);

	while (1){	
		// reset the copy fdset
		memcpy(&fds_cpy, &fds, sizeof(fds));
	
		printf("Waiting on select..\n"); //TODO: delete this!

		// call select
		rdy = select(max + 1, &fds_cpy, NULL, NULL, NULL);
		if (rdy == -1){
			perror("select");
			close(listen_soc);
			exit(1);
		}

		printf("%d fd(s) are ready!\n", rdy); //TODO: delete this!
		
		// check if listen_soc is ready
		if (rdy > 0 && FD_ISSET(listen_soc, &fds_cpy)){
			new = accept(listen_soc, NULL, NULL);
			if (new == -1){
				perror("accept");
			}
			else{
				// create a new client
				struct client *new_client = malloc(sizeof(struct client));
				new_client->fd = new;
				new_client->inbuf = 0;
				new_client->state = initial;
				new_client->filename[0] = '\0';
				new_client->next = NULL;
				
				//add new_client to the end of the linked list
				if (first == NULL){
					new_client->prev = NULL;
					first = new_client;
				}
				else{
					curr = first;
				
					while (curr->next != NULL){
						curr = curr->next;
					}
					new_client->prev = curr;
					curr->next = new_client;
				}

				//add new_client's fd into fdset
				FD_SET(new, &fds);
				if (new > max){
					max = new;
				}
				printf("Accepted new client with fd %d\n", new); //TODO: delete this!
			}
			rdy--;
		}
		
		printf("%d fd(s) ready after checking listening socket.\n", rdy);

		// check if any client is ready
		curr = first;
		while (rdy > 0){

			if (FD_ISSET(curr->fd, &fds_cpy)){
				
				printf("currently at fd %d\n", curr->fd); //TODO: delete this!

				if (curr->state == initial){
				/* the client has not yet finished transfering its filename, 
				 * meaning that no '\r\n' is found in curr.filename.
				 *
				 * do a read call, null terminate curr.filename, check
				 * if '\r\n' is present.
				 *
				 * if '\r\n' is found, set up curr.fp, set curr.current_block to 1,
				 * write a "C" to curr.fd, set curr.state to pre_block.
				 */
					printf("getting filename..\n"); //TODO: delete this!
					int curr_len = strlen(curr->filename);
					int read_len = read(curr->fd, curr->filename + curr_len, (20 - curr_len));
					if (read_len == -1){
						// drop the client
						perror("read");
						curr->state = finish;
					}
					if ((curr_len + read_len) == 20){
						// filename too long, taking first 19 characters
						curr->filename[19] = '\0';
						curr->fp = open_file_in_dir(curr->filename, "filestore");
						if (curr->fp == NULL){
							perror("open");
							curr->state = finish;
						} else{
							curr->current_block = 1;
							write(curr->fd, "C", sizeof(char));
							curr->state = pre_block;
						}
					}
					else{
						curr->filename[curr_len + read_len] = '\0';
						
						// check if '\r\n' in curr.filename
						if (strstr(curr->filename, "\r\n") == NULL){
							// no '\r\n', do nothing
						}
						else{
							// found
							strstr(curr->filename, "\r\n")[0] = '\0';
							curr->fp = open_file_in_dir(curr->filename, "filestore");
							if (curr->fp == NULL){
								perror("open");
								curr->state = finish;
							} else{
								curr->current_block = 1;
								write(curr->fd, "C", sizeof(char));
								curr->state = pre_block;
							}
						}
					}
				}
				else if (curr->state == pre_block){
				// monitor the stream until a valid ASCII symbol is found
					while (read(curr->fd, &temp, 1) > 0){
						if (temp == SOH){
							// receive block (132)
							curr->blocksize = 132;
							curr->state = get_block;
							break;
						}
						else if (temp == EOT){
							// send ACK, set curr.state to finish to clean up
							write(curr->fd, "ACK", 1);
							curr->state = finish;

							printf("finished receiving %s.\n", curr->filename);

							break;
						}
						else if (temp == STX){
							// receive block (1028)
							curr->blocksize = 1028;
							curr->state = get_block;
							break;
						}
					}		
				}
				else if (curr->state == get_block){
					int bytes_read;
					bytes_read = read(curr->fd, curr->buf + curr->inbuf, curr->blocksize - curr->inbuf);
					if (bytes_read == -1 || bytes_read == 0){
						// error reading or client disconnects, go to finish state to clean up
						perror("read");
						curr->state = finish;
					}
					curr->inbuf += bytes_read;
					if (curr->inbuf == curr->blocksize){
						// has read the expected bytes, switching to check_block
						
						printf("readed the expected %d bytes payload for filename '%s' from fd %d\n", curr->blocksize - 4, curr->filename, curr->fd); //TODO: delete this!

						curr->state = check_block;
					}
					
				}
				if (curr->state == check_block){
					
					printf("checking block integrity..\n"); //TODO: delete this!

					unsigned char payload[curr->blocksize - 4];
					memcpy(payload, curr->buf + 2, curr->blocksize - 4);
					
					unsigned short crc = crc_message(XMODEM_KEY, payload, curr->blocksize - 4);
					unsigned char char_block1;
					unsigned char char_block2;
					char_block1 = crc >> 8;
					char_block2 = crc;

					// check if block number == 255 - inverse, drop client if not so

					if ((unsigned char)curr->buf[0] != (255 - (unsigned char)curr->buf[1])){
						curr->state = finish;
						printf("block number and inverse do not corresbond.\n"); //TODO: delete this!
					}

					// check if block number == curr.current_block - 1, send ACK if so
					// go to pre_block stage
					else if ((unsigned char)curr->buf[0] == (curr->current_block - 1)){
						temp = ACK;
						write(curr->fd, &temp, 1);
						curr->state = pre_block;
						curr->inbuf = 0;
						printf("duplicate block received.\n"); //TODO: delete this!
					}
					
					// check if block number != curr.current_block, drop if so
					else if ((unsigned char)curr->buf[0] != curr->current_block){
						curr->state = finish;
					}
					
					// check if CRC16 match, send NAK if not so
					// go to pre_block stage
					else if ((unsigned char)curr->buf[curr->blocksize - 2] != char_block1 || (unsigned char)curr->buf[curr->blocksize - 1] != char_block2){
						temp = NAK;
						write(curr->fd, &temp, 1);
						curr->state = pre_block;
						curr->inbuf = 0;
						printf("block corrupted. Receiving again..\n"); //TODO: delete this!
					}
					
					// else, write payload to curr.fp, increment curr.current_block,
					// send ACK, go to pre_block stage
					else{	
						fwrite(payload, sizeof(char), curr->blocksize - 4, curr->fp);
						curr->current_block++;
						temp = ACK;
						write(curr->fd, &temp, 1);
						curr->state = pre_block;
						curr->inbuf = 0;
						
						printf("writted to %s, going to accept next block\n", curr->filename); //TODO: delete this!
					}
				}
				if (curr->state == finish){
					// clean-ups happen here

					printf("cleaning up client with fd %d..\n", curr->fd);

					FD_CLR(curr->fd, &fds);
					close(curr->fd);
					if (curr->fp != NULL){
						if (fclose(curr->fp) != 0){
							perror("fclose");
						}
					}
					if (curr == first){
						first = curr->next;
						
					}
					else{
						curr->prev->next = curr->next;
						curr->next->prev = curr->prev;
					}
					struct client *junk = curr;
					if (curr->next != NULL){
						curr = curr->next;
					}
					free(junk);
					
					printf("done!\n\n");

				}

				rdy--;
			}
			else{
				if (curr->next != NULL){
					curr = curr->next;
				}
				else if (curr->next == NULL){
					fprintf(stderr, "This should not happen\n");
					break;
				}
			}
		}
		
	}

}
