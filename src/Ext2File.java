/**
 * @author Oliver Griffiths
 * A class representing an EXT2 file
 */
public class Ext2File {

	private long location;
	private Volume volume;
	private Inode inode;
	/**
	 * Produces an array of bytes from the volume, given start byte and end byte.
	 * @param vol The Volume the file resides in
	 * @param path The path that leads to the file
	 */
	public Ext2File(Volume vol, String path) {
		volume = vol;
		inode = vol.getInodeFromPath(path);
	}

	/**
	 * Produces an array of bytes from the file, given start byte and length.
	 * @param startByte The byte to start reading on
	 * @param length The amount of bytes to read
	 * @return The array of read bytes from the file
	 */
	public byte[] read(long startByte, long length) {
		long newLength = length - startByte;
		seek(startByte);
		return read(newLength);
	}

	/**
	 * Produces an array of bytes from the file at the current pointer location, given the length
	 * @param length The amount of bytes to read
	 * @return The array of read bytes from the file
	 */
	public byte[] read(long length) {
		if((location + length <= inode.getFileSize()) && (location >= 0) && (length > 0)) {
			byte[] bytes = volume.readFromFile(inode, location, location + length);
			location += length;
			return bytes;
		} else{
			System.out.println("Bytes requested out of bounds, file size is: " + inode.getFileSize());
			byte[] failed = {0};
			return failed;
		}
	}

	/**
	 * Places the pointer at the specificed place in the file
	 * @param place The byte to place the pointer on
	 */
	public void seek(long place) {
		location = place;
	}

	/**
	* Returns the current position of the pointer
	* @return Pointer position
	*/
	public long position() {
		return location;
	}

	/**
	* Returns the size of the file
	* @return Filesize
	*/
	public long size() {
		return inode.getFileSize();
	}

	/**
	* Returns the Inode of the file
	* @return The file's Inode
	*/
	public Inode getInode() {
		return inode;
	}
}