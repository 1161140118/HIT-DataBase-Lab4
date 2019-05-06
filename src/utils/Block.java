/**
 * 
 */
package utils;

/**
 * @author chen
 *
 */
public class Block {
    protected boolean free = true;
    private final int blockSize;
    public final int id ;
    // Êý¾ÝÓò
    public int[] data;
    private int index = 0;
    
    protected Block(int id,int blockSize) {
        this.blockSize = blockSize;
        this.id = id;
        data = new int[blockSize/4];
    }
    
    protected void free(){
        free = true;
        data = new int[blockSize/4];
        index = 0;
    }
    
    public void writeData(int d) {
        data[index] = d;
        index++;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String string="";
        for (int i : data) {
            string+=","+i;
        }
        return string.substring(1);
    }

}
