/**
 * 
 */
package datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.Block;
import utils.Buffer;

/**
 * @author standingby
 *
 */
public class HashBucket {
    Map<Integer, List<Reference>> hashBuckets = new HashMap<>();

    /**
     * 
     */
    public HashBucket(List<Integer> addrList, Buffer buffer) {
        int basicIO = buffer.getIOCounter();
        Block input;

        for (Integer integer : addrList) {
            input = buffer.readBlockFromDisk(integer);

            for (int i = 0; i < 7; i++) {
                int key = hashFunc(input.data[i * 2]);
                if (hashBuckets.containsKey(key)) {
                    hashBuckets.get(key).add(new Reference(integer, i * 2));
                } else {
                    hashBuckets.put(key,
                            new ArrayList<>(Arrays.asList(new Reference(integer, i * 2))));
                }
            }

            buffer.freeBlockInBuffer(input);
        }
        buffer.free();
        System.out
                .println("Construct Hash Buckets with I/O : " + (buffer.getIOCounter() - basicIO));
    }

    public Map<Integer, List<Reference>> getHashBuckets() {
        return this.hashBuckets;
    }

    public static int hashFunc(int key) {
        return key % 60;
    }

}
