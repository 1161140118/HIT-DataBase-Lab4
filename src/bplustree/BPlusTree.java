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
public class BPlusTree<V> {
    public static int rank;
    private Node<V> root;
    
    /**
     * 
     */
    public BPlusTree(int rank) {
        this.rank = rank;
        // ���ڵ�ʼ��ά��ͬһ��Node
        this.root = new Node<V>(null, false);
    }
    
    public void insertData(int key,V ref) {
        root.insertData(key, ref);
    }
    
    
    /**
     * �ݹ����Node.search��������ѯ����
     * @param key ������
     * @return
     */
    public V search(int key) {
        return root.search(key);
    }
    
    public List<V> rangeSearch(int start,int end){
        return root.rangeSearch(start, end);
    }
    
    
    public static void main(String[] args) {
        BPlusTree<Reference> tree = new BPlusTree<>(4);
        for(int i=1;i<=10;i++) {
            tree.insertData(i,new Reference(i, i));
        }
        Reference reference = tree.search(5);
        System.out.println(reference);
    }

}
