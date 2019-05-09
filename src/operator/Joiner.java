/**
 * 
 */
package operator;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import datastructure.HashBucket;
import datastructure.Reference;
import process.Calculator;
import utils.Block;
import utils.Buffer;
import utils.ExtMem;

/**
 * @author standingby
 *
 */
public class Joiner {
	private Buffer buffer;

	public Joiner(Buffer buffer) {
		super();
		this.buffer = buffer;
	}

	public List<Integer> nestLoopJoin(List<Integer> addrR, List<Integer> addrS) {
		buffer.free();
		int basicIO = buffer.getIOCounter();
		List<Integer> result = new ArrayList<>();
		// ��ַ
		int base = Calculator.JOINBASE + 10000;
		Block output = buffer.getNewBlockInBuffer();
		Block blockS;
		List<Block> blockR = new ArrayList<>();

		for (int iaddr = 0; iaddr < addrR.size();) {
			// ��������뻺����������1���S
			int len = buffer.getBlockFreeNumber() - 1;
			len = len + iaddr >= addrR.size() ? addrR.size() - iaddr : len;
			blockR.clear();
			for (int j = 0; j < len; j++) {
				blockR.add(buffer.readBlockFromDisk(addrR.get(iaddr + j)));
			}
			iaddr += len;

			for (Integer saddr : addrS) {
				blockS = buffer.readBlockFromDisk(saddr);

				// Join
				for (Block block : blockR) {
					// ���������е�R��
					for (int i = 0; i < 7; i++) {
						// ��R������

						for (int j = 0; j < 7; j++) {
							// ���� S������
							if (blockS.data[i * 2] == block.data[j * 2]) {
								output.writeData(block.data[j * 2]);
								output.writeData(block.data[j * 2 + 1]);
								output.writeData(blockS.data[i * 2]);
								output.writeData(blockS.data[i * 2 + 1]);
								if (output.getIndex() == 12) {
									output.writeData(0);
									output.writeData(0);
									output.writeData(base + 1);
									buffer.writeBlockToDisk(output, base);
									result.add(base);
									output = buffer.getNewBlockInBuffer();
									base++;
								}
							}
						}
					}
				}
				buffer.freeBlockInBuffer(blockS);
			} // end S
			for (Block block : blockR) {
				buffer.freeBlockInBuffer(block);
			}
		} // end R

		if (!output.isEmpty()) {
			buffer.writeBlockToDisk(output, base);
			result.add(base);
		}

		System.out.println("Nest-Loop-Join with I/O : " + (buffer.getIOCounter() - basicIO));
		return result;
	}

	public List<Integer> sortMergeJoin(List<Integer> addrR, List<Integer> addrS) {
		buffer.free();
		int basicIO = buffer.getIOCounter();
		List<Integer> result = new ArrayList<>();
		// ��ַ
		int base = Calculator.JOINBASE + 20000;

		Map<Integer, Block> temp = new HashMap<>();

		Block output = buffer.getNewBlockInBuffer();
		Block inputR;
		Block inputS = buffer.readBlockFromDisk(addrS.get(0));
		Block preS = buffer.readBlockFromDisk(addrS.get(0));

		int preKey = inputS.data[0];
		int curKey = inputS.data[0];
		int preAddr = 0;
		int curAddr = 0;
		int preindex = 0;
		int curindex = 0;

		boolean end = false;

		for (Integer r : addrR) {
			if (end) {
				break;
			}
			inputR = buffer.readBlockFromDisk(r);
			for (int i = 0; i < 7 && !end; i++) {
				// ˳����� R ��ÿ��Ԫ��
				int rKey = inputR.data[i * 2];

				for (;; curindex++) {
					if (curindex == 7) {
						if (curAddr + 1 != addrS.size()) {
							// ƽ��
							// ������ȡ��һ�� S block
							if (inputS.id != preS.id) {
								buffer.freeBlockInBuffer(inputS);
							}
							if (temp.containsKey(curAddr + 1)) {
								// �л���
								inputS = temp.get(++curAddr);
							} else {
								inputS = buffer.readBlockFromDisk(addrS.get(++curAddr));
							}
							curindex = 0;
						} else {
							// ����
							// S ����
							if (rKey > curKey) {
								end = true;
								break;
							} else {
								// ����
								if (inputS.id != preS.id) {
									// ����ͬһ�����̿飬��ռͬһƬ����
									// buffer.freeBlockInBuffer(sid);
									// ������ǰ��
									temp.put(curAddr, inputS);
								}
								inputS = preS;
								curindex = preindex;
								curAddr = preAddr;
							}
						}

					}

					// ��ȡ s
					curKey = inputS.data[2 * curindex];

					if (rKey > curKey) {
						// s��С��������ȡ��һ��s
					} else

					if (rKey == curKey) {
						if (preKey != rKey) {
							// ����preKey
							int preid = preS.id;
							preKey = rKey;
							preAddr = curAddr;
							preS = inputS;
							preindex = curindex;
							if (preid != inputS.id) {
								buffer.freeBlockInBuffer(preid);
							}
						}
						output.writeData(inputR.data[i * 2]);
						output.writeData(inputR.data[i * 2 + 1]);
						output.writeData(inputS.data[curindex * 2]);
						output.writeData(inputS.data[curindex * 2 + 1]);
						if (output.getIndex() == 12) {
							output.writeData(0);
							output.writeData(0);
							output.writeData(base + 1);
							buffer.writeBlockToDisk(output, base);
							result.add(base);
							output = buffer.getNewBlockInBuffer();
							base++;
						}
						// ������s

					} else

					if (rKey < curKey) {
						if (rKey == preKey) {
							if (inputS.id != preS.id) {
								// ����ͬһ�����̿飬��ռͬһƬ����
								// buffer.freeBlockInBuffer(sid);
								// ������ǰ��
								temp.put(curAddr, inputS);
							}
							inputS = preS;
							curindex = preindex;
							curAddr = preAddr;
						} else {
							// ����pre
							if (preS.id != inputS.id) {
								buffer.freeBlockInBuffer(preS.id);
							}
							preAddr = curAddr;
							preS = inputS;
							preindex = curindex;
						}
						// ��ȡ��һ��r
						break;
					}
				}

			} // end r
			buffer.freeBlockInBuffer(inputR);
		}

		if (!output.isEmpty()) {
			buffer.writeBlockToDisk(output, base);
			result.add(base);
		}

		System.out.println("Sort-Merge-Join with I/O : " + (buffer.getIOCounter() - basicIO));
		return result;
	}

