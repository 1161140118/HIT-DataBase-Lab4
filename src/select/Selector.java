/**
 * 
 */
package select;

import process.Calculator;
import utils.Block;
import utils.Buffer;

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
            input = buffer.readBlockFromDisk(i);
            for(int j=0;i< 7;j++) {
                if (input.data[j*2] == valueA) {
                    output.writeData(input.data[j*2]);
                    output.writeData(input.data[j*2+1]);
                }
            }
            
            
            
            
            
            
            buffer.freeBlockInBuffer(input);
        }
        
        
        System.out.println("Linear Select with I/O : "+(buffer.getIOCounter() - basicIO));
    }
    
    
}
