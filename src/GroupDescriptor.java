/**
 * @author Oliver Griffiths
 * Class to represent an EXT2 Group Descriptor
 */
public class GroupDescriptor {
	private int blockBitmapPtr;
	private int inodeBitmapPtr;
	private int inodeTablePtr;
	private short freeBlocks;
	private short freeInodes;
	private short usedDirectories;

	/**
	 * Creates a new Group Descriptor, given the 32 bytes that make it up
	 * @param bytes Array of bytes that make up the Group Descriptor
	 */
	public GroupDescriptor(byte[] bytes) {
		blockBitmapPtr = Converter.bytesToInt(bytes, 0, 4);
		inodeBitmapPtr = Converter.bytesToInt(bytes, 4, 8);
		inodeTablePtr = Converter.bytesToInt(bytes, 8, 12);
		freeBlocks = Converter.bytesToShort(bytes, 12, 14);
		freeInodes = Converter.bytesToShort(bytes, 14, 16);
		usedDirectories = Converter.bytesToShort(bytes, 16, 18);
	}

	/**
	 * Returns the pointer to the block group's Block bitmap table
	 * @return Block Bitmap Table Pointer
	 */
	public int getBlockBitmapPtr() {
		return blockBitmapPtr;
	}

	/**
	 * Returns the pointer to the block group's Inode bitmap table
	 * @return Inode Bitmap Table Pointer
	 */
	public int getInodeBitmapPtr() {
		return inodeBitmapPtr;
	}

	/**
	 * Returns the pointer to the block group's Inode Table
	 * @return Inode Table Pointer
	 */
	public int getInodeTablePtr() {
		return inodeTablePtr;
	}

	/**
	 * Returns the amount of free blocks in the group
	 * @return Free Blocks
	 */
	public short getFreeBlocks() {
		return freeBlocks;
	}

	/**
	 * Returns the amount of free Inodes in the group
	 * @return Free Inodes
	 */
	public short getFreeInodes() {
		return freeInodes;
	}

	/**
	 * Returns the amount of used directories in the group
	 * @return Used Directories
	 */
	public short getUsedDirs() {
		return usedDirectories;
	}
}