	public List<Integer> hashJoin(Map<Integer, List<Reference>> rBuckets, Map<Integer, List<Reference>> sBuckets) {
		buffer.free();
		int basicIO = buffer.getIOCounter();
		List<Integer> result = new ArrayList<>();
		// ��ַ
		int base = Calculator.JOINBASE + 30000;
		Block output = buffer.getNewBlockInBuffer();
		Map<Integer, Block> inputRs = new HashMap<>();
		Block inputR;
		Block inputS;

		for (Integer r : rBuckets.keySet()) {
			if (!sBuckets.containsKey(r)) {
				// s buckets �޴�key��������
				continue;
			}

			List<Reference> rRefs = rBuckets.get(r);
			Collections.sort(rRefs);

			// ������Ķ���R
			for (int i = 0; i < rRefs.size();) {
				for (Block block : inputRs.values()) {
					// ��ջ���
					buffer.freeBlockInBuffer(block);
				}
				inputRs.clear();

				int len = buffer.getBlockFreeNumber() - 1; // ����len���¿�
				len = len + i > rRefs.size() ? rRefs.size() - i : len;

				for (int j = 0; j < len && i < rRefs.size(); i++) {
					// j ��¼�������ʹ����
					if (inputRs.containsKey(rRefs.get(i).block)) {
						// �Ѽ���
						continue;
					} else {
						inputRs.put(rRefs.get(i).block, buffer.readBlockFromDisk(rRefs.get(i).block));
						j++;
					}
				}

				for (Reference sRef : sBuckets.get(r)) {
					inputS = buffer.readBlockFromDisk(sRef.block);

					for (Reference rRef : rRefs) {
						if (!inputRs.containsKey(rRef.block)) {
							// ��������δ������
							continue;
						}
						inputR = inputRs.get(rRef.block);

						if (inputR.data[rRef.index] == inputS.data[sRef.index]) {
							output.writeData(inputR.data[rRef.index]);
							output.writeData(inputR.data[rRef.index + 1]);
							output.writeData(inputS.data[sRef.index]);
							output.writeData(inputS.data[sRef.index + 1]);
							if (output.getIndex() == 12) {
								output.writeData(0);
								output.writeData(0);
								output.writeData(base + 1);
								buffer.writeBlockToDisk(output, base);
								result.add(base);
								output = buffer.getNewBlockInBuffer();
								base++;
							}
						}
					}
					buffer.freeBlockInBuffer(inputS);
				}
			}
		}
		if (!output.isEmpty()) {
			buffer.writeBlockToDisk(output, base);
			result.add(base);
		}
		System.out.println("Hash-Join with I/O : " + (buffer.getIOCounter() - basicIO));
		return result;
	}

	public static void main(String[] args) {
		// nest-loop-join
		// System.out.println(new Joiner(ExtMem.getDefaultBuffer()).nestLoopJoin(
		// Calculator.getAddrList("R", false), Calculator.getAddrList("S", false)));

		// System.out.println(new Joiner(ExtMem.getDefaultBuffer()).sortMergeJoin(
		// Calculator.getAddrList("R", true), Calculator.getAddrList("S", true)));

		Buffer buffer = ExtMem.getDefaultBuffer();
		System.out.println(
				new Joiner(buffer).hashJoin(new HashBucket(Calculator.getAddrList("R", false), buffer).getHashBuckets(),
						new HashBucket(Calculator.getAddrList("S", false), buffer).getHashBuckets()));

	}

}
