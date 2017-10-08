/**
 * @author Oliver Griffiths
 * Class to represent an EXT2 Directory
 */
public class Directory {
	private Volume vol;
	private Inode inode;
	/**
	 * Creates a new Directory, given an inode and the volume
	 * @param i The inode of the directory
	 * @param v The volume the directory resides in
	 */
	public Directory(Inode i, Volume v) {
		vol = v;
		inode = i;
	}

	/**
	 * Creates a new Directory, the volume and the path to the directory
	 * @param v The volume the directory resides in
	 * @param path The file path to the directory
	 */
	public Directory(Volume v, String path) {
		vol = v;
		inode = vol.getInodeFromPath(path);
	}

	/**
	 * Returns an array of FileInfo, to the length of the amount of files in the directory
	 * @return Infromation about all the files in the directory
	 */
	public FileInfo[] getFileInfo() {
		int length = getLength();
		FileInfo[] files = new FileInfo[length];

		int count = 0;
		for(int i = 0; i < length; i++) {
			int fileInodePtr = Converter.bytesToInt(vol.readFromFile(inode, count, count+4));
			Inode fileInode = vol.getInode(fileInodePtr);
			String fileName = "";
			byte nameLength = vol.readFromFile(inode, count+6, count+7)[0];
			for (int j = 0; j < nameLength; j++) { // Reading in the name of the file one character at a time
				fileName = fileName + Character.toString((char) (vol.readFromFile(inode, count+8+j, count+9+j)[0] & 0xFF));
			}
			
			files[i] = new FileInfo(fileInode, fileName, fileInodePtr);
			short point = Converter.bytesToShort(vol.readFromFile(inode, count+4, count+6));
			count = count + point;
		}
		return files;
		
	}

	//The amount of files inside the directory needs to be found to initialise a FileInfo array
	private int getLength() { 
		int count = 0;
		int length = 0;
		while(count < inode.getFileSize()) { //Follow the pointers and increment the counter
			short point = Converter.bytesToShort(vol.readFromFile(inode, count+4, count+6)); 
			length++;
			count = count + point;
		}
		return length;
	}
} 