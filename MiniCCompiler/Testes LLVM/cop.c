#include <stdio.h>

#define C 3


int teste(int a[], int t)
{
    const int x = 10;
    const int *y = &x;
    //*y = 11;
    printf("%d %d\n", *y, x);
    for (int c = 0; c < t; c++){
        printf("%d\n", a[c]);
    }
}

int func(double a){
    return 0;
}

int abc(){
    func('a');
    char x = 10 + 'a' + 1.0;
    return 10;
}

int main()
{
    int zz[2][abc() + 1];
    int ***z;
    int y = 1;
    int *x = &y;
    teste(x, 2);
    return 0;
}