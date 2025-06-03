package org.example;

import java.util.Random;
import java.util.Scanner;

public class MaxOfArrayRand {
    static int maxOf(int[] a) {
        int max = a[0];
        for (int i = 1; i < a.length; i++) {
            if(a[i] > max) {
                max = a[i];
            }
        }
        return max;
    }
    public static void main(String[] args) {
        Random r = new Random();
        Scanner sc = new Scanner(System.in);

        System.out.println("find max of array");
        System.out.println("men : ");
        int number = sc.nextInt();

        int[] arr = new int[number];

        System.out.println("키값은 아래와 같다.");
        for(int i = 0; i < number; i++) {
            arr[i] =  100 + r.nextInt(100);
            System.out.println(arr[i]);
        }

        System.out.println(maxOf(arr));
    }
}
