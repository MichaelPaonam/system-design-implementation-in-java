package com.system.design;

import java.util.HashMap;
import java.util.Map;

public class TestConsistentHash {

    public static void main(String[] args) {

        ConsistentHashRing ring = new ConsistentHashRing(100);

        ring.addNode("NodeA");
        ring.addNode("NodeB");
        ring.addNode("NodeC");

        Map<String, Integer> distribution = new HashMap<>();

        for (int i = 0; i < 10_000; i++) {
            String key = "user:" + i;
            String node = ring.getNode(key);
            distribution.put(node, distribution.getOrDefault(node, 0) + 1);
        }

        distribution.forEach((node, count) ->
                System.out.println(node + " -> " + count));
    }
}
