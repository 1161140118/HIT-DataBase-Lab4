/**
 * 
 */
package bplustree;

import java.util.LinkedList;
import java.util.List;

/**
 * @author standingby
 *
 */
public class Node<V> {
    /** 父节点，用于向上插入 */
    protected Node<V> parent;
    /** 关键字链表，递增 */
    protected List<Integer> keyList = new LinkedList<>();
    /** 子节点索引，左子树<key<=右子树 */
    private List<Node<V>> childNodes;

    /**
     * 
     */
    public Node(Node<V> parent, boolean isLeaf) {
        this.parent = parent;
        if (!isLeaf) {
            this.childNodes = new LinkedList<>();
        }
    }

    public Node(Node<V> parent, List<Integer> keyList, List<Node<V>> childNodes) {
        this.parent = parent;
        this.keyList = keyList;
        this.childNodes = childNodes;
    }


    /**
     * 子节点分裂时，由子节点调用
     * @param key 待插入关键字
     */
    protected void insertNode(int key, Node<V> node) {
        System.out.println("非叶节点插入 " + key);
        int i = 0;
        for (i = 0; i < keyList.size(); i++) {
            if (key < keyList.get(i)) {
                break;
            }
        }
        keyList.add(i, key);
        childNodes.add(i + 1, node);
        /**
         * 判定分裂
         * 非根节点：上溢分裂
         * 根节点：下推分裂，当前节点分裂为两个新节点，当前节点重置，新节点作为当前节点子节点
         */
        if (keyList.size() >= BPlusTree.rank) {
            System.out.println("非叶节点分裂." + key);
            int split = BPlusTree.rank / 2;
            Integer splitKey = keyList.get(split);
            // 新节点：左右子树
            List<Integer> leftKeyList = new LinkedList<>();
            List<Node<V>> leftChilds = new LinkedList<>();
            List<Integer> rightKeyList = new LinkedList<>();
            List<Node<V>> rightChilds = new LinkedList<>();
            // copy
            for (int j = 0; j < split; j++) {
                leftKeyList.add(keyList.get(j));
                leftChilds.add(childNodes.get(j));
            }
            leftChilds.add(childNodes.get(split));
            for (int j = split + 1; j < keyList.size(); j++) {
                rightKeyList.add(keyList.get(j));
                rightChilds.add(childNodes.get(j));
            }
            rightChilds.add(childNodes.get(keyList.size()));


            this.keyList.clear();
            this.childNodes.clear();
            Node<V> leftChild = new Node<V>(this, leftKeyList, leftChilds);
            Node<V> rightChild = new Node<V>(this, rightKeyList, rightChilds);

            if (parent != null) {
                // 上溢
                this.keyList = leftKeyList;
                this.childNodes = leftChilds;
                parent.insertNode(splitKey, rightChild);
                // 更新父子关系
                for (Node<V> right : rightChilds) {
                    right.parent = rightChild;
                }

                System.out.println("非叶节点上溢 " + splitKey);
            } else {
                // 根节点下推
                // 重设父子关系
                for (Node<V> left : leftChilds) {
                    left.parent = leftChild;
                }
                for (Node<V> right : rightChilds) {
                    right.parent = rightChild;
                }

                // 更新当前结点，分裂后下推
                this.keyList.add(splitKey);
                this.childNodes.add(leftChild);
                this.childNodes.add(rightChild);
                System.out.println("根节点下推" + splitKey);
            }
        }
        System.out.println(keyList);
    }

    protected void insertData(int key, V ref) {
        int i = 0;
        for (i = 0; i < keyList.size(); i++) {
            if (key < keyList.get(i)) {
                break;
            }
        }
        if (childNodes.isEmpty()) {
            childNodes.add(new Leaf<V>(this));
        }
        childNodes.get(i).insertData(key, ref);
    }


    protected List<V> search(int key) {
        int i = 0;
        for (i = 0; i < keyList.size(); i++) {
            if (key < keyList.get(i)) {
                break;
            }
        }
        if (childNodes.isEmpty()) {
            return null;
        }
        return childNodes.get(i).search(key);
    }

    protected List<List<V>> rangeSearch(int start, int end) {
        int i = 0;
        for (i = 0; i < keyList.size(); i++) {
            if (start < keyList.get(i)) {
                break;
            }
        }
        return childNodes.get(i).rangeSearch(start, end);
    }


}
