int f = 4;
int r = 1;

for (int i = f; i > 0; i= i-1) {
    r = r * i;
    for (int j = 0; j < 10; j=j+1) {
        r = r + j;
    }

    if (r > 100) {
        r = r - 250;
    }

    else {
        r = r + 100;
    }
}