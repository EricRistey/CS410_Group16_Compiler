int b = 0;
int a = 1;
int c = 0;
int i = 0;

for (i = 0; i <= 5; i = i+1) {
    c = a + b;
    a = b;
    b = c;
}
