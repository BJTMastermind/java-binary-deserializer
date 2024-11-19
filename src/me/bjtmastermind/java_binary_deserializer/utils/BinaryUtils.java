package me.bjtmastermind.java_binary_deserializer.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class BinaryUtils {

    public static String toHex(int val) {
        return toHex(val, 8);
    }

    public static String toHex(int val, int nbits) {
        return Integer.toHexString((val + (1 << nbits)) % (1 << nbits));
    }

    public static int find(ByteBuffer input, byte[] pattern) {
        for (int i = 0; i <= input.capacity() - pattern.length; i++) {
            boolean found = true;
            for (int j = 0; j < pattern.length; j++) {
                if (input.get(i + j) != pattern[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            }
        }
        return -1;
    }

    public static Object loads(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            ois.close();
            return obj;
        }
    }

    public static byte[] dumps(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            return baos.toByteArray();
        }
    }
}
