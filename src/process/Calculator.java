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
    /** ���С64���ɷ�7��Ԫ���1����̴��̿��ַ */
    public static final int BLOCKSIZE = 64;
    /** ��������С520�����ɷ�8���� */
    public static final int BUFFERSIZE = 520;

    /** ԭʼ���ݻ�ַ */
    public static final int RAWDATABASE = 100000;
    /** R ���ƫ�� */
    public static final int RBASE = 0;
    /** S ���ƫ�� */
    public static final int SBASE = 1000;
    /** ����м��������ַ */
    public static final int EXTERNALBASE = 110000;
    /** �����������ַ */
    public static final int SORTEDBASE = 120000;
    /** ѡ������ַ��ѡ������ַ 2xx0xx */
    public static final int SELECTBASE = 200000;
    /** ͶӰ�����ַ��ͶӰ�����ַ 30x0xx */
    public static final int PROJECTIONBASE = 300000;
    /** ���ӽ����ַ�����ӽ����ַ 4x00xx */
    public static final int JOINBASE = 400000;



    public static void initData() {
        Buffer buffer = ExtMem.initBuffer(BUFFERSIZE, BLOCKSIZE);
        Random random = new Random(System.currentTimeMillis());
        // ��ϵR��16*7 = 112 ��Ԫ�飬 16����
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

        // ��ϵS��32*7 = 224 ��Ԫ�飬32����
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
