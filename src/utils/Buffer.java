/**
 * 
 */
package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author standingby
 *
 */
public class Buffer {
    private int ioCounter=0;
    private int blockTotalNumber;
    private int blockFreeNumber;
    
    /** �洢���ݿ� */
    public List<Block> blocks = new ArrayList<>();
    
    
    protected Buffer(int bufferSize, int blockSize) {
        this.blockTotalNumber = bufferSize / (blockSize+1);
        this.blockFreeNumber = blockTotalNumber;
        
        // ��ʼ��block 
        for(int i=0;i<blockTotalNumber;i++) {
            blocks.add(new Block(i,blockSize));
        }
    }
    
    /**
     * �ͷŻ���
     */
    public void free() {
        for (Block block : blocks) {
            block.free=true;
        }
    }
    
    /**
     * ��ÿ�
     * @return �¿�����index
     */
    public Block getNewBlockInBuffer() {
        if (blockFreeNumber==0) {
            return null;
        }
        for(int i=0;i<blockTotalNumber;i++) {
            if (blocks.get(i).free) {
                blocks.get(i).free=false;
                blockFreeNumber--;
                return blocks.get(i);
            }
        }
        return null;
    }
    
    public void freeBlockInBuffer(int index) {
        if (!blocks.get(index).free) {
            blocks.get(index).free=true;
            blockFreeNumber++;
        }
    }
    
    public void freeBlockInBuffer(Block block) {
        if (!block.free) {
            block.free=true;
            blockFreeNumber++;
        }
    }
    
    
    public Block readBlockFromDisk(int addr) {
        if (blockFreeNumber==0) {
            System.err.println("Buffer Overflows!");
            return null;
        }
        
        // ��ѯ�տ�
        Block block=null;
        for(int i=0;i<blockTotalNumber;i++) {
            if (blocks.get(i).free) {
                block=blocks.get(i);
                break;
            }
        }
        if (block==null) { // �޿տ�
            return null;
        }
        
        // ��ȡ�ļ����ݵ�block
        String filename= ExtMem.DISKADDR+addr+".blk";
        try {
            List<String> strings = Files.readAllLines(Paths.get(filename));
            //  parse to block
            String[] line = strings.get(0).split(",");
            for(int i=0;i<line.length && i<block.data.length;i++) {
                block.data[i] = Integer.valueOf(line[i]);
            }
            block.free=false;
            blockFreeNumber--;
            ioCounter ++;
            return block;
        } catch (IOException e) {
            System.err.println("Reading Block Failed! :"+filename);
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean writeBlockToDisk(Block block,int addr) {
        String filename= ExtMem.DISKADDR+addr+".blk";
        File file = new File(filename);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(blocks.toString());     
            writer.close();
            block.free = true;
            blockFreeNumber++;
            ioCounter++;
            return true;
        } catch (IOException e) {
            System.err.println("Reading Block Failed! :"+filename);
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean writeBlockToDisk(int index , int addr) {
        return writeBlockToDisk(blocks.get(index), addr);
    }
    
    public int getIOCounter() {
        return ioCounter;
    }
    
}


