import java.io.IOException;
import java.io.RandomAccessFile;
/**
 * @author Oliver Griffiths
 * A class representing an EXT2 volume.
 */
public class Volume {
	private String volumeName;
	private SuperBlock superBlock;

	//Declare constants
	private final long BOOT_OFFSET = 1024L;
	private final long BLOCK_SIZE;
	private final long INDIRECT_POINTERS; // Ointer numbers used to calculate limits
	private final long DOUBLE_INDIRECT_POINTERS;
	private final long TREBLE_INDIRECT_POINTERS;
	private final long BLOCK_DESCRIPTOR_SIZE = 32L;
	private final long DIRECT_LIMIT;
	private final long INDIRECT_LIMIT;
	private final long DOUBLE_INDIRECT_LIMIT;
	private final long TREBLE_INDIRECT_LIMIT;
	private final long INODE_SIZE;
	private final int ROOT_INODE_PTR = 2;
	/**
	 * Creates a new volume from a given file
	 * @param filename The relative or absolute path of the file to be represented as a volume
	 */
	public Volume(String filename) { 
		volumeName = filename;
		superBlock = new SuperBlock(getBytes(1024, 2048));

		BLOCK_SIZE = 1024 * (long)Math.pow(2, superBlock.getBlockSize());

		//Calculate limits forlater use in indirection

		INDIRECT_POINTERS = BLOCK_SIZE / 4;
		DOUBLE_INDIRECT_POINTERS = INDIRECT_POINTERS * INDIRECT_POINTERS;
		TREBLE_INDIRECT_POINTERS = DOUBLE_INDIRECT_POINTERS * INDIRECT_POINTERS;

		DIRECT_LIMIT = BLOCK_SIZE * 12;
		INDIRECT_LIMIT = BLOCK_SIZE * INDIRECT_POINTERS;
		DOUBLE_INDIRECT_LIMIT = INDIRECT_LIMIT * INDIRECT_POINTERS;
		TREBLE_INDIRECT_LIMIT = DOUBLE_INDIRECT_LIMIT * INDIRECT_POINTERS;

		INODE_SIZE = 0xFFFFFFFF & (long)superBlock.getInodeSize();
	}

