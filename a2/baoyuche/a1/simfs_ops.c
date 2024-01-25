/* This file contains functions that are not part of the visible "interface".
 * They are essentially helper functions.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "simfs.h"

/* Internal helper functions first.
 */

FILE *
openfs(char *filename, char *mode)
{
    FILE *fp;
    if((fp = fopen(filename, mode)) == NULL) {
        perror("openfs");
        exit(1);
    }
    return fp;
}

void
closefs(FILE *fp)
{
    if(fclose(fp) != 0) {
        perror("closefs");
        exit(1);
    }
}

/* File system operations: creating, deleting, reading, and writing to files.
 */

/* Create an empty file with name specified by newfile using the first available fentry.
 * The file name newfile should have at most 11 characters.
 */
void createfile(char *filename, char *newfile){
	if (strlen(newfile) > 11){
		fprintf(stderr, "Error: file name too long\n");
		exit(1);
	}
	FILE *fs = openfs(filename, "r+");
	fentry file;
	int err = 0;
	for (int i = 0; i < MAXFILES; i++){
		fread(&file, sizeof(fentry), 1, fs);
		if (strcmp(newfile, file.name) == 0){
			fprintf(stderr, "Error: Duplicated file name\n");
			closefs(fs);
			exit(1);
		}
	}
	rewind(fs);
	for (int i = 0; i < MAXFILES; i++){
		fread(&file, sizeof(fentry), 1, fs);
		if (file.name[0] == '\0'){
			strcpy(file.name, newfile);
			err = 1;
			break;
		}
	}
	fseek(fs, -(sizeof(fentry)), SEEK_CUR);
	fwrite(&file, sizeof(fentry), 1, fs);
	if (err == 0){
		fprintf(stderr, "Error: No available fentry\n");
	}
	closefs(fs);
}

/* Delete a file with name removefile that has been previously added to the filesystem filename,
 * while also clearing all data blocks it occupied.
 */
void deletefile(char* filename, char *removefile){	
	FILE *fs = openfs(filename, "r+");
	fentry file;
	fnode node;
	int find = 1;
	//int currindex = 0;
	int nextindex;
	int indexarray[MAXBLOCKS];
	int count = 0;
	for (int i = 0; i < MAXFILES; i++){
		fread(&file, sizeof(fentry), 1, fs);
		if (strcmp(removefile, file.name) == 0){
			find = 0;
			nextindex = file.firstblock;
			file.name[0] = '\0';
			file.size = 0;
			file.firstblock = -1;
			fseek(fs, -sizeof(fentry), SEEK_CUR);
			fwrite(&file, sizeof(fentry), 1, fs);
		}
	}
	if (find == 1){
		fprintf(stderr, "Error: file does not exist");
		closefs(fs);
		exit(1);
	}
	while (nextindex != -1){
		fseek(fs, MAXFILES * sizeof(fentry) + nextindex * sizeof(fnode), SEEK_SET);
		fread(&node, sizeof(fnode), 1, fs);
		indexarray[count] = node.blockindex;
	//	currindex = node.blockindex;
		nextindex = node.nextblock;
		count++;
	}
	char zerobuf[BLOCKSIZE] = {0};
	for (int i = 0; i < count; i++){
		fseek(fs, indexarray[i] * BLOCKSIZE, SEEK_SET);
		if (fwrite(zerobuf, BLOCKSIZE, 1, fs) < 1){
			fprintf(stderr, "Error: clear data failed when deleting");
			closefs(fs);
			exit(1);
		}
		fseek(fs, MAXFILES * sizeof(fentry) + indexarray[i] * sizeof(fnode), SEEK_SET);
		fread(&node, sizeof(fnode), 1, fs);
		node.blockindex = -node.blockindex;
		node.nextblock = -1;
		fseek(fs, -sizeof(fnode), SEEK_CUR);
		fwrite(&node, sizeof(fnode), 1, fs);
	}
}

