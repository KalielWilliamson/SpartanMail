import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.zip.Deflater;

/**	program execution in 3.5317 milliseconds per kilobyte.
	3.6 seconds per megabyte.
	3700 seconds per gigabyte.
*/
/*As of now (9/25/2015) I have correctly applied Shamir's Secret Sharing.
 * 1) I now have to check to see if the class can take in bytes, break them up,
 * then save 9-bit size shares to files. 
 * 2) I also have to see if it can also read
 * 9-bits at a time of a file and reconstruct the secret from several files.
 * 3) I also have to make sure that it works for 2 data types: text from the message
 * and files that are attached.
 * 4) Get an example of share break up and reconstruct by hand to make sure the
 * reconstruction works properly. If it does great, if not fix it. If that's not the 
 * issue, then the issue must be share construction, or the break up of bits. Check to
 * make sure that it is possible to get 256 as a share value. If not, then that must
 * mean something with your bit manipulation is wrong.
 */

public class SSS {
	static int numberOfShares = 0;
	static byte[] secret = null;
	static int[][] shares = null;
	int[][] messageShares = null;
	static int[][] fileShares = null;
	static int[][][] severalFileShares = null;
	static BitSet shareBitSet = null; 
	static int primeNumber = 257;
	static int numNegative = 0;
	public SSS(String inputsecret, int number) throws IOException {
		secret = inputsecret.getBytes();
		numberOfShares = number;
		//figure out what to do with message input
		messageShares = shareGenerator(secret,numberOfShares);
	}
	/**
	 * 
	 * @param secret
	 * @param number
	 * @throws IOException
	 */
	public SSS(String inputsecret, File file, int number) throws IOException {
		secret = inputsecret.getBytes();
		numberOfShares = number;
		//figure out what to do with message input
		messageShares = shareGenerator(secret,numberOfShares);
		fileShares = shareGenerator(readFileBytes(file),numberOfShares);
	}
	public SSS(File file, int number) throws IOException {
		numberOfShares = number;
		//figure out what to do with message input
		fileShares = shareGenerator(readFileBytes(file),numberOfShares);
		System.out.println("file share # 5: "+fileShares[5].length);
	}
	public SSS(String secret, File[] file, int number) throws IOException {
		numberOfShares = number;
		//figure out what to do with message input
		messageShares = shareGenerator(secret.getBytes(),numberOfShares);
		for(int i = 0; i < file.length; i++)
		{
			severalFileShares[i] = shareGenerator(readFileBytes(file[i]),numberOfShares);
		}
	}
	/**
	 * [Bit stream]->[Byte array]->[Byte]->[Share generator]->[shares] & [primeNumber]
	 * @param input
	 * @throws IOException
	 */
	public SSS(File[] inputFiles, String message) throws IOException 
	{
		/*Create a bit array for each input. The BitSet object is a
		 * set of binary numbers. An array of that is therefore
		 * an array of bit sets.
		 */
		BitSet[] bitStream = new BitSet[inputFiles.length + 1];
		/*2D array of shares where the rows are all the files plus
		 * one for the String message. The number of columns is based
		 * on the size each input file and then the message.
		 */
		shares = new int[inputFiles.length + 1][1];
		for(int i = 0; i < inputFiles.length; i++) 
		{
			shares[i] = new int[(int)(serialize(inputFiles[i]).length*8)/9 + 1];
		}
		shares[inputFiles.length + 1] = new int[message.getBytes().length + 1];
		
		/* For the bitstream, the set which holds files. It takes 9 bits at a time
		 * of the bitstream and saves it as a number (2 bytes, 16 bits) therefore, 
		 * it adds 7 0's to the end.
		 */
		for(int i = 0; i < inputFiles.length; i++) {
			bitStream[i] = BitSet.valueOf(serialize(inputFiles[i]));
			for(int a = 9; a < bitStream[i].size(); a+=9) {
				shares[i][a/9 - 1] = ByteBuffer.wrap(bitStream[i].get(a-9, a).toByteArray()).getInt();
			}
		}
		for(int a = 9; a < bitStream[inputFiles.length + 1].size(); a+=9) {
			shares[inputFiles.length + 1][a/9 - 1] = ByteBuffer.wrap(bitStream[inputFiles.length + 1].get(a-9, a).toByteArray()).getInt();
		}
		secret = sharesToSecret(shares);
	}
	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileBytes(File file) throws IOException
	{
		Path p = FileSystems.getDefault().getPath("", file.getPath());
		System.out.println(file.getPath());
		byte[] ans = Files.readAllBytes(p);
		System.out.println(ans.length);
        return ans;
	}
	/**
	 *  All elements in the 2D shares array are integers.
	 * The primitive data type int is 4 bytes starting with
	 * the most significant bits.
	 * The goal is to take each element, logical AND it with
	 * the number 511 to get it to 9-bits in length, then write
	 * that 9-bit number to file. The goal is to do this for
	 * every element in the shares sub array.
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	/**
	 * This method will write the specified share to the input
	 * file. Each share element is written as a 9-bit number.
	 * @param file
	 * @param shareNumber
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] Compress(byte[] input) {
		byte[] output2 = new byte[input.length];
		Deflater compresser = new Deflater();
		//User HUFFMAN_ONLY
		compresser.setStrategy(2);
		compresser.setInput(input);
		compresser.finish();
		int compressedDataLength = compresser.deflate(output2);
		if(compressedDataLength > 0)
		{
			byte[] output = new byte[compressedDataLength];
			for(int i = 0; i < compressedDataLength; i++)
			{
				output[i] = output2[i];
			}
			compresser.end();
			System.out.println("Compression Applicable");
			return output;
		}
		compresser.end();
	    return input;
	}
	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static byte[] serialize(File fileName) throws IOException {
		Path path = Paths.get(fileName.getAbsolutePath());
		byte[] data = Files.readAllBytes(path);
		return data;
    }
	/**
	 * 
	 * @param secret
	 * @param primeNumber
	 * @param numberOfShares
	 * @return
	 */
	public static int[] shareGenerator(int secret, int numberOfShares) {
		//System.out.println("\nByte: "+secret);
		int[] shares = new int[numberOfShares];
		int share = secret;
		int[] coefficients = new int[numberOfShares-1];
		//create random coefficients
		for(int i=0; i<numberOfShares-1; i++)
		{
			coefficients[i] = 5 + (int)(Math.random() * (primeNumber - 5));
			if(coefficients[i] < 5)
			{
				numNegative++;
			}
		}
		for(int x = 1; x <= numberOfShares; x++) 
		{
			//(1 <= x <= numberOfShares): share[x] = f(x) = secret + a[0]*x + a[1]*x^2 +...
			//String equation = secret+" + ";
			int exp = 0;
			for(int i = 0; i < numberOfShares - 1; i++)
			{
				exp = (int) Math.pow(x, (i+1));
				share += coefficients[i]*(exp);

			}
			//f(x)mod(p)
			shares[x-1] = share % primeNumber;
			//System.out.println(shares[x-1]+" = ["+equation+"]mod("+primeNumber+")");
			share = secret;
		}
		return shares;
	}
	/**
	 * 
	 * @param secret
	 * @param Number
	 * @throws IOException
	 */
	public static void printShares(byte[] secret, int Number) throws IOException {
		int shares[][] = shareGenerator(secret,Number);
		System.out.println("length: "+shares.length+"\twidth: "+shares[0].length);
		for(int a = 0; a < Number; a++) {
			System.out.print("\t|Share "+a);
		}
		System.out.println("\t|");
		for(int i = 0; i < shares.length; i++) {
			System.out.print("Byte "+i+"\t|\t");
			for(int a = 0; a < Number; a++) {
				System.out.print(shares[i][a]+"\t|\t");
			}
			System.out.println("");
		}
		byte[] byteArray = sharesToSecret(shares);
		String reconstitutedString = new String(byteArray);
		System.out.println("secret: "+reconstitutedString);
	}
	/**
	 * This method will take in the byte stream that is the secret, whether it be
	 * a text message or a file. Either data type must be in the form of a byte array.
	 * @param secret: byte stream that is the file or message
	 * @param numberOfShares: the number of shares that should be made from the secret
	 * @return shares: a 2 dimensional 
	 * @throws IOException
	 */
	public static int[][] shareGenerator(byte[] secret, int numberOfShares) throws IOException 
	{
		//There's an issue when serializing the secret into a byte 
		int[][] shares = new int[secret.length][numberOfShares];
		//for each byte in the byte array
		for(int a = 0; a < secret.length; a++) 
		{
			/*place the output of shareGenerator into each element of the  
			 * shares array. Each element will be 9-bits in size.
			 */
			shares[a] = shareGenerator(secret[a],numberOfShares);
		}
		return shares;
	}
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static int[] gcdD(int a, int b) { 
	    if (b == 0) {
	    	int[] ans = {a,1,0};
	    	return ans; 
	    }
	    else { 
	        int n = (int) Math.floor(a/b);
	        int c = a % b;
	        int[] r = gcdD(b,c); 
	        int[] ans = {r[0], r[2], r[1]-r[2]*n};
	        return ans;
	    }
	}
	/**
	 * 
	 * @param k
	 * @return
	 */
	private static int modInverse(int k) { 
	    k = k % primeNumber;
	    int r = (k < 0) ? -gcdD(primeNumber,-k)[2] : gcdD(primeNumber,k)[2];
	    return (primeNumber + r) % primeNumber;
	}
	/**
	 * 
	 * @param shares
	 * @return
	 */
	public static byte[] sharesToSecret(int[][] shares) {
		//call getRowSecret() for each byte in the input stream
		byte[] byteStream = new byte[shares.length];
		for(int i = 0; i < shares.length; i++) {
			byteStream[i] = (byte) byteReconstruction(shares[i]);
		}
		return byteStream;
	}
	/**
	 * Lagrange basis polynomial calculation
	 * @param args
	 * @throws IOException
	 */
	public static int byteReconstruction(int[] share)
	{
		int accum, count, formula, value, numerator, denominator;
		//share = new int[]{1494,329,965};
		
		for(formula = accum = 0; formula < share.length; formula++)
		{
			for(count = 0, numerator = denominator = 1; count < share.length; count++)
			{
				if(formula != count) 
				{
					numerator = (numerator * -(count + 1)) % primeNumber;
					denominator = (denominator * (formula - count)) % primeNumber;
				}
			}
			value = share[formula];
	        accum = (primeNumber + accum + (value * numerator * modInverse(denominator))) % primeNumber;
		}
		return accum;
	}
	public static void main(String[] args) throws IOException {
		numberOfShares = 3;
		File file = new File("C:\\Users\\Kaliel\\Desktop\\abstract.txt");
		//figure out what to do with message input
		fileShares = shareGenerator(readFileBytes(file),numberOfShares);
		printShares(readFileBytes(file),3);
		System.out.println(fileShares.length);
		System.out.println("Number of Negatives: "+numNegative);
	}
}