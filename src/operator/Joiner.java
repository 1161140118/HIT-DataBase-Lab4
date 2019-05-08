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
public class Joiner {
    private Buffer buffer;

    public Joiner(Buffer buffer) {
        super();
        this.buffer = buffer;
    }


    public List<Integer> nestLoopJoin(List<Integer> addrR, List<Integer> addrS) {
        buffer.free();
        int basicIO = buffer.getIOCounter();
        List<Integer> result = new ArrayList<>();
        // 基址
        int base = Calculator.JOINBASE + 10000;
        Block output = buffer.getNewBlockInBuffer();
        Block blockS;
        List<Block> blockR = new ArrayList<>();


        for (int iaddr = 0; iaddr < addrR.size();) {
            // 尽量多读入缓冲区，仅留1块给S
            int len = buffer.getBlockFreeNumber() - 1;
            len = len + iaddr >= addrR.size() ? addrR.size() - iaddr : len;
            blockR.clear();
            for (int j = 0; j < len; j++) {
                blockR.add(buffer.readBlockFromDisk(addrR.get(iaddr + j)));
            }
            iaddr += len;

            for (Integer saddr : addrS) {
                blockS = buffer.readBlockFromDisk(saddr);

                // Join
                for (Block block : blockR) {
                    // 遍历缓冲中的R块
                    for (int i = 0; i < 7; i++) {
                        // 对R块数据

                        for (int j = 0; j < 7; j++) {
                            // 遍历 S块数据
                            if (blockS.data[i * 2] == block.data[j * 2]) {
                                output.writeData(block.data[j * 2]);
                                output.writeData(block.data[j * 2 + 1]);
                                output.writeData(blockS.data[i * 2]);
                                output.writeData(blockS.data[i * 2 + 1]);
                                if (output.getIndex() == 12) {
                                    output.writeData(0);
                                    output.writeData(0);
                                    output.writeData(base + 1);
                                    buffer.writeBlockToDisk(output, base);
                                    result.add(base);
                                    output = buffer.getNewBlockInBuffer();
                                    base++;
                                }
                            }
                        }
                    }
                }
                buffer.freeBlockInBuffer(blockS);
            } // end S
            for (Block block : blockR) {
                buffer.freeBlockInBuffer(block);
            }
        } // end R

        if (!output.isEmpty()) {
            buffer.writeBlockToDisk(output, base);
            result.add(base);
        }

        System.out.println("Nest-Loop-Join with I/O : " + (buffer.getIOCounter() - basicIO));
        return result;
    }

    public List<Integer> sortMergeJoin(List<Integer> addrR, List<Integer> addrS) {
        buffer.free();
        int basicIO = buffer.getIOCounter();
        List<Integer> result = new ArrayList<>();
        // 基址
        int base = Calculator.JOINBASE + 20000;
        Block output = buffer.getNewBlockInBuffer();
        Block inputR;
        Block inputS;

        for (Integer r : addrR) {
            inputR = buffer.readBlockFromDisk(r);
            for (int i = 0; i < 7; i++) {
                // 顺序遍历 R 中每个元组



            } // end r
            buffer.freeBlockInBuffer(inputR);
        }



        System.out.println("Sort-Merge-Join with I/O : " + (buffer.getIOCounter() - basicIO));
        return result;
    }


    public List<Integer> hashJoin() {
        buffer.free();
        int basicIO = buffer.getIOCounter();
        List<Integer> result = new ArrayList<>();
        // 基址
        int base = Calculator.JOINBASE + 30000;
        Block output = buffer.getNewBlockInBuffer();
        Block inputR;
        List<Block> inputS = new ArrayList<>();



        System.out.println("Sort-Merge-Join with I/O : " + (buffer.getIOCounter() - basicIO));
        return result;
    }



    public static void main(String[] args) {
        // nest-loop-join
        System.out.println(new Joiner(ExtMem.getDefaultBuffer()).nestLoopJoin(
                Calculator.getAddrList("R", false), Calculator.getAddrList("S", false)));

    }

}
