/**
 * 
 */
package bplustree;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author standingby
 *
 */
public class Leaf<V> extends Node<V> {
    /** ��һ��Ҷ��� */
    private Leaf<V> next;
    /** ������������ͬKey��ʹ������� */
    private List<List<V>> refList;
    

    /**
     * @param parent
     * @param isLeaf
     */
    public Leaf(Node<V> parent) {
        super(parent, false);
        this.refList = new LinkedList<>();
    }

    private Leaf(Node<V> parent, List<Integer> newKeyList, List<List<V>> newRefList) {
        super(parent, false);
        this.keyList = newKeyList;
        this.refList = newRefList;
    }

    /**
     * ��Ҷ������������
     * ���ܴ�������
     * @return 
     */
    @Override
    protected void insertData(int key, V ref) {
        if (keyList.contains(key)) {
            // �Ѵ���key����ӵ�����
            refList.get(keyList.indexOf(key)).add(ref);
        }else {
            // ������Key������Key��������
            int i = 0;
            for (i = 0; i < refList.size(); i++) {
                if (key < keyList.get(i)) {
                    break;
                }
            }
            keyList.add(i, key);
            refList.add(i, new LinkedList<V>(Arrays.asList(ref))); //TODO
        }
        System.out.println("Ҷ������"+key);
        
        /**
         * �ж�����
         * ����4�������ﵽ4�����ݽ�㣬���ѣ����������key��index=2�� 
         */
        if (refList.size() >= BPlusTree.rank) {
            int split = BPlusTree.rank / 2;
            List<Integer> newKeyList = new LinkedList<>();
            List<List<V>> newRefList = new LinkedList<>();
            // copy
            for (int j = split; j < keyList.size(); j++) {
                newKeyList.add(keyList.get(j));
                newRefList.add(refList.get(j));
            }
            // remove
            int len  = keyList.size();
            for (int j = split; j < len; j++) {
                // ִ�� size-split ��
                keyList.remove(split);
                refList.remove(split);
            }
            Leaf<V> newLeaf = new Leaf<V>(parent, newKeyList, newRefList);
            this.next = newLeaf;
            System.out.println("Ҷ������."+key+" : "+newKeyList);
            parent.insertNode(newKeyList.get(0), newLeaf);
        }
        System.out.println(key+" �������ս�� "+keyList);
        // ��鲻����
        if (keyList.size() != refList.size()) {
            System.err.println("Error : The length of keylist not equals to reflist!");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see bplustree.Node#search(int)
     */
    @Override
    protected List<V> search(int key) {
        int index = keyList.indexOf(key);
        if (index == -1) {
            return null;
        }
        return refList.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see bplustree.Node#rangeSearch(int, int)
     */
    @Override
    protected List<List<V>> rangeSearch(int start, int end) {
        List<List<V>> references = new LinkedList<>();
        int i = 0;
        for (i = 0; i < keyList.size(); i++) {
            if (start <= keyList.get(i)) {
                break;
            }
        }
        for (; i < refList.size(); i++) {
            references.add(refList.get(i));
        }
        if (next != null) {
            references.addAll(next.endOfSearch(end));
        }
        return references;
    }

    /**
     * �ݹ���ã���Χ��ѯ��ĩβ��
     */
    private List<List<V>> endOfSearch(int end) {
        List<List<V>> references = new LinkedList<>();
        int i = 0;
        for (; i < keyList.size(); i++) {
            if (end < keyList.get(i)) {
                break;
            }
            references.add(refList.get(i));
        }
        if (i==keyList.size() && next!=null) {
            // ��Χ��ѯδ����
            references.addAll(next.endOfSearch(end));
        }
        return references;
    }

}
