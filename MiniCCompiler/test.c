#include <stdio.h>

#define X "as"

int teste(int b){
    if (X == "as"){
        int aux;
        scanf("%d", &aux);
        return aux;
    } else if (4 != 10){
       return 10;
    }
}

int main(){  
    int a = 4;
    int x = teste(teste(a));
    return 0;
}