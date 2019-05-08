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
    private List<Integer> addrR;
    private List<Integer> addrS;

    public Joiner(Buffer buffer, List<Integer> addrR, List<Integer> addrS) {
        super();
        this.buffer = buffer;
        this.addrR = addrR;
        this.addrS = addrS;
    }


    public List<Integer> nestLoopJoin() {
        buffer.free();
        int basicIO = buffer.getIOCounter();
        List<Integer> result = new ArrayList<>();
        // 基址
        int base = Calculator.JOINBASE + 10000;
        Block output = buffer.getNewBlockInBuffer();
        Block inputR;
        List<Block> inputS = new ArrayList<>();

        for (Integer raddr : addrR) {
            inputR = buffer.readBlockFromDisk(raddr);

            for (int iaddr = 0; iaddr < addrS.size();) {
                // 读满缓冲区
                int len = buffer.getBlockFreeNumber();
                len = len + iaddr >= addrS.size() ? addrS.size() - iaddr : len;
                inputS.clear();;
                for (int j = 0; j < len; j++) {
                    inputS.add(buffer.readBlockFromDisk(addrS.get(iaddr + j)));
                }
                iaddr += len;

                // Join
                for (Block block : inputS) {
                    // 遍历缓冲中的S块
                    for (int i = 0; i < 7; i++) {
                        // 对R块数据
                        for (int j = 0; j < 7; j++) {
                            // 遍历 S块数据
                            if (inputR.data[i * 2] == block.data[j * 2]) {
                                output.writeData(inputR.data[i * 2]);
                                output.writeData(inputR.data[i * 2 + 1]);
                                output.writeData(block.data[j * 2]);
                                output.writeData(block.data[j * 2 + 1]);
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
                for (Block block : inputS) {
                    buffer.freeBlockInBuffer(block);
                }
            } // end S
            buffer.freeBlockInBuffer(inputR);
        } // end R

        if (!output.isEmpty()) {
            buffer.writeBlockToDisk(output, base);
            result.add(base);
        }

        System.out.println("Nest-Loop-Join with I/O : " + (buffer.getIOCounter() - basicIO));
        return result;
    }

    public List<Integer> sortMergeJoin() {

        return null;
    }


    public List<Integer> hashJoin() {


        return null;
    }



    public static void main(String[] args) {
        System.out.println(new Joiner(ExtMem.getDefaultBuffer(), Calculator.getAddrList("R", false),
                Calculator.getAddrList("S", false)).nestLoopJoin());
    }

}
