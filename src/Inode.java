import java.util.Date;
/**
 * @author Oliver Griffiths
 * Class to represent an EXT2 Inode
 */
public class Inode {
	//Inode data
	private String fileMode = "";
	private short userID;
	private long fileSize;
	private Date lastAccess;
	private Date creationTime;
	private Date lastModified;
	private Date deletedTime; 
	private short groupID;
	private short hardLinks;
	private int[] blockPointers = new int[12];
	private int indirectPointer;
	private int doubleIndirectPointer;
	private int tripleIndirectPointer;

	private static final int IFSCK = 0xC000;      // Socket
	private static final int IFLNK = 0xA000;      // Symbolic Link
	private static final int IFREG = 0x8000;      // Regular File
	private static final int IFBLK = 0x6000;      // Block Device
	private static final int IFDIR = 0x4000;      // Directory
	private static final int IFCHR = 0x2000;      // Character Device
	private static final int IFIFO = 0x1000;      // FIFO
	private static final int ISUID = 0x0800;      // Set process User ID
	private static final int ISGID = 0x0400;      // Set process Group ID
	private static final int ISVTX = 0x0200;      // Sticky bit
	//User
	private static final int IRUSR = 0x0100; 	//Read
	private static final int IWUSR = 0x0080; 	//Write
	private static final int IXUSR = 0x0040; 	//Execute
	//Group
	private static final int IRGRP = 0x0020; 	// Same as above
	private static final int IWGRP = 0x0010;
	private static final int IXGRP = 0x0008;
	//Others
	private static final int IROTH = 0x0004; 	//Same as above
	private static final int IWOTH = 0x0002;
	private static final int IXOTH = 0x0001;

	/**
	 * Creates a new Inode
	 * @param bytes Array of 128 bytes containing the inode data.
	 */
	public Inode(byte[] bytes) {
		//Initialise all the data for the Inode
		userID = Converter.bytesToShort(bytes, 2, 4);

		//Combine the two halves to get the full 64bit file size
		byte[] sizeArray = {bytes[4], bytes[5], bytes[6], bytes[7],  bytes[108], bytes[109], bytes[110], bytes[111]};
		fileSize = Converter.bytesToLong(sizeArray);

		//Times stored in Date instances
		lastAccess = new Date((0xFFFFFFFF & (long)Converter.bytesToInt(bytes, 8, 12))*1000);
		creationTime = new Date((0xFFFFFFFF & (long)Converter.bytesToInt(bytes, 12, 16))*1000);
		lastModified = new Date((0xFFFFFFFF & (long)Converter.bytesToInt(bytes, 16, 20))*1000);
		deletedTime = new Date((0xFFFFFFFF & (long)Converter.bytesToInt(bytes, 20, 24))*1000);

		groupID = Converter.bytesToShort(bytes, 24, 26);

		hardLinks = Converter.bytesToShort(bytes, 26, 28);

		for(int i = 0; i < 12; i++) {
			blockPointers[i] = Converter.bytesToInt(bytes, 40 + (i * 4), 44 + (i * 4));
		}

		indirectPointer = Converter.bytesToInt(bytes, 88, 92);
		doubleIndirectPointer = Converter.bytesToInt(bytes, 92, 96);
		tripleIndirectPointer = Converter.bytesToInt(bytes, 96, 100);
		
		//Creating the -rwxrwxrwx string by checking the flags
		short modeBytes =  Converter.bytesToShort(bytes, 0, 2);

		//Determine type of file
		if(hasFlag(modeBytes, IFSCK)) {
			fileMode = fileMode + "s";
		} else if(hasFlag(modeBytes, IFLNK)) {
			fileMode = fileMode + "l";
		} else if(hasFlag(modeBytes, IFREG)) {
			fileMode = fileMode + "-";
		} else if(hasFlag(modeBytes, IFBLK)) {
			fileMode = fileMode + "d";
		} else if(hasFlag(modeBytes, IFDIR)) {
			fileMode = fileMode + "d";
		} else if(hasFlag(modeBytes, IFCHR)) {
			fileMode = fileMode + "c";
		} else if(hasFlag(modeBytes, IFIFO)) {
			fileMode = fileMode + "p";
		}


		//Owner permissions
		if(hasFlag(modeBytes, IRUSR)) {
			fileMode = fileMode + "r";
		} else {
			fileMode = fileMode + "-";
		}

		if(hasFlag(modeBytes, IWUSR)) {
			fileMode = fileMode + "w";
		} else {
			fileMode = fileMode + "-";
		}

		if(hasFlag(modeBytes, ISUID) && fileMode.charAt(0) == '-') {
			fileMode = fileMode + "s";
		} else if(hasFlag(modeBytes, IXUSR)) {
			fileMode = fileMode + "x";
		} else {
			fileMode = fileMode + "-";
		}


		//Group Permissions
		if(hasFlag(modeBytes, IRGRP)) {
			fileMode = fileMode + "r";
		} else {
			fileMode = fileMode + "-";
		}

		if(hasFlag(modeBytes, IWGRP)) {
			fileMode = fileMode + "w";
		} else {
			fileMode = fileMode + "-";
		}

		if(hasFlag(modeBytes, ISGID) && (fileMode.charAt(0) == '-' || fileMode.charAt(0) == 'd')) {
			fileMode = fileMode + "s";
		} else if(hasFlag(modeBytes, IXGRP)) {
			fileMode = fileMode + "x";
		} else {
			fileMode = fileMode + "-";
		}


		//Other permissions
		if(hasFlag(modeBytes, IROTH)) {
			fileMode = fileMode + "r";
		} else {
			fileMode = fileMode + "-";
		}

		if(hasFlag(modeBytes, IWOTH)) {
			fileMode = fileMode + "w";
		} else {
			fileMode = fileMode + "-";
		}

		if(hasFlag(modeBytes, IXOTH)) {
			fileMode = fileMode + "x";
		} else {
			fileMode = fileMode + "-";
		}


		//Sticky Bit
		if(hasFlag(modeBytes, ISVTX) && fileMode.charAt(0) == 'd') {
			fileMode = fileMode + "t";
		}
	}

