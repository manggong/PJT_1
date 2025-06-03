package org.example;

public class Max3m {
    static int max3m(int a, int b, int c) {
        int max = a;
        if (b > max) {
            max = b;
        }
        if (c > max) {
            max = c;
        }
        return max;
    }
    public static void main(String[] args) {
        System.out.println(max3m(3,2,1));
    }

}
