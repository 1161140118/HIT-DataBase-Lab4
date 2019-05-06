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
    /** ���С64���ɷ�7��Ԫ���1����̴��̿��ַ */
    public static final int BLOCKSIZE = 64;
    /** ��������С520�����ɷ�8���� */
    public static final int BUFFERSIZE = 520;
    
    /** ԭʼ���ݻ�ַ */
    public static final int RAWDATABASE = 100000;
    /** ѡ������ַ��ѡ������ַ 2x00xx */
    public static final int SELECTBASE = 200000;
    /** ͶӰ�����ַ��ͶӰ�����ַ 3000xx */
    public static final int PROJECTIONBASE = 300000;
    /** ���ӽ����ַ�����ӽ����ַ 4x00xx */
    public static final int JOINBASE = 400000;
    
    public static void initData() {
        Buffer buffer = ExtMem.initBuffer(BUFFERSIZE, BLOCKSIZE);
        Random random = new Random(System.currentTimeMillis());
        // ��ϵR��16*7 = 112 ��Ԫ�飬 16����
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
        
        // ��ϵS��32*7 = 224 ��Ԫ�飬32����
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
