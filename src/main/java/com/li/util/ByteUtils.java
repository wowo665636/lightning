package com.li.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by wangdi on 18/3/1.
 */
public class ByteUtils {

    public  int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public  byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }


    public  byte[] longToBytes(ByteBuffer buffer, long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public  ByteBuffer bytesToLong(byte[] bytes) {
        ByteBuffer buffer =ByteBuffer.allocate(bytes.length);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer;
    }
    /**
     * 利用 {@link java.nio.ByteBuffer}实现byte[]转long
     * @param input
     * @param offset
     * @param littleEndian 输入数组是否小端模式
     * @return
     */
    public static long bytesToLong(byte[] input, int offset, boolean littleEndian) {
        // 将byte[] 封装为 ByteBuffer
        ByteBuffer buffer = ByteBuffer.wrap(input,offset,8);
        if(littleEndian){
            // ByteBuffer.order(ByteOrder) 方法指定字节序,即大小端模式(BIG_ENDIAN/LITTLE_ENDIAN)
            // ByteBuffer 默认为大端(BIG_ENDIAN)模式
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        return buffer.getLong();
    }

    /**
     * 将字节数组转为long<br>
     * 如果input为null,或offset指定的剩余数组长度不足8字节则抛出异常
     * @param input
     * @param offset 起始偏移量
     * @param littleEndian 输入数组是否小端模式
     * @return
     */
    public static long longFrom8Bytes(byte[] input, int offset, boolean littleEndian){
        long value=0;
        // 循环读取每个字节通过移位运算完成long的8个字节拼装
        for(int  count=0;count<8;++count){
            int shift=(littleEndian?count:(7-count))<<3;
            value |=((long)0xff<< shift) & ((long)input[offset+count] << shift);
        }
        return value;
    }


    public static void main(String[] args) {
        ByteUtils byteUtils = new ByteUtils();
        byte[] b = "repeatbi_trend_v0972447a0d7670560_0_0_1519910477456".getBytes();
        byte[] b2 = "repeatbi_trend_v01cdd155a54fb6387_0_0_1519910471517".getBytes();
        long a1 = longFrom8Bytes(b,0,false);
        long a2 = longFrom8Bytes(b2,0,false);

        long long_key = byteUtils.bytesToLong(b).getLong();
        long long_key1 = byteUtils.bytesToLong(b2).getLong();

        System.out.println(a1);
        System.out.println(a2);

    }
}
