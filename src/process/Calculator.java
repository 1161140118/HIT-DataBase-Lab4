package process;

import java.util.ArrayList;
import java.util.List;
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
    /** R 结果偏移 */
    public static final int RBASE = 0;
    /** S 结果偏移 */
    public static final int SBASE = 1000;
    /** 外存中间结果排序基址 */
    public static final int EXTERNALBASE = 110000;
    /** 外存排序结果基址 */
    public static final int SORTEDBASE = 120000;
    /** 选择结果基址，选择结果地址 2xx0xx */
    public static final int SELECTBASE = 200000;
    /** 投影结果基址，投影结果地址 30x0xx */
    public static final int PROJECTIONBASE = 300000;
    /** 连接结果基址，连接结果地址 4x00xx */
    public static final int JOINBASE = 400000;



    public static void initData() {
        Buffer buffer = ExtMem.initBuffer(BUFFERSIZE, BLOCKSIZE);
        Random random = new Random(System.currentTimeMillis());
        // 关系R：16*7 = 112 个元组， 16个块
        int data[] = new int[16];
        for (int i = 0; i < 16; i++) {
            Block block = buffer.getNewBlockInBuffer();
            data = new int[16];
            for (int j = 0; j < 7; j++) {
                data[j * 2] = random.nextInt(40) + 1;
                if (data[2 * j] == 40) {
                    System.out.println("ok. 40");
                }
                data[j * 2 + 1] = random.nextInt(1000) + 1;
            }
            data[14] = RAWDATABASE + RBASE + i + 1;
            block.data = data;
            buffer.writeBlockToDisk(block, RAWDATABASE + RBASE + i);
        }

        // 关系S：32*7 = 224 个元组，32个块
        for (int i = 0; i < 32; i++) {
            Block block = buffer.getNewBlockInBuffer();
            data = new int[16];
            for (int j = 0; j < 7; j++) {
                data[j * 2] = random.nextInt(41) + 20;
                if (data[2 * j] == 60) {
                    System.out.println("ok. 60");
                }
                data[j * 2 + 1] = random.nextInt(1000) + 1;
            }
            data[14] = RAWDATABASE + SBASE + i + 1;
            block.data = data;
            if (i == 47) {
                data[14] = -1;
            }
            buffer.writeBlockToDisk(block, RAWDATABASE + SBASE + i);
        }
        System.out.println("Init Buffer with I/O : " + buffer.getIOCounter());
    }

    public static List<Integer> getAddrList(String relation, boolean sorted) {
        int base = sorted ? SORTEDBASE : RAWDATABASE;
        int len;
        switch (relation) {
            case "R":
                base += RBASE;
                len = 16;
                break;

            case "S":
                base += SBASE;
                len = 32;
                break;

            default:
                System.err.println("Parameter Error : relation should be 'R' or 'S' ");
                return null;
        }
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            result.add(base + i);
        }
        return result;
    }



    public static void main(String[] args) {
        Calculator.initData();
    }


}