/* Write length long of data to the file with name filename in filesystem, beginning with offset start.
 */
void writefile(char *filesystem, char *filename, int start, int length){
	FILE *fs = openfs(filesystem, "r+");
	char user_input[length];
	int char_written = 0;
	char format[99];
	sprintf(format, "%%%dc", length);
	fentry file;
	int fileindex;
	fnode curr_node;
	int next_block;
	int find = 1;

	for (fileindex = 0; fileindex < MAXFILES; fileindex++){
		fread(&file, sizeof(fentry), 1, fs);
		if (strcmp(filename, file.name) == 0){
			next_block = file.firstblock;
			find = 0;
			break;
		}
	}
	if (find == 1){
		fprintf(stderr, "Error: file does not exist\n");
		closefs(fs);
		exit(1);
	}
	if (start > file.size){
		fprintf(stderr, "Error: Out of Range - writing start point not allowed\n");
		closefs(fs);
		exit(1);
	}
	if (scanf(format, user_input) == EOF){
		fprintf(stderr, "Error: failure reading user input\n");
		closefs(fs);
		exit(1);
	}

	file.size += length;
	fseek(fs, fileindex * sizeof(fentry), SEEK_SET);
	fwrite(&file, sizeof(fentry), 1, fs);   //undate fentry info
	if (next_block == -1){				//if the file is empty
		next_block = find_unused_block(fs);
		file.firstblock = next_block;
		fseek(fs, fileindex * sizeof(fentry), SEEK_SET);
		fwrite(&file, sizeof(fentry), 1, fs);   //undate fentry info
		fseek(fs, BLOCKSIZE * next_block, SEEK_SET);
		if (length - char_written >= BLOCKSIZE){
		char_written += fwrite(user_input + char_written, sizeof(user_input[0]), BLOCKSIZE, fs);
		} else {
		char_written += fwrite(user_input + char_written, sizeof(user_input[0]), length - char_written, fs);
		}
		fseek(fs, MAXFILES * sizeof(fentry) + next_block * sizeof(fnode), SEEK_SET);
		fread(&curr_node, sizeof(fnode), 1, fs);
	}else {
		fseek(fs, MAXFILES * sizeof(fentry) + next_block * sizeof(fnode), SEEK_SET);
		while (start >= BLOCKSIZE){
			start -= BLOCKSIZE;
			fseek(fs, MAXFILES * sizeof(fentry) + next_block * sizeof(fnode), SEEK_SET);
			fread(&curr_node, sizeof(fnode), 1, fs);
			next_block = curr_node.nextblock;
		}
		while (char_written != length){
			if (next_block == -1){
				break;
			} else {
				fseek(fs, next_block * BLOCKSIZE + start, SEEK_SET);
				if (length - char_written >= BLOCKSIZE - start){
				char_written += fwrite(user_input + char_written, sizeof(user_input[0]), BLOCKSIZE - start, fs);
				} else {
				char_written += fwrite(user_input + char_written, sizeof(user_input[0]), length - char_written, fs);
				}
				start = 0;
				fseek(fs, MAXFILES * sizeof(fentry) + next_block * sizeof(fnode), SEEK_SET);
				fread(&curr_node, sizeof(fnode), 1, fs);
				next_block = curr_node.nextblock;
			}
		}
	}
	//enter here if need to find new block after first time
	while(char_written != length){
		next_block = find_unused_block(fs);
		curr_node.nextblock = next_block;
		fseek(fs, MAXFILES * sizeof(fentry) + curr_node.blockindex * sizeof(fnode), SEEK_SET);
		fwrite(&curr_node, sizeof(fnode), 1, fs);
		fseek(fs, next_block * BLOCKSIZE, SEEK_SET);
		if (length - char_written >= BLOCKSIZE){
		char_written += fwrite(user_input + char_written, sizeof(user_input[0]), BLOCKSIZE, fs);
		} else {
		char_written += fwrite(user_input + char_written, sizeof(user_input[0]), length - char_written, fs);
		}
		fseek(fs, MAXFILES * sizeof(fentry) + next_block * sizeof(fnode), SEEK_SET);
		fread(&curr_node, sizeof(fnode), 1, fs);
	}
	closefs(fs);
}

