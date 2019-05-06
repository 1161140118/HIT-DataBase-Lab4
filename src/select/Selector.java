/**
 * 
 */
package select;

import java.util.List;
import process.Calculator;
import utils.Block;
import utils.Buffer;
import utils.ExtMem;

/**
 * @author standingby
 *
 */
public class Selector {
    private final int valueA;
    private final int valueC;
    private final Buffer buffer;
    private final int blockTotalNumber;
    
    public Selector(int valueA, int valueC, Buffer buffer) {
        super();
        this.valueA = valueA;
        this.valueC = valueC;
        this.buffer = buffer;
        this.blockTotalNumber = buffer.blockTotalNumber;
    }

    public void linearSelect() {
        int basicIO = buffer.getIOCounter();
        int base = Calculator.SELECTBASE + 10000;
        Block output = buffer.getNewBlockInBuffer();
        Block input;
        int i= Calculator.RAWDATABASE;
        
        // R.A
        for( ; i< Calculator.RAWDATABASE + 16 ;i++) {
            // 100000 - 100015
            input = buffer.readBlockFromDisk(i);
            for(int j=0;j< 7;j++) {
                if (input.data[j*2] == valueA) {
                    output.writeData(input.data[j*2]);
                    output.writeData(input.data[j*2+1]);
                    if (output.isFull()) {
                    	System.out.println(output);
						buffer.writeBlockToDisk(output, base);
						output = buffer.getNewBlockInBuffer();
						base++;
					}
                }
            }
            buffer.freeBlockInBuffer(input);
        }
        buffer.writeBlockToDisk(output, base);
        output = buffer.getNewBlockInBuffer();
        // S.C
        base += 1000;
        for( ; i< Calculator.RAWDATABASE + 48 ;i++) {
            // 100016 - 100047
            input = buffer.readBlockFromDisk(i);
            for(int j=0;j< 7;j++) {
                if (input.data[j*2] == valueC) {
                    output.writeData(input.data[j*2]);
                    output.writeData(input.data[j*2+1]);
                    if (output.isFull()) {
						buffer.writeBlockToDisk(output, base);
						buffer.freeBlockInBuffer(output);
						output = buffer.getNewBlockInBuffer();
						base++;
					}
                }
            }
			buffer.freeBlockInBuffer(input);
        }
        buffer.writeBlockToDisk(output, base);
        buffer.freeBlockInBuffer(output);
        System.out.println("Linear Select with I/O : "+(buffer.getIOCounter() - basicIO));
    }
    
    public void binarySearch(List<Integer> sortedBlockAddrs) {
        
    }
    
    public static void main(String[] args) {
		new Selector(40, 60, ExtMem.getDefaultBuffer()).linearSelect();

	}
    
}
