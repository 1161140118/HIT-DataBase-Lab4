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
    public final int id ;
    // Êý¾ÝÓò
    public int[] data;
    
    protected Block(int id,int blockSize) {
        this.id = id;
        data = new int[blockSize/4];
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String string="";
        for (int i : data) {
            string+=i+",";
        }
        return string;
    }

}
