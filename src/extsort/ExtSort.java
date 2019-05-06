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

        /** 最小归并段 */
        List<List<Integer>> addrLists = new LinkedList<>();

        List<Integer> temp = new LinkedList<>();
        // 内存排序，形成归并段
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
                    baseAddr + (i / buffer.blockTotalNumber) * buffer.blockTotalNumber)); // 取整数倍
            temp.clear();
        }
        secAddr = baseAddr + 10000;
        return sectionMerge(addrLists);
    }

    /**
     * 内存块排序
     * @return
     */
    private static List<Integer> blockSort(List<Integer> blockAddrs, int addrBase) {
        buffer.free();
        List<Block> blocks = new LinkedList<>();
        // 读入内存
        for (Integer integer : blockAddrs) {
            blocks.add(buffer.readBlockFromDisk(integer));
        }
        // 直接选择排序：升序
        for (int bi = 0; bi < blocks.size(); bi++) {
            for (int i = 0; i < 7; i++) {
                int pre = blocks.get(bi).data[i * 2];

                int j = i;
                for (int bj = bi; bj < blocks.size(); bj++) {
                    for (; j < 7; j++) {
                        int cur = blocks.get(bj).data[j * 2];
                        if (cur < pre) {
                            // 升序
                            // 交换
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
     * 内存归并
     * @param addrLists 归并段序列
     * @return
     */
    private static List<Integer> sectionMerge(List<List<Integer>> addrLists) {
        buffer.free();
        int n_ = buffer.blockTotalNumber - 1;

        if (addrLists.size() < n_) {
            // 可以进行 n-1 路归并排序
            return mergeSort(addrLists);
        }

        /****************
         *  递归归并
         ****************/

        int i = 0;
        List<List<Integer>> result = new ArrayList<>();

        // 划分为n-1路
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
        int sumSec = addrLists.size(); // 待归并路数

        /** 记录各序列剩余未归并块数（包括输入缓冲） */
        int[] remain = new int[sumSec];
        /** 记录各序列块数 */
        int[] secLen = new int[sumSec];

        for (int i = 0; i < sumSec; i++) {
            remain[i] = addrLists.get(i).size();
            secLen[i] = addrLists.get(i).size();
        }
        int remainSec = sumSec;

        // 初始化块内索引
        int[] indexs = new int[sumSec];

        // 初始化输入输出块
        List<Block> inputs = new ArrayList<>();
        for (List<Integer> list : addrLists) {
            inputs.add(buffer.readBlockFromDisk(list.get(0)));
        }
        Block output = buffer.getNewBlockInBuffer();

        // 输出地址序列
        List<Integer> result = new LinkedList<>();

        /*********************
         *      归并
         *********************/



        while (remainSec > 0) {
            // 初始化记录
            int minNum = -1;
            int minIndex = 0;

            for (int i = 0; i < sumSec; i++) { // 遍历输入快，查找最小值
                if (remain[i] == 0) {
                    // 输入块中无此序列的块
                    continue;
                }
                if (minNum == -1) {
                    // 第一个有值块，初始化
                    minNum = inputs.get(i).data[indexs[i]];
                    minIndex = i;
                } else if (minNum > inputs.get(i).data[indexs[i]]) {
                    // 有新的最小值
                    minNum = inputs.get(i).data[indexs[i]];
                    minIndex = i;
                }
            }

            // 写出最小值
            output.writeData(minNum);
            output.writeData(inputs.get(minIndex).data[indexs[minIndex] + 1]);
            if (output.isFull()) {
                output.data[14] = secAddr + 1;
                buffer.writeBlockToDisk(output, secAddr);
                output = buffer.getNewBlockInBuffer();
                result.add(secAddr);
                secAddr++;
            }

            // 输入块指针后移
            indexs[minIndex] += 2;

            // 若当前输入块已读完，继续移入输入块
            if (indexs[minIndex] >= 13) {
                indexs[minIndex] = 0; // 重置index
                buffer.freeBlockInBuffer(inputs.get(minIndex));

                remain[minIndex]--;
                if (remain[minIndex] > 0) {
                    // 有剩余，则移入
                    inputs.remove(minIndex);
                    inputs.add(minIndex, buffer.readBlockFromDisk(
                            addrLists.get(minIndex).get(secLen[minIndex] - remain[minIndex])));
                } else {
                    // 无剩余，已读完
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
