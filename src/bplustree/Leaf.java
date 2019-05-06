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
    /** 下一个叶结点 */
    private Leaf<V> next;
    /** 数据索引，相同Key，使用链表存 */
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
     * 在叶结点插入索引，
     * 可能触发分裂
     * @return 
     */
    @Override
    protected void insertData(int key, V ref) {
        if (keyList.contains(key)) {
            // 已存在key，添加到链表
            refList.get(keyList.indexOf(key)).add(ref);
        }else {
            // 不存在Key，插入Key和新链表
            int i = 0;
            for (i = 0; i < refList.size(); i++) {
                if (key < keyList.get(i)) {
                    break;
                }
            }
            keyList.add(i, key);
            refList.add(i, new LinkedList<V>(Arrays.asList(ref))); //TODO
        }
        System.out.println("叶结点插入"+key);
        
        /**
         * 判定分裂
         * 例：4阶树，达到4个数据结点，分裂，上溢第三个key（index=2） 
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
                // 执行 size-split 此
                keyList.remove(split);
                refList.remove(split);
            }
            Leaf<V> newLeaf = new Leaf<V>(parent, newKeyList, newRefList);
            this.next = newLeaf;
            System.out.println("叶结点分裂."+key+" : "+newKeyList);
            parent.insertNode(newKeyList.get(0), newLeaf);
        }
        System.out.println(key+" 插入最终结果 "+keyList);
        // 检查不变量
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
     * 递归调用，范围查询到末尾。
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
            // 范围查询未结束
            references.addAll(next.endOfSearch(end));
        }
        return references;
    }

}
