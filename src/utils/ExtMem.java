/**
 * 
 */
package utils;

import java.io.File;
import java.util.Arrays;

/**
 * @author chen
 *
 */
public class ExtMem {
    public static final String DISKADDR = "src/disk/";
    
    public static Buffer initBuffer(int bufferSize, int blockSize) {
        return new Buffer(bufferSize, blockSize);
    }    

    public static void dropBlockOnDisk(int addr) {
        String filename= DISKADDR+addr+".blk";
        File file = new File(filename);
        if (!file.delete()) {
            System.err.println("Dropping Block Fails!");
        }
    }
    
    public static void main(String[] args) {
        String test = "ab,dfs,sd,, ,";
        System.out.println(Arrays.toString(test.split(",")));
        
    }
    
}
