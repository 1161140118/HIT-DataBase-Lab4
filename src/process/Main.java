package process;

import utils.Block;
import utils.Buffer;
import utils.ExtMem;

/**
 * @author chen
 *
 */
public class Main {
    public static final int BLOCKSIZE = 64;
    public static final int BUFFERSIZE = 520;
    

    public static void main(String[] args) {
        Buffer buffer = ExtMem.initBuffer(BUFFERSIZE, BLOCKSIZE);
        Block block = buffer.getNewBlockInBuffer();
        block.data[0]=1;
        block.data[1]=2;
        buffer.writeBlockToDisk(block.id, block.id);
    }
    
}
