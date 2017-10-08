public class Driver {
	public static void main(String[] args) {
		Volume vol = new Volume("ext2fs");
		//Ext2File file = new Ext2File(vol, "/files/trpl-ind-s");

		//Example print of file
		Ext2File file = new Ext2File(vol, "/two-cities");
		byte buf[ ] = file.read(0L, file.size());
        System.out.format ("%s\n", new String(buf));


		//Helper.printInode(file.getInode());

		//Helper.printSuperBlock(vol.getSuperBlock());

		//Helper.dumpHexBytes(file.read(0L, 1024L)); //Print first block
		//Helper.dumpHexBytes(file.read(file.size() - 1024L, file.size())); //Print Second block

		//Directory dir = new Directory(vol.getInode(2), vol);
		//Helper.ls(dir.getFileInfo());
		//Helper.printDescriptor(vol.getDescriptor(2));
		//Helper.printInode(vol.getInode(3425));
		//Directory dir = new Directory(vol, "/files");
		//Helper.ls(dir.getFileInfo());
	}
}