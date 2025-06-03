package org.example;

import java.util.Scanner;

public class SumWhile {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter number: ");
        System.out.println("n ~:");
        int n = scanner.nextInt();

        int sum = 0;
        int i = 0;

        while (i <= n) {
            sum += i;
            i++;
        }
        System.out.println(sum);
    }
}
