package org.example;

import java.util.Scanner;

public class SumForPos {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n;

        do {
            System.out.println("number of n:");
            n = scanner.nextInt();
        } while (n <= 0);

        int sum = 0;

        for (int i = 0; i <= n; i++) {
            sum += i;
        }

        System.out.printf("sum = %d", sum);
    }
}