	/**
	 * Produces an array of bytes from the volume, given start byte and end byte.
	 * @param start The byte to start reading on
	 * @param end The byte to finish reading on
	 * @return The array of read bytes from the volume
	 */
	public byte[] getBytes(long start, long end) {
		byte[] bytes;
		try {
			bytes = getTrueBytes(start, end);
		} catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
			bytes = new byte[1];
			bytes[1] = 0; 
		}
		return bytes;
	}

	private byte[] getTrueBytes(long start, long end) throws IOException {
		final int length = (int) (end - start); // We can error check this later
		byte[] bytes = new byte[length];
		RandomAccessFile file = new RandomAccessFile(volumeName, "r");
		file.seek(start);
		for(int i = 0; i < length; i++) {
			bytes[i] = file.readByte(); //Read the bytes in one by one
		}
		return bytes;
	}

	/**
	 * Returns the SuperBlock currently in use on the volume
	 * @return The volume's SuperBlock instance
	 */
	public SuperBlock getSuperBlock() {
		return superBlock;
	}

	/**
	 * Returns the byte location of a block on the volume
	 * @param block A pointer to a block
	 * @return The location of a block, in bytes
	 */
	public long getBlockLocation(int block) {
		return block * BLOCK_SIZE;
	}

	/**
	 * Returns Block Group Descriptor for a given Block Group
	 * @param blockGroup The Block Group of the descriptor
	 * @return The GroupDescriptor of the given Block Group
	 */
	public GroupDescriptor getDescriptor(int blockGroup) {
		long location = BOOT_OFFSET + BLOCK_SIZE + (32 * blockGroup); // Retrieve the requested Descriptor from the Descriptor Table
		GroupDescriptor groupDescriptor = new GroupDescriptor(getBytes(location, location+BLOCK_DESCRIPTOR_SIZE));
		return groupDescriptor; 
	}

	/**
	 * Returns a given Inode, given the pointer ID of it
	 * @param id The pointer/ID for the Inode
	 * @return The Inode matching the id
	 */
	public Inode getInode(int id) {
		int blockGroup = id / superBlock.getGroupInodes();
		id = id % superBlock.getGroupInodes();
		long location = getBlockLocation(getDescriptor(blockGroup).getInodeTablePtr()) + ((id - 1) * INODE_SIZE);
		Inode inode = new Inode(getBytes(location, location+INODE_SIZE));
		return inode;
	}

	/**
	 * Returns the Inode of a file, given the path to it
	 * @param path The path leading to the desired file
	 * @return The inode of the file defined by the path
	 */
	public Inode getInodeFromPath(String path) {
		Inode inode = getInode(ROOT_INODE_PTR); //Root is returned if path is invalid
		if(path.equals("")) {
			return inode;
		}
		if(path.charAt(0) == '/') {
			path = path.substring(1); // If there is "/" at the beginning, it adds an extra element to the array, so remove it
		}
		String[] names = path.split("/"); // Get array of file names
		Directory directory = new Directory(getInode(ROOT_INODE_PTR), this);
		for (int i = 0; i < names.length; i++) {
			FileInfo[] files = directory.getFileInfo(); // Get the information for all the files from the directory
			for (int j = 0; j < files.length; j++) { // Check all files in the directory
				if(files[j].getName().equals(names[i])) { // Check if we found a filename match
					if(i < names.length - 1) { // If we're not at the destination file
						directory = new Directory(getInode(files[j].getInodePtr()), this); // Switch directory
					} else{ // Otherwise we get the Inode for that file.
						inode = getInode(files[j].getInodePtr());
						return inode;
					}
					break;
				}
			}
		}
		System.out.println("Could not find file, returning root!");
		return inode;
	}

	/**
	 * Returns an array of bytes, given the desired start and end point in a file, and the file's Inode
	 * @param inode The inode of the file
	 * @param start The start byte in the file for reading
	 * @param end The stopping byte in the file when reading
	 * @return The Inode matching the id
	 */
	public byte[] readFromFile(Inode inode, long start, long end) {
		int length = (int) (end - start);
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) { //Assemble array of bytes
			bytes[i] = getByteFromFile(start + i, inode); //Retrieve single byte
		}
		return bytes;
	}

	private byte getByteFromFile(long location, Inode inode) { //Decide which function to call to get the byte
		if(location < DIRECT_LIMIT) { //Call relevant function for the current byte requested in the file
			return getDirectByte(location, inode);
		} else if(location < (INDIRECT_LIMIT + DIRECT_LIMIT)) {
			return getIndirectByte(location, inode);
		} else if(location < (DOUBLE_INDIRECT_LIMIT + INDIRECT_LIMIT + DIRECT_LIMIT)) {
			return getDoubleIndirectByte(location, inode);
		} else if(location < (TREBLE_INDIRECT_LIMIT + DOUBLE_INDIRECT_LIMIT + INDIRECT_LIMIT + DIRECT_LIMIT)) {
			return getTrebleIndirectByte(location, inode);
		} else{
			byte failed = 0;
			System.out.println("Failed! Byte requested is larger than maximum allowed EXT2 file.");
			return failed;
		}
 	}

 	//Get a byte from one of the direct pointers
 	private byte getDirectByte(long location, Inode inode) {
 		int block = (int)(location/BLOCK_SIZE);
 		int pointer = inode.getBlockPointers()[block];

 		if(pointer == 0) {//Deal with holes/sparse files
 			return 0;
 		}

 		long newLocation = getBlockLocation(inode.getBlockPointers()[block]) + (location % BLOCK_SIZE); // Calculate the place of the byte
 		return getBytes(newLocation, newLocation+1)[0]; //Read and return it
 	}

 	/* Essential function. Takes a pointer to an indirect table and a desired block pointer in it, 
	   then returns that pointer.
	  */
 	private int getBlockFromIndirect(int pointer, long block) { 
 		long pointerBlock = getBlockLocation(pointer);
 		long pointLocation = pointerBlock + (block * 4); // Find the pointer inside
 		int blockLocation = Converter.bytesToInt(getBytes(pointLocation, pointLocation + 4)); // Convert bytes to int
 		return blockLocation;
 	}

 	// Get a byte from the indirect table
 	private byte getIndirectByte(long location, Inode inode) {
 		location = location - DIRECT_LIMIT; // Offset from Direct
 		int directBlockPtr = getBlockFromIndirect(inode.getIndirectPointer(), location / BLOCK_SIZE);

 		if(directBlockPtr == 0) { //Deal with holes/sparse files
 			return 0;
 		}

 		long newBlock = getBlockLocation(directBlockPtr); // Calculate pointer location number, then block location
 		long byteLocation = newBlock + (location % BLOCK_SIZE); 
 		return getBytes(byteLocation, byteLocation+1)[0];
 	}

 	// Get a byte using the double indirect table
 	private byte getDoubleIndirectByte(long location, Inode inode) {
 		location = location - (DIRECT_LIMIT + INDIRECT_LIMIT); // Offset from Indirect

 		int indirectBlockPtr = getBlockFromIndirect(inode.getDoubleIndirectPointer(), location / INDIRECT_LIMIT);
 		long scaledLocation = location % INDIRECT_LIMIT; // Calculate to get the indirect table

 		int directBlockPtr = getBlockFromIndirect(indirectBlockPtr, scaledLocation / BLOCK_SIZE); // Get the direct block

 		if(directBlockPtr == 0) {//Deal with holes/sparse files
 			return 0;
 		}

 		Long newBlock = getBlockLocation(directBlockPtr);

 		long byteLocation = newBlock + (location % BLOCK_SIZE);
 		return getBytes(byteLocation, byteLocation+1)[0];
 	}

 	// Get a byte using the treble indirect table
  	private byte getTrebleIndirectByte(long location, Inode inode) {
  		location = location - (DIRECT_LIMIT + INDIRECT_LIMIT + DOUBLE_INDIRECT_LIMIT); // Offset from Double Indirect

  		int doubleIndPtr = getBlockFromIndirect(inode.getTripleIndirectPointer(), location / DOUBLE_INDIRECT_LIMIT); //Calculate for Double Indirect table
  		long firstScale = location % DOUBLE_INDIRECT_LIMIT;

 		int indirectBlockPtr = getBlockFromIndirect(doubleIndPtr, firstScale / INDIRECT_LIMIT);
 		long secondScale = firstScale % INDIRECT_LIMIT; // Calculate for indirect table

 		int directBlockPtr = getBlockFromIndirect(indirectBlockPtr, secondScale / BLOCK_SIZE); // Get direct block

 		if(directBlockPtr == 0) {//Deal with holes/sparse files
 			return 0;
 		}

 		Long newBlock = getBlockLocation(directBlockPtr);

 		long byteLocation = newBlock + (location % BLOCK_SIZE);
 		return getBytes(byteLocation, byteLocation+1)[0];
 	}
}