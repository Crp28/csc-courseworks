#include "closest_brute.h"

double brute_force(struct Point P[], size_t n) 
{ 
	double close = dist(P[0], P[1]);
	for (size_t i = 0; i < n; i++){
		for (size_t j = 0; j < n; j++){
			if (i != j && dist(P[i], P[j]) < close){
				close = dist(P[i], P[j]);
			}
		}
	}
	
	return close; 
} 