/* Helper function for writefile that finds the first available fnode,
 * initialize the corresponding block with zeros, and returns its index.
 */
int find_unused_block(FILE *fs){
	fnode curr_node;
	int find = 1;
	fseek(fs, MAXFILES * sizeof(fentry), SEEK_SET);
	for (int i = 0; i < MAXBLOCKS; i++){
		fread(&curr_node, sizeof(fnode), 1, fs);
		if (curr_node.blockindex < 0){
			curr_node.blockindex = -curr_node.blockindex;
			fseek(fs, -sizeof(fnode), SEEK_CUR);
			fwrite(&curr_node, sizeof(fnode),1 , fs);    //update fnode info
			find = 0;
			break;
		}
	}
	if (find == 1){
		fprintf(stderr, "Error: write failed. Filesystem storage full\n");
		closefs(fs);
		exit(1);
	}
	fseek(fs, BLOCKSIZE * curr_node.blockindex, SEEK_SET);
	char zerobuf[BLOCKSIZE] = {0};
	if (fwrite(zerobuf, BLOCKSIZE, 1, fs) < 1){
		fprintf(stderr, "Error: failed initialing block with zeroes\n");	
		closefs(fs);
		exit(1);
	}
	return curr_node.blockindex;
}

/* Read from the file specified by filename in filesystem, starting at offset start.
 * The amound of data read should be length bytes, and is printed to stdout.
 */
void readfile(char *filesystem, char *filename, int start, int length){
	FILE *fs = openfs(filesystem, "r+");
	char output[length+1];
	output[length] = '\0';

	fentry file;
	fnode curr_node;
	int next_block;
	int find = 1;
	int bytes_read = 0;

	for (int fileindex = 0; fileindex < MAXFILES; fileindex++){
		fread(&file, sizeof(fentry), 1, fs);
		if (strcmp(filename, file.name) == 0){
			next_block = file.firstblock;
			find = 0;
			break;
		}
	}
	if (find == 1){
		fprintf(stderr, "Error: file does not exist\n");
		closefs(fs);
		exit(1);
	}
	if (start > file.size){
		fprintf(stderr, "Error: Out of Range - reading start point not allowed\n");
		closefs(fs);
		exit(1);
	}
	if ((start + length) > file.size){
		fprintf(stderr, "Error: Out of Range - reading endpoint exceeds file size\n");
		closefs(fs);
		exit(1);
	}
	
	while (start >= BLOCKSIZE){
		start -= BLOCKSIZE;
		fseek(fs, MAXFILES * sizeof(fentry) + next_block * sizeof(fnode), SEEK_SET);
		fread(&curr_node, sizeof(fnode), 1, fs);
		next_block = curr_node.nextblock;
	}
	fseek(fs, BLOCKSIZE * next_block + start, SEEK_SET);
	if (length < BLOCKSIZE - start){
		bytes_read += fread(output, 1, length, fs);
	} else {
		bytes_read += fread(output, 1, BLOCKSIZE - start, fs);
	}

	while (bytes_read != length){
		fseek(fs, MAXFILES * sizeof(fentry) + next_block * sizeof(fnode), SEEK_SET);
		fread(&curr_node, sizeof(fnode), 1, fs);
		next_block = curr_node.nextblock;
		fseek(fs, BLOCKSIZE * next_block, SEEK_SET);
		if ((length - bytes_read) < BLOCKSIZE){
			bytes_read += fread(output + bytes_read, 1, length - bytes_read, fs);
		} else{
			bytes_read += fread(output + bytes_read, 1, BLOCKSIZE, fs);
		}
	}
	printf("%s", output);
	closefs(fs);
}
// Signatures omitted; design as you wish.
