/**
 * 
 */
package operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import bplustree.BPlusTree;
import bplustree.Reference;
import process.Calculator;
import utils.Block;
import utils.Buffer;
import utils.ExtMem;

/**
 * @author standingby
 *
 */
public class Selector {
    private final Buffer buffer;

    public Selector(Buffer buffer) {
        super();
        this.buffer = buffer;
    }

    public List<Integer> linearSelect(List<Integer> addrList, String relation, int target) {
        buffer.free();
        int basicIO = buffer.getIOCounter();
        List<Integer> result = new ArrayList<>();
        // 基址
        int base = relation.equals("R") ? Calculator.RBASE : Calculator.SBASE;
        base += Calculator.SELECTBASE + 10000;
        Block output = buffer.getNewBlockInBuffer();
        Block input;

        for (Integer addr : addrList) {
            input = buffer.readBlockFromDisk(addr);
            for (int j = 0; j < 7; j++) {
                if (input.data[j * 2] == target) {
                    output.writeData(input.data[j * 2]);
                    output.writeData(input.data[j * 2 + 1]);
                    if (output.isFull()) {
                        buffer.writeBlockToDisk(output, base);
                        result.add(base);
                        buffer.freeBlockInBuffer(output);
                        output = buffer.getNewBlockInBuffer();
                        base++;
                    }
                }
            }
            buffer.freeBlockInBuffer(input);
        }
        if (!output.isEmpty()) {
            buffer.writeBlockToDisk(output, base);
            result.add(base);
        }
        buffer.freeBlockInBuffer(output);

        System.out.println(
                relation + " : Linear Select with I/O : " + (buffer.getIOCounter() - basicIO));
        return result;
    }

    public List<Integer> binarySearch(List<Integer> sortedBlockAddrs, String relation, int target) {
        buffer.free();
        int basicIO = buffer.getIOCounter();
        List<Integer> result = new ArrayList<>();
        // 基址
        int base = relation.equals("R") ? Calculator.RBASE : Calculator.SBASE;
        base += Calculator.SELECTBASE + 20000;

        Block output = buffer.getNewBlockInBuffer();
        Block midBlock;

        // 二分搜索
        int head = 0;
        int tail = sortedBlockAddrs.size() - 1;
        int mid;

        while (true) {
            mid = (head + tail + 1) / 2;
            midBlock = buffer.readBlockFromDisk(sortedBlockAddrs.get(mid));

            int midMin = midBlock.data[0];
            int midMax = midBlock.data[12];

            if (target < midMin) {
                tail = mid - 1;;

            } else if (target > midMax) {
                head = mid + 1;

            } else {
                int cur = mid;
                Block input = buffer.getNewBlockInBuffer();
                // 在当前块内搜索，以及前后向搜索
                if (target == midMin) {
                    // 前一个磁盘块搜索
                    while (--cur >= head) {
                        input = buffer.readBlockFromDisk(sortedBlockAddrs.get(cur));
                        for (int i = 6; i >= 0; i--) {
                            if (input.data[i * 2] != target) {
                                break;
                            }
                            output.writeData(input.data[i * 2]);
                            output.writeData(input.data[i * 2 + 1]);
                            if (output.isFull()) {
                                buffer.writeBlockToDisk(output, base);
                                result.add(base);
                                buffer.freeBlockInBuffer(output);
                                output = buffer.getNewBlockInBuffer();
                                base++;
                            }
                        }
                        buffer.freeBlockInBuffer(input);
                    }

                }
                if (target == midMax) {
                    // 后一个磁盘块搜索
                    while (++cur <= tail) {
                        input = buffer.readBlockFromDisk(sortedBlockAddrs.get(cur));
                        for (int i = 0; i < 7; i++) {
                            if (input.data[i * 2] != target) {
                                break;
                            }
                            output.writeData(input.data[i * 2]);
                            output.writeData(input.data[i * 2 + 1]);
                            if (output.isFull()) {
                                buffer.writeBlockToDisk(output, base);
                                result.add(base);
                                buffer.freeBlockInBuffer(output);
                                output = buffer.getNewBlockInBuffer();
                                base++;
                            }
                        }
                        buffer.freeBlockInBuffer(input);
                    }
                }
                // 当前磁盘块搜索
                for (int i = 0; i < 7; i++) {
                    if (target > midBlock.data[i * 2]) {
                        continue;
                    }
                    if (target < midBlock.data[i * 2]) {
                        break;
                    }
                    output.writeData(midBlock.data[i * 2]);
                    output.writeData(midBlock.data[i * 2 + 1]);
                    if (output.isFull()) {
                        buffer.writeBlockToDisk(output, base);
                        result.add(base);
                        buffer.freeBlockInBuffer(output);
                        output = buffer.getNewBlockInBuffer();
                        base++;
                    }
                }
                break;
            } // end else
        }
        if (!output.isEmpty()) {
            buffer.writeBlockToDisk(output, base);
            result.add(base);
            base++;
        }
        buffer.freeBlockInBuffer(output);
        buffer.freeBlockInBuffer(midBlock);

        System.out.println(
                relation + " : Binary Select with I/O : " + (buffer.getIOCounter() - basicIO));
        return result;
    }


    public List<Integer> indexSearch(BPlusTree<Reference> bPlusTree, String relation, int target) {
        buffer.free();
        int basicIO = buffer.getIOCounter();
        List<Integer> result = new ArrayList<>();
        // 基址
        int base = relation.equals("R") ? Calculator.RBASE : Calculator.SBASE;
        base += Calculator.SELECTBASE + 30000;
        Block output = buffer.getNewBlockInBuffer();
        Block input;
        // 根据查询结果，读取数据
        List<Reference> references = bPlusTree.search(target);
        for (Reference reference : references) {
            input = buffer.readBlockFromDisk(reference.block);
            output.writeData(input.data[reference.index]);
            output.writeData(input.data[reference.index + 1]);
            if (output.isFull()) {
                buffer.writeBlockToDisk(output, base);
                result.add(base);
                buffer.freeBlockInBuffer(output);
                output = buffer.getNewBlockInBuffer();
                base++;
            }
            buffer.freeBlockInBuffer(input);
        }
        if (!output.isEmpty()) {
            buffer.writeBlockToDisk(output, base);
            result.add(base);
        }
        buffer.freeBlockInBuffer(output);

        System.out.println(
                relation + " : Index Select with I/O : " + (buffer.getIOCounter() - basicIO));
        return result;
    }

    public static void main(String[] args) {
        Buffer buffer = ExtMem.getDefaultBuffer();
        Selector selector = new Selector(buffer);
        // System.out.println(selector.binarySearch(Calculator.getAddrList("R", true), "R", 40));
        // System.out.println(selector.binarySearch(Calculator.getAddrList("S", true), "S", 60));
        System.out.println(selector.indexSearch(
                BPlusTree.getBPlusTree(Calculator.getAddrList("R", false), buffer), "R", 40));
    }

}
