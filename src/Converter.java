import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @author Oliver Griffiths
 * A class to aid in dealing with little endian byte conversions
 */
public class Converter {

	private static ByteBuffer makeBuffer(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer;
	}

	/**
	 * Takes an array of eight bytes or more, converts the first 8 into long
	 * @param bytes The array of bytes
	 * @return The decoded long value
	 */
	public static long bytesToLong(byte[] bytes) {
		return makeBuffer(bytes).getLong();
	}

	/**
	 * Takes an array of eight bytes or more, converts the first 8 between start and end into long
	 * @param bytes The array of bytes
	 * @param start The start point for the conversion
	 * @param end The end point for the conversion
	 * @return The decoded long value
	 */
	public static long bytesToLong(byte[] bytes, int start, int end) {
		return makeBuffer(Arrays.copyOfRange(bytes, start, end)).getLong();
	}

	/**
	 * Takes an array of four bytes or more, converts the first 4 into int
	 * @param bytes The array of bytes
	 * @return The decoded int value
	 */
	public static int bytesToInt(byte[] bytes) {
		return makeBuffer(bytes).getInt();
	}

	/**
	 * Takes an array of four bytes or more, converts the first 4 between start and end into int
	 * @param bytes The array of bytes
	 * @param start The start point for the conversion
	 * @param end The end point for the conversion
	 * @return The decoded int value
	 */
	public static int bytesToInt(byte[] bytes, int start, int end) {
		return makeBuffer(Arrays.copyOfRange(bytes, start, end)).getInt();
	}

	/**
	 * Takes an array of two bytes or more, converts the first 2 into short
	 * @param bytes The array of bytes
	 * @return The decoded short value
	 */
	public static short bytesToShort(byte[] bytes) {
		return makeBuffer(bytes).getShort();
	}

	/**
	 * Takes an array of two bytes or more, converts the first 2 between start and end into short
	 * @param bytes The array of bytes
	 * @param start The start point for the conversion
	 * @param end The end point for the conversion
	 * @return The decoded short value
	 */
	public static short bytesToShort(byte[] bytes, int start, int end) {
		return makeBuffer(Arrays.copyOfRange(bytes, start, end)).getShort();
	}

}