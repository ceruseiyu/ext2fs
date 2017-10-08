import java.util.Date;

/**
 * @author Oliver Griffiths
 * Represents the information of an EXT2 File, read from a directory.
 */
public class FileInfo {
	private String mode;
	private int links;
	private short ownerID;
	private short groupID;
	private long size;
	private Date modified;
	private String filename;
	private int inodePtr;

	/**
	 * Creates a new FileInfo 
	 * @param inode The inode of the file
	 * @param name The name of the file
	 * @param ptr Pointer to the inode of the file
	 */
	public FileInfo(Inode inode, String name, int ptr) {
		mode = inode.getFileMode();
		links = inode.getHardLinks();
		ownerID = inode.getUserID();
		groupID = inode.getGroupID();
		size = inode.getFileSize();
		modified = inode.getLastModified();
		filename = name;
		inodePtr = ptr;
	}

	/**
	 * Returns the String formatted filemode
	 * @return The filemode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * Returns the amount of hard links to the file
	 * @return Hard link count
	 */
	public int getLinks() {
		return links;
	}


	/**
	 * Returns the ID of the owner of the file
	 * @return Owner ID
	 */
	public short getOwnerID() {
		return ownerID;
	}

	/**
	 * Returns the ID of the gorup of the file
	 * @return Group ID
	 */
	public short getGroupID() {
		return groupID;
	}

	/**
	 * Returns the size of the file in bytes
	 * @return Filesize
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Returns the date the file was modified
	 * @return Last Modified Time
	 */
	public Date getModified() {
		return modified;
	}

	/**
	 * Returns the name of the file
	 * @return Filename
	 */
	public String getName() {
		return filename;
	}

	/**
	 * Returns the pointer to the Inode of the file
	 * @return Inode pointer
	 */
	public int getInodePtr() {
		return inodePtr;
	}
}