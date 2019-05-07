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
    /** ���ڵ㣬�������ϲ��� */
    protected Node<V> parent;
    /** �ؼ����������� */
    protected List<Integer> keyList = new LinkedList<>();
    /** �ӽڵ�������������<key<=������ */
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
     * �ӽڵ����ʱ�����ӽڵ����
     * @param key ������ؼ���
     */
    protected void insertNode(int key, Node<V> node) {
        System.out.println("��Ҷ�ڵ���� " + key);
        int i = 0;
        for (i = 0; i < keyList.size(); i++) {
            if (key < keyList.get(i)) {
                break;
            }
        }
        keyList.add(i, key);
        childNodes.add(i + 1, node);
        /**
         * �ж�����
         * �Ǹ��ڵ㣺�������
         * ���ڵ㣺���Ʒ��ѣ���ǰ�ڵ����Ϊ�����½ڵ㣬��ǰ�ڵ����ã��½ڵ���Ϊ��ǰ�ڵ��ӽڵ�
         */
        if (keyList.size() >= BPlusTree.rank) {
            System.out.println("��Ҷ�ڵ����." + key);
            int split = BPlusTree.rank / 2;
            Integer splitKey = keyList.get(split);
            // �½ڵ㣺��������
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
                // ����
                this.keyList = leftKeyList;
                this.childNodes = leftChilds;
                parent.insertNode(splitKey, rightChild);
                // ���¸��ӹ�ϵ
                for (Node<V> right : rightChilds) {
                    right.parent = rightChild;
                }

                System.out.println("��Ҷ�ڵ����� " + splitKey);
            } else {
                // ���ڵ�����
                // ���踸�ӹ�ϵ
                for (Node<V> left : leftChilds) {
                    left.parent = leftChild;
                }
                for (Node<V> right : rightChilds) {
                    right.parent = rightChild;
                }

                // ���µ�ǰ��㣬���Ѻ�����
                this.keyList.add(splitKey);
                this.childNodes.add(leftChild);
                this.childNodes.add(rightChild);
                System.out.println("���ڵ�����" + splitKey);
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
