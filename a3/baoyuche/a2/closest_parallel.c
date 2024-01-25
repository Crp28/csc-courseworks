#include "closest_parallel.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
int curr_depth = 0;

double _closest_parallel(struct Point P[], size_t n, int pdmax, int *pcount)
{
    static int num_forks = 0;
    	if (n <= 3 || pdmax == 0){
		return _closest_serial(P, n);
	}
	else{
		size_t mid = n/2;
		struct Point *Pleft = malloc(sizeof(struct Point)*mid);
		struct Point *Pright = malloc(sizeof(struct Point)*(n-mid));
		memcpy(Pleft, P, sizeof(struct Point)*mid);
		memcpy(Pright, P+mid, sizeof(struct Point)*(n-mid));
		double left_result;
		double right_result;
		//starting left fork
		int lfd[2];
		pipe(lfd);
		int idl = fork();
		if (idl < 0) {
			perror("fork");
			exit(1);
		}
		else if (idl == 0){
			//child
			curr_depth++;
			close(lfd[0]);   //close reading end
			double returnvalue = _closest_parallel(Pleft, mid, pdmax - 1, pcount);
			if (pdmax - 1 > 0){
				num_forks += 2;
			}
			if (write(lfd[1], &returnvalue, sizeof(double)) == -1){
				perror("write to pipe");
				exit(1);
			}
			close(lfd[1]);

			exit(num_forks);
		}

		//finishing left fork
		
		//starting right fork

		int rfd[2];
		pipe(rfd);
		int idr = fork();
		if (idr < 0) {
			perror("fork");
			exit(1);
		}
		else if (idr == 0){
			//child
			curr_depth++;
			close(rfd[0]);   //close reading end
			double returnvalue = _closest_parallel(Pright, n - mid, pdmax - 1, pcount);
			if (pdmax - 1 > 0){
				num_forks += 2;
			}
			if (write(rfd[1], &returnvalue, sizeof(double)) == -1){
				perror("write to pipe");
				exit(1);
			}
			close(rfd[1]);

			exit(num_forks);
		}
		//finishing right fork
		

		else if (idl > 0 && idr > 0){
			//parent
			free(Pleft);
			free(Pright);
			close(lfd[1]);   //close writing end
			close(rfd[1]);
			int l_readed;
			int r_readed;
			while (1){
				l_readed = read(lfd[0], &left_result, sizeof(double));
				r_readed = read(rfd[0], &right_result, sizeof(double));
				if (l_readed == 0 && r_readed == 0){
					break;
				}
				else if (l_readed == -1 || r_readed == -1){
					perror("read");
					exit(1);
				}
			}
			int l_status;
			int r_status;
			waitpid(idl, &l_status, 0);
			waitpid(idr, &r_status, 0);
			if (WIFEXITED(l_status) == 1 && WIFEXITED(r_status) == 1){
				if (WEXITSTATUS(l_status) == 1 || WEXITSTATUS(r_status) == 1){
					exit(1);
				}
				else{
					num_forks += WEXITSTATUS(l_status);
					num_forks += WEXITSTATUS(r_status);
				}
			} else{
				exit(1);
			}
			*pcount = num_forks + 2;
			double smaller;
			if (left_result > right_result){
				smaller = combine_lr(P, n, P[mid], right_result);
			}
			else{
				smaller = combine_lr(P, n, P[mid], left_result);
			}
			return smaller;
		}
	}
	
	return 0.0;
}

double closest_parallel(struct Point P[], size_t n, int pdmax, int *pcount)
{
    	qsort(P, n, sizeof(struct Point), compare_x);
	return _closest_parallel(P, n, pdmax, pcount);
	return 0.0;
}
