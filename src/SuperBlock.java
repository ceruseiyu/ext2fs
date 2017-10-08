/**
 * @author Oliver Griffiths
 * Class to represent an EXT2 SuperBlock
 */
public class SuperBlock {
	private short magicNumber;
	private int inodes;
	private int blocks;
	private int blockSize;
	private int groupBlocks;
	private int groupInodes;
	private int inodeSize;
	private String volumeLabel;

	/**
	 * Creates a new SuperBlock given the bytes that make up it
	 * @param bytes Block of 1024 bytes containing the information for the SuperBlock
	 */
	public SuperBlock(byte[] bytes) {
		inodes = Converter.bytesToInt(bytes, 0, 4); 
		blocks = Converter.bytesToInt(bytes, 4, 8);
		blockSize = Converter.bytesToInt(bytes, 24, 28);
		groupBlocks = Converter.bytesToInt(bytes, 32, 36);
		groupInodes = Converter.bytesToInt(bytes, 40, 44);
		magicNumber = Converter.bytesToShort(bytes, 56, 58); 
		inodeSize = Converter.bytesToShort(bytes, 88, 92); 

		volumeLabel = "";
		for (int i = 0; i < 16; i++) {
			volumeLabel = volumeLabel + getChar(bytes[120 + i]);
		}
	}

	private String getChar(byte original) {
		if(original != 0) {
			return Character.toString((char) (original & 0xFF));
		}
		return "";
	}

	/**
	 * Returns the magic number of the SuperBlock
	 * @return Magic number
	 */
	public short getMagicNumber() {
		return magicNumber;
	}

	/**
	 * Returns the total number of inodes in the volume
	 * @return Inode count
	 */
	public int getInodes() {
		return inodes;
	}

	/**
	 * Returns the total number of blocks in the volume
	 * @return Block count
	 */
	public int getBlocks() {
		return blocks;
	}

	/**
	 * Returns the size of the blocks in the volume.
	 * @return Size n. Where Block size is 1024 * 2^n
	 */
	public int getBlockSize() {
		return blockSize; 
	}

	/**
	 * Returns the amount of blocks in each block group
	 * @return Block group blocks
	 */
	public int getGroupBlocks() {
		return groupBlocks;
	}

	/**
	 * Returns the amount of inodes in each block group
	 * @return Block group inodes
	 */
	public int getGroupInodes() {
		return groupInodes;
	}

	/**
	 * Returns the size of inodes in bytes
	 * @return Block group blocks
	 */
	public int getInodeSize() {
		return inodeSize;
	}

	/**
	 * Returns the label for the volume
	 * @return Volume Label
	 */
	public String getVolumeLabel() {
		return volumeLabel;
	}
}