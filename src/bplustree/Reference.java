/**
 * 
 */
package bplustree;

/**
 * @author standingby
 *
 */
public class Reference {
    int block;
    int index;
    
    public Reference(int block, int index) {
        super();
        this.block = block;
        this.index = index;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return block+","+index;
    }

}
