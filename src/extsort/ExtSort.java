/**
 * 
 */
package extsort;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import process.Calculator;
import utils.Block;
import utils.ExtMem;

/**
 * @author standingby
 *
 */
public class ExtSort {
    private static utils.Buffer buffer;
    private static int secAddr;

    public static List<Integer> sort(List<Integer> blockAddrs, utils.Buffer buffer, int baseAddr) {
        ExtSort.buffer = buffer;

        /** ��С�鲢�� */
        List<List<Integer>> addrLists = new LinkedList<>();

        List<Integer> temp = new LinkedList<>();
        // �ڴ������γɹ鲢��
        int i = 0;
        for (Integer integer : blockAddrs) {
            temp.add(integer);
            i++;
            if (i % buffer.blockTotalNumber == 0) {
                addrLists.add(blockSort(temp, baseAddr + i - buffer.blockTotalNumber));
                temp.clear();
            }
        }
        if (temp.size() > 0) {
            addrLists.add(blockSort(temp,
                    baseAddr + (i / buffer.blockTotalNumber) * buffer.blockTotalNumber)); // ȡ������
            temp.clear();
        }
        secAddr = baseAddr + 10000;
        return sectionMerge(addrLists);
    }

    /**
     * �ڴ������
     * @return
     */
    private static List<Integer> blockSort(List<Integer> blockAddrs, int addrBase) {
        buffer.free();
        List<Block> blocks = new LinkedList<>();
        // �����ڴ�
        for (Integer integer : blockAddrs) {
            blocks.add(buffer.readBlockFromDisk(integer));
        }
        // ֱ��ѡ����������
        for (int bi = 0; bi < blocks.size(); bi++) {
            for (int i = 0; i < 7; i++) {
                int pre = blocks.get(bi).data[i * 2];

                int j = i;
                for (int bj = bi; bj < blocks.size(); bj++) {
                    for (; j < 7; j++) {
                        int cur = blocks.get(bj).data[j * 2];
                        if (cur < pre) {
                            // ����
                            // ����
                            int tmp1 = blocks.get(bi).data[i * 2];
                            int tmp2 = blocks.get(bi).data[i * 2 + 1];
                            blocks.get(bi).data[i * 2] = blocks.get(bj).data[j * 2];
                            blocks.get(bi).data[i * 2 + 1] = blocks.get(bj).data[j * 2 + 1];
                            blocks.get(bj).data[j * 2] = tmp1;
                            blocks.get(bj).data[j * 2 + 1] = tmp2;
                            pre = cur;
                        }
                    }
                    j = 0;
                }
            }
        }
        List<Integer> addrs = new LinkedList<>();
        for (Block block : blocks) {
            block.data[14] = addrBase + 1;
            buffer.writeBlockToDisk(block, addrBase);
            addrs.add(addrBase);
            addrBase++;
        }
        return addrs;
    }

    /**
     * �ڴ�鲢
     * @param addrLists �鲢������
     * @return
     */
    private static List<Integer> sectionMerge(List<List<Integer>> addrLists) {
        buffer.free();
        int n_ = buffer.blockTotalNumber - 1;

        if (addrLists.size() < n_) {
            // ���Խ��� n-1 ·�鲢����
            return mergeSort(addrLists);
        }

        /****************
         *  �ݹ�鲢
         ****************/

        int i = 0;
        List<List<Integer>> result = new ArrayList<>();

        // ����Ϊn-1·
        List<List<Integer>> temp = new ArrayList<>();
        for (List<Integer> list : addrLists) {
            temp.add(list);
            i++;
            if (i % n_ == 0) {
                result.add(sectionMerge(temp));
                temp.clear();
            }
        }
        if (temp.size() > 0) {
            result.add(sectionMerge(temp));
            temp.clear();
        }

        return sectionMerge(result);
    }

    private static List<Integer> mergeSort(List<List<Integer>> addrLists) {
        buffer.free();
        int sumSec = addrLists.size(); // ���鲢·��

        /** ��¼������ʣ��δ�鲢�������������뻺�壩 */
        int[] remain = new int[sumSec];
        /** ��¼�����п��� */
        int[] secLen = new int[sumSec];

        for (int i = 0; i < sumSec; i++) {
            remain[i] = addrLists.get(i).size();
            secLen[i] = addrLists.get(i).size();
        }
        int remainSec = sumSec;

        // ��ʼ����������
        int[] indexs = new int[sumSec];

        // ��ʼ�����������
        List<Block> inputs = new ArrayList<>();
        for (List<Integer> list : addrLists) {
            inputs.add(buffer.readBlockFromDisk(list.get(0)));
        }
        Block output = buffer.getNewBlockInBuffer();

        // �����ַ����
        List<Integer> result = new LinkedList<>();

        /*********************
         *      �鲢
         *********************/



        while (remainSec > 0) {
            // ��ʼ����¼
            int minNum = -1;
            int minIndex = 0;

            for (int i = 0; i < sumSec; i++) { // ��������죬������Сֵ
                if (remain[i] == 0) {
                    // ��������޴����еĿ�
                    continue;
                }
                if (minNum == -1) {
                    // ��һ����ֵ�飬��ʼ��
                    minNum = inputs.get(i).data[indexs[i]];
                    minIndex = i;
                } else if (minNum > inputs.get(i).data[indexs[i]]) {
                    // ���µ���Сֵ
                    minNum = inputs.get(i).data[indexs[i]];
                    minIndex = i;
                }
            }

            // д����Сֵ
            output.writeData(minNum);
            output.writeData(inputs.get(minIndex).data[indexs[minIndex] + 1]);
            if (output.isFull()) {
                output.data[14] = secAddr + 1;
                buffer.writeBlockToDisk(output, secAddr);
                output = buffer.getNewBlockInBuffer();
                result.add(secAddr);
                secAddr++;
            }

            // �����ָ�����
            indexs[minIndex] += 2;

            // ����ǰ������Ѷ��꣬�������������
            if (indexs[minIndex] >= 13) {
                indexs[minIndex] = 0; // ����index
                buffer.freeBlockInBuffer(inputs.get(minIndex));

                remain[minIndex]--;
                if (remain[minIndex] > 0) {
                    // ��ʣ�࣬������
                    inputs.remove(minIndex);
                    inputs.add(minIndex, buffer.readBlockFromDisk(
                            addrLists.get(minIndex).get(secLen[minIndex] - remain[minIndex])));
                } else {
                    // ��ʣ�࣬�Ѷ���
                    remainSec--;
                }
            }
        }

        return result;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        List<Integer> R = new LinkedList<>();
        for (int i = 0; i < 16; i++) {
            R.add(i + Calculator.RAWDATABASE + Calculator.RBASE);
        }
        System.out.println(
                sort(R, ExtMem.getDefaultBuffer(), Calculator.SORTEDBASE + Calculator.RBASE));
        List<Integer> S = new LinkedList<>();
        for (int i = 0; i < 32; i++) {
            S.add(i + Calculator.RAWDATABASE + Calculator.SBASE);
        }
        System.out.println(
                sort(S, ExtMem.getDefaultBuffer(), Calculator.SORTEDBASE + Calculator.SBASE));
    }

}