	// Used to compare the pre-defined filemode flags
	private boolean hasFlag(int number, int flag) { 
		if((number & flag) == flag) { //It shortens the flag check statements
			return true;
		}
		return false;
	}

	/**
	 * Returns a string formatted version of the filemode flags
	 * @return -rwrxrwxrwx Formatted filemode
	 */
	public String getFileMode() {
		return fileMode;
	}

	/**
	 * Returns the User ID
	 * @return User ID
	 */
	public short getUserID() {
		return userID;
	}

	/**
	 * Returns the size of the Inode's file
	 * @return File Size
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * Returns date of the last access of the file
	 * @return Last Access time
	 */
	public Date getLastAccess() {
		return lastAccess;
	}

	/**
	 * Returns the time the file was created
	 * @return Creation Time
	 */
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * Returns the time the file was last modified
	 * @return Last Modified Time
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * Returns the time the file was deleted
	 * @return Deletion Time
	 */
	public Date getDeletedTime() {
		return deletedTime;
	}

	/**
	 * Returns the Group ID
	 * @return Group ID
	 */
	public short getGroupID() {
		return groupID;
	}

	/**
	 * Returns the amount of hard links to this file
	 * @return Hard Link Count
	 */
	public short getHardLinks() {
		return hardLinks;
	}

	/**
	 * Returns an array of the first 12 direct block pointers
	 * @return Direct Block Pointers
	 */
	public int[] getBlockPointers() {
		return blockPointers;
	}

	/**
	 * Returns a block pointer to an indirect block
	 * @return Indirect block pointer
	 */
	public int getIndirectPointer() {
		return indirectPointer;
	}

	/**
	 * Returns a block pointer to a double indirect block
	 * @return Double indirect block pointer
	 */
	public int getDoubleIndirectPointer() {
		return doubleIndirectPointer;
	}

	/**
	 * Returns a block pointer to a triple indirect block
	 * @return Triple indirect block pointer
	 */
	public int getTripleIndirectPointer() {
		return tripleIndirectPointer;
	}

}
