#include "closest_helpers.h"
#include <math.h>

int verbose = 0;

int compare_x(const void* a, const void* b) 
{
    const struct Point *ap = a;
    const struct Point *bp = b;
    return ap->x - bp->x;
} 

int compare_y(const void* a, const void* b) 
{ 
    const struct Point *ap = a;
    const struct Point *bp = b;
    return ap->y - bp->y;
} 

double dist(struct Point p1, struct Point p2) 
{
    double p1x = (double)p1.x;
    double p1y = (double)p1.y;
    double p2x = (double)p2.x;
    double p2y = (double)p2.y;
    return sqrt(pow(p1x-p2x, 2) + pow(p1y-p2y, 2));
} 
