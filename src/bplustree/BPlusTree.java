/**
 * 
 */
package bplustree;

import java.util.List;
import process.Calculator;
import utils.Block;
import utils.Buffer;
import utils.ExtMem;

/**
 * @author standingby
 *
 */
public class BPlusTree<V> {
    /** �����������Key>=rankʱ������  */
    public static int rank;
    private Node<V> root;

    public BPlusTree(int rank) {
        BPlusTree.rank = rank;
        // ���ڵ�ʼ��ά��ͬһ��Node
        this.root = new Node<V>(null, false);
    }

    public void insertData(int key, V ref) {
        if (key==40) {
            System.err.println("40");
        }
        root.insertData(key, ref);
    }


    /**
     * �ݹ����Node.search��������ѯ����
     * @param key ������
     * @return
     */
    public List<V> search(int key) {
        return root.search(key);
    }

    /**
     * range search from start to end
     * @param start start key, include 
     * @param end   end key, include
     * @return  value list
     */
    public List<List<V>> rangeSearch(int start, int end) {
        return root.rangeSearch(start, end);
    }

    public static BPlusTree<Reference> getBPlusTree(List<Integer> addrList, Buffer buffer) {
        BPlusTree<Reference> tree = new BPlusTree<>(4);
        for (Integer integer : addrList) {
            Block input = buffer.readBlockFromDisk(integer);
            for (int i = 0; i < 7; i++) {
                tree.insertData(input.data[i * 2], new Reference(integer, i * 2));
            }
            buffer.freeBlockInBuffer(input);
        }
        return tree;
    }


    public static void main(String[] args) {
        getBPlusTree(Calculator.getAddrList("R", false), ExtMem.getDefaultBuffer());
    }

}
