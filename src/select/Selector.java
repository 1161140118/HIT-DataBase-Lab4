/**
 * 
 */
package select;

import java.util.ArrayList;
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
    private final int blockTotalNumber;

    public Selector(Buffer buffer) {
        super();
        this.buffer = buffer;
        this.blockTotalNumber = buffer.blockTotalNumber;
    }

    public List<Integer> linearSelect(List<Integer> addrList, String relation, int value) {
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
                if (input.data[j * 2] == value) {
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
            buffer.freeBlockInBuffer(output);
        }
        System.out.println(
                relation + " : Linear Select with I/O : " + (buffer.getIOCounter() - basicIO));
        return result;
    }

    public List<Integer> binarySearch(List<Integer> sortedBlockAddrs, String relation, int value) {
        buffer.free();
        int basicIO = buffer.getIOCounter();
        List<Integer> result = new ArrayList<>();
        // 基址
        int base = relation.equals("R") ? Calculator.RBASE : Calculator.SBASE;
        base += Calculator.SELECTBASE + 20000;
        Block output = buffer.getNewBlockInBuffer();
        Block input;



        System.out.println(
                relation + " : Binary Select with I/O : " + (buffer.getIOCounter() - basicIO));
        return result;
    }

    public List<Integer> indexSearch(BPlusTree<Reference> bPlusTree, String relation, int value) {
        buffer.free();
        int basicIO = buffer.getIOCounter();
        List<Integer> result = new ArrayList<>();
        // 基址
        int base = relation.equals("R") ? Calculator.RBASE : Calculator.SBASE;
        base += Calculator.SELECTBASE + 30000;
        Block output = buffer.getNewBlockInBuffer();
        Block input;
        // 根据查询结果，读取数据
        List<Reference> references = bPlusTree.search(value);
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
            buffer.freeBlockInBuffer(output);
        }
        System.out.println(
                relation + " : Index Select with I/O : " + (buffer.getIOCounter() - basicIO));
        return result;
    }

    public static void main(String[] args) {

    }

}
