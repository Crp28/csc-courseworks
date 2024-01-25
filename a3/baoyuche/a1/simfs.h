#include <stdio.h>
#include "simfstypes.h"

/* File system operations */
void printfs(char *);
void initfs(char *);
void createfile(char *, char *);
void deletefile(char *, char *);
void writefile(char *, char *, int, int);
int find_unused_block(FILE *);
void readfile(char *, char *, int, int);

/* Internal functions */
FILE *openfs(char *filename, char *mode);
void closefs(FILE *fp);
