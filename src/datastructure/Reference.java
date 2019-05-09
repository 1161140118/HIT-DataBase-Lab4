/**
 * 
 */
package datastructure;

/**
 * @author standingby
 *
 */
public class Reference implements Comparable<Reference> {
	/** 磁盘块地址 */
	public final int block;
	/** 磁盘块内数据索引， data[index] = key */
	public final int index;

	public Reference(int block, int index) {
		super();
		this.block = block;
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return block + "," + index;
	}

	@Override
	public int compareTo(Reference o) {
		return block - o.block;
	}

}
