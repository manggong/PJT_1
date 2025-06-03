package org.example;

public class IntArray {
    public static void main(String[] args) {
        int[] arr = new int[5];

        arr[1] = 37;
        arr[2] = 42;

        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }
}
