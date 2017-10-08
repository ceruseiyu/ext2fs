import java.util.Formatter;

/**
 * @author Oliver Griffiths
 * A Helper class to aid in coding, testing, demonstration and debugging.
 */
public class Helper {
	/**
	 * Takes an array of bytes, converts it to hex and relevant characters, then prints them
	 * @param bytes Array of bytes to be printed
	 */
	public static void dumpHexBytes(byte[] bytes) {
		int lineCount = 0;
		String hexLine = "";
		String charLine = "";
		for(int i = 0; i < bytes.length; i++) {
			if(lineCount == 12) {
				System.out.println(hexLine + " | " + charLine);
				lineCount = 0;
				hexLine = "";
				charLine = "";
			}
			hexLine = hexLine + " " + getHex(bytes[i]);
			charLine = charLine + " " + getChar(bytes[i]);
			lineCount++;
		}
		System.out.println(hexLine + " | " + charLine);
	}

	private static String getHex(byte original) {
		Formatter formatter = new Formatter();
		formatter.format("%02x", original);
		return formatter.toString();
	}

	private static String getChar(byte original) {
		if(original > 31 && original < 128) {
			return Character.toString((char) (original & 0xFF));	
		}
		return ".";
	}
	/**
	 * Prints all the parameters of a given SuperBlock
	 * @param superBlock The SuperBlock to be printed
	 */
	public static void printSuperBlock(SuperBlock superBlock) {
		System.out.println("Magic Number: " + superBlock.getMagicNumber());
		System.out.println("Inodes: " + superBlock.getInodes());
		System.out.println("Blocks:  " + superBlock.getBlocks());
		System.out.println("Block Size:  " + superBlock.getBlockSize());
		System.out.println("Blocks per Group:  " + superBlock.getGroupBlocks());
		System.out.println("Inodes per Group:  " + superBlock.getGroupInodes());
		System.out.println("Inode Size: " + superBlock.getInodeSize());
		System.out.println("Volume Label: " + superBlock.getVolumeLabel());
	}

	/**
	 * Prints all the paramters of a given GroupDescriptor
	 * @param desc The GroupDescriptor to be printed
	 */
	public static void printDescriptor(GroupDescriptor desc) {
		System.out.println("Block Bitmap Pointer: " + desc.getBlockBitmapPtr());
		System.out.println("Inode Bitmap Pointer: " + desc.getInodeBitmapPtr());
		System.out.println("Inode Table Pointer: " + desc.getInodeTablePtr());
		System.out.println("Free Blocks: " + desc.getFreeBlocks());
		System.out.println("Free Inodes: " + desc.getFreeInodes());
		System.out.println("Used Directories: " + desc.getUsedDirs());
	}

	/**
	 * Prints all the  parameters of a given Inode
	 * @param inode The Inode to be printed
	 */
	public static void printInode(Inode inode) {
		System.out.println("Filemode: " + inode.getFileMode());
		System.out.println("User ID: " + inode.getUserID());
		System.out.println("File Size: " + inode.getFileSize());
		System.out.println("Last Accessed: " + inode.getLastAccess().toString());
		System.out.println("File Created: " + inode.getCreationTime().toString());
		System.out.println("Last Modified: " + inode.getLastModified().toString());
		System.out.println("Deleted Time: " + inode.getDeletedTime().toString());
		System.out.println("Group ID: " + inode.getGroupID());
		System.out.println("Hard Links: " + inode.getHardLinks());
		int[] pointers = inode.getBlockPointers();
		for(int i = 0; i < 12; i++) {
			System.out.println("Block Pointer " + (i + 1) + ": " + pointers[i]);
		}
		System.out.println("Indirect Pointer: " + inode.getIndirectPointer());
		System.out.println("Double Indirect Pointer: " + inode.getDoubleIndirectPointer());
		System.out.println("Triple Indirect Pointer: " + inode.getTripleIndirectPointer());
	}

	/**
	 * Lists the names of the files in a directory, given a FileInfo array
	 * @param files An array of FileInfo describing the files in a directory
	 */
	public static void printFileNames(FileInfo[] files) {
		for (int i = 0; i < files.length; i++)  { 
			System.out.println(files[i].getName());
		}
	}

	/**
	 * Lists the files of a directory in a fashion similar to the unix "ls -l" command
	 * @param files An array of FileInfo describing the files ina  directory
	 */
	public static void ls(FileInfo[] files) {
		for (int i = 0; i < files.length; i++)  {
			String listing = files[i].getMode() + " " + files[i].getLinks() + " " + files[i].getOwnerID() + " " + files[i].getGroupID() + " " + files[i].getSize(); 
			listing = listing + " " + files[i].getModified().toString() + " " + files[i].getName();
			System.out.println(listing);
		}
	}
}