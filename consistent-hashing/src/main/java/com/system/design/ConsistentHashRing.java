package com.system.design;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ConsistentHashRing {

    private final int replicas; // virtual nodes per physical node
    private final NavigableMap<Long, String> ring = new TreeMap<>();

    public ConsistentHashRing(int replicas) {
        this.replicas = replicas;
    }

    /* ---------------- HASH FUNCTION ---------------- */

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(key.getBytes());

            // Use first 8 bytes to form a positive long
            long hash = 0;
            for (int i = 0; i < 8; i++) {
                hash = (hash << 8) | (bytes[i] & 0xff);
            }
            return hash & 0x7fffffffffffffffL;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        return murmurHash64(key);
    }

    /* ---------------- ADD NODE ---------------- */

    public void addNode(String node) {
        for (int i = 0; i < replicas; i++) {
            String virtualNode = node + "#" + i;
            long hash = hash(virtualNode);
            ring.put(hash, node);
        }
    }

    /* ---------------- REMOVE NODE ---------------- */

    public void removeNode(String node) {
        for (int i = 0; i < replicas; i++) {
            String virtualNode = node + "#" + i;
            long hash = hash(virtualNode);
            ring.remove(hash);
        }
    }

    /* ---------------- LOOKUP ---------------- */

    public String getNode(String key) {
        if (ring.isEmpty()) {
            return null;
        }

        long hash = hash(key);

        // Find first node clockwise
        Map.Entry<Long, String> entry = ring.ceilingEntry(hash);

        // Wrap around if needed
        if (entry == null) {
            entry = ring.firstEntry();
        }

        return entry.getValue();
    }

    public static long murmurHash64(String key) {
        byte[] data = key.getBytes(StandardCharsets.UTF_8);
        long h1 = 0;

        for (byte b : data) {
            h1 ^= b;
            h1 *= 0x87c37b91114253d5L;
            h1 = Long.rotateLeft(h1, 31);
            h1 *= 0x4cf5ad432745937fL;
        }
        return h1 & Long.MAX_VALUE;
    }

}
