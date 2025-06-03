package org.example;

import java.util.Scanner;

public class SumFor {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter number: ");
        System.out.println("n ~:");
        int n = scanner.nextInt();

        int sum = 0;
        int i = 0;

       for (i = 1; i <= n; i++) {
           sum += i;
       }
        System.out.println(sum);
    }
}
