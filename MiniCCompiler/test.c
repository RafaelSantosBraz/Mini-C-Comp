#define x 10

int a = 10;

int main(int b){
    b = 42;
    int c = a + b;
    for (int i = 0; i < 10; i++){
        printf("%d\n", c);
    }
    return 0;
}
