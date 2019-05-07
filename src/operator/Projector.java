/**
 * 
 */
package operator;

import java.util.ArrayList;
import java.util.List;
import process.Calculator;
import utils.Block;
import utils.Buffer;
import utils.ExtMem;

/**
 * @author standingby
 *
 */
public class Projector {
    private Buffer buffer;

    public Projector(Buffer buffer) {
        super();
        this.buffer = buffer;
    }

    /**
     * 投影
     * @param addrList
     * @param relation
     * @param index 投影属性索引
     * @return
     */
    public List<Integer> project(List<Integer> addrList, String relation, int index) {
        buffer.free();
        int basicIO = buffer.getIOCounter();
        List<Integer> result = new ArrayList<>();
        // 基址
        int base = relation.equals("R") ? Calculator.RBASE : Calculator.SBASE;
        base += Calculator.PROJECTIONBASE;
        Block output = buffer.getNewBlockInBuffer();
        Block input;

        for (Integer integer : addrList) {
            input = buffer.readBlockFromDisk(integer);
            for (int i = 0; i < 7; i++) {
                output.writeData(input.data[i * 2 + index]);
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

        if (!output.isEmpty()) {
            buffer.writeBlockToDisk(output, base);
            result.add(base);
        }
        buffer.freeBlockInBuffer(output);

        System.out.println(relation + " : Project with I/O : " + (buffer.getIOCounter() - basicIO));

        return result;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(new Projector(ExtMem.getDefaultBuffer())
                .project(Calculator.getAddrList("R", false), "R", 0));
    }

}
