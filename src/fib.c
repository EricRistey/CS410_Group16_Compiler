int a = 0;
int b = 1;

for (int i = 0; i < 10; i=i+1) {
    int c = a + b;
    a = b;
    b = c;
}

RESULT = a;