#include "closest_serial.h"
#include <string.h>
#include <stdlib.h>

double combine_lr(struct Point P[], size_t n, struct Point mid_point, double d)
{
	struct Point *area = malloc(sizeof(struct Point)*n);
	size_t num = 0;
	double result = d;
	for (size_t i = 0; i < n; i++){
		if (abs(P[i].x - mid_point.x) < d){
			area[num] = P[i];
			num++;
		}
	}
	qsort(area, num, sizeof(struct Point), compare_y);
	for (size_t i = 0; i < num; i++){
		size_t count = 1;
		while ((i + count) < num && area[i+count].y - area[i].y < result){
			if (dist(area[i], area[i+count]) < result){
				result = dist(area[i], area[i+count]);
			}
			count++;
		}
	}
	free(area);
	return result;
}

double _closest_serial(struct Point P[], size_t n)
{
	if (n <= 3){
		return brute_force(P, n);
	}
	else{
		size_t mid = n/2;      //left will have P[0]...P[mid-1], right will have P[mid]...P[n-1]
		struct Point *Pleft = malloc(sizeof(struct Point)*mid);
		struct Point *Pright = malloc(sizeof(struct Point)*(n-mid));
		memcpy(Pleft, P, sizeof(struct Point)*mid);
		memcpy(Pright, P+mid, sizeof(struct Point)*(n-mid));
		double left_result = _closest_serial(Pleft, mid);
		double right_result = _closest_serial(Pright, n-mid);
		double smaller;
		if (left_result > right_result){
			smaller = right_result;
		}
		else {
			smaller = left_result;
		}
		free(Pleft);
		free(Pright);
		double result = combine_lr(P, n, P[mid], smaller);
		return result;
	}
}

double closest_serial(struct Point P[], size_t n)
{
	qsort(P, n, sizeof(struct Point), compare_x);
	return _closest_serial(P, n);
}
