/**
 * 
 */
package bplustree;

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
        BPlusTree.rank = rank;
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
    
    /**
     * range search from start to end
     * @param start start key, include 
     * @param end   end key, include
     * @return  value list
     */
    public List<V> rangeSearch(int start,int end){
        return root.rangeSearch(start, end);
    }
    
    
    public static void main(String[] args) {
        BPlusTree<Reference> tree = new BPlusTree<>(4);
        for(int i=1;i<=10;i++) {
            tree.insertData(i,new Reference(i, i));
        }
        List<Reference> reference = tree.rangeSearch(3, 7);
        System.out.println(reference);
    }

}
