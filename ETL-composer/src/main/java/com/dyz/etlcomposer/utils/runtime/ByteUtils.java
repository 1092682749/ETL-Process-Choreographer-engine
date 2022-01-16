package com.dyz.etlcomposer.utils.runtime;

public class ByteUtils {
    public static int bytes2Int(byte[] b, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = ((int) b[i]) & 0xff; // & 0xff
            n <<= (--len) * 8;
            sum = n + sum;
        }
        return sum;
    }

    public byte[] int2bytes(int value, int len) {
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[len - i - 1] = (byte) (value >> (8 * i) & 0xff);
        }
        return bytes;
    }

    public static void main(String[] args) {

        byte[] bytes = new byte[]{0, 0, -128};
        System.out.println(bytes2Int(bytes, 0, 3));
//        int i = bytes;
    }
}
