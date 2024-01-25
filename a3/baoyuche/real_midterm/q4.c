/*
MIDTERM TEST QUESTION 4
Strings

6 marks

strncat is a difficult function to use correctly.
Dan suggests an alternative called dan_strcat.
You'll write the code for the dan_strcat function here.

dan_strcat takes three parameters:
*dst is the destination string,
*src is the source string, and
*size (careful here!) is the full size that has been allocated for the dst array.
dan_strcat is guaranteed to null-terminate dst.
It returns (careful again!) the number of characters, including the null-terminator,
that would have been in dst if there was sufficient space in dst to store it all.

Write dan_strcat below. You may use strlen,
but do NOT use any other string functions.
*/

#include <string.h>

size_t dan_strcat(char *dst, const char *src, size_t size) {
    // TODO your code here
	int alen = strlen(dst);
	int blen = strlen(src);
	if (size >= alen + blen + 1){
		for (int count = 0; count < blen + 1; count++){
			dst[alen + count] = src[count];
		}
		dst[alen + blen] = '\0';
		return strlen(dst) + 1;
	}
	else{
		return strlen(dst) + strlen(src) + 1;
	}


}

int main() {
    // Feel free to add test code here (not marked)
    
    return 0;
}
