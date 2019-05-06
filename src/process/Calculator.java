package process;

import java.util.Random;
import utils.Block;
import utils.Buffer;
import utils.ExtMem;

/**
 * @author chen
 *
 */
public class Calculator {
    /** 块大小64，可放7个元组和1个后继磁盘块地址 */
    public static final int BLOCKSIZE = 64;
    /** 缓冲区大小520，最多可放8个块 */
    public static final int BUFFERSIZE = 520;
    
    /** 原始数据基址 */
    public static final int RAWDATABASE = 100000;
    /** 选择结果基址，选择结果地址 2x00xx */
    public static final int SELECTBASE = 200000;
    /** 投影结果基址，投影结果地址 3000xx */
    public static final int PROJECTIONBASE = 300000;
    /** 连接结果基址，连接结果地址 4x00xx */
    public static final int JOINBASE = 400000;
    
    public static void initData() {
        Buffer buffer = ExtMem.initBuffer(BUFFERSIZE, BLOCKSIZE);
        Random random = new Random(System.currentTimeMillis());
        // 关系R：16*7 = 112 个元组， 16个块
        int i=0;
        int data[] = new int[16];
        for(;i<16;i++) {
            Block block = buffer.getNewBlockInBuffer();
            data = new int[16];
            for(int j=0;j<7;j++) {
                data[j*2] = random.nextInt(40)+1;
                data[j*2+1] = random.nextInt(1000)+1;
            }
            data[14] = RAWDATABASE+i+1;
            block.data = data;
            buffer.writeBlockToDisk(block, RAWDATABASE+i);
        }
        
        // 关系S：32*7 = 224 个元组，32个块
        for(;i<48;i++) {
            Block block = buffer.getNewBlockInBuffer();
            data = new int[16];
            for(int j=0;j<7;j++) {
                data[j*2] = random.nextInt(41)+20;
                data[j*2+1] = random.nextInt(1000)+1;
            }
            data[14] = RAWDATABASE+i+1;
            block.data = data;
            if (i==47) {
                data[14] = -1;
            }
            buffer.writeBlockToDisk(block, RAWDATABASE+i);
        }
        System.out.println("Init Buffer with I/O : "+buffer.getIOCounter());
    }
    

    
    

    public static void main(String[] args) {
        Calculator.initData();
    }
    
    
    
}
