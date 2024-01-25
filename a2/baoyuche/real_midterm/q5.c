/*
MIDTERM TEST QUESTION 5
Assignment 1

4 marks

A simfs file system is totally messed up (TMU)
if the same block is used multiple times within a file or across multiple files.

For example, if two different files both use block 5, then the file system is TMU.

Write code to solve the below task.
*/

#include <stdio.h>
#include <stdlib.h>
#include "simfs.h"


/* Print whether simfs filename provided on commandline is
*  Totally Messed Up or not.
*
*  Output 'TMU' if the simfs filename is Totally Messed Up;
*  otherwise, output 'not TMU'.
*/

int main(int argc, char *argv[]) {

    // TODO your code here
	FILE *fs = openfs(argv[1], "r");
	int checked[MAXBLOCKS+1] = {0};
	fentry curr_file;
	fnode curr_node;
	int index;
	for (int filenum = 0; filenum < MAXFILES; filenum++){
		fseek(fs, filenum * sizeof(fentry), SEEK_SET);
		fread(&curr_file, sizeof(fentry), 1, fs);
		index = curr_file.firstblock;
		if (index != -1){
			if (checked[index] == 0){
				checked[index] = index;
				while (index != -1){
					fseek(fs, MAXFILES * sizeof(fentry) + index * sizeof(fnode), SEEK_SET);
					fread(&curr_node, sizeof(fnode), 1, fs);
					index = curr_node.nextblock;
					if (index != -1){
						if (checked[index] == 0){
							checked[index] = index;
						} else{
							printf("TMU");
							closefs(fs);
							exit(0);
						}
					}
				}
			} else{
				printf("TMU");
				closefs(fs);
				exit(0);
			}
		}
	}
	printf("not TMU");
	closefs(fs);
	return 0;
}
