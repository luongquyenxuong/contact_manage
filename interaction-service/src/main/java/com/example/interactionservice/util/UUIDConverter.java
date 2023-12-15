package com.example.interactionservice.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDConverter {
    public static UUID bytesToUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long mostSignificantBits = byteBuffer.getLong();
        long leastSignificantBits = byteBuffer.getLong();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }

}
