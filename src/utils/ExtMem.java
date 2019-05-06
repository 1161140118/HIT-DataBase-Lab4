/**
 * 
 */
package utils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import process.Calculator;

/**
 * @author standingby
 *
 */
public class ExtMem {
    public static final String DISKADDR = "src/disk/";
    
    public static Buffer initBuffer(int bufferSize, int blockSize) {
        return new Buffer(bufferSize, blockSize);
    }    
    
    public static Buffer getDefaultBuffer() {
        return initBuffer(Calculator.BUFFERSIZE, Calculator.BLOCKSIZE);
    }

    public static void dropBlockOnDisk(int addr) {
        String filename= DISKADDR+addr+".blk";
        File file = new File(filename);
        if (!file.delete()) {
            System.err.println("Dropping Block Fails!");
        }
    }
    
    
}
