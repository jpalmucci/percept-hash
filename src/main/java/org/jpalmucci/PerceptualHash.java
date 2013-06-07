package org.jpalmucci;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

public class PerceptualHash implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7182312535280122247L;
	/** list of the 64 most common characters */
	int[] tokens = null;
	/** their average rate */
	double[] tokenMeans = null;
	
	/** train on a body of text */	
    public void train(Reader r) throws IOException {
    	tokens = new int[64];
    	tokenMeans = new double[64];

		long total = 0;
		// count of all types of characters
		long[] counts = new long[256];
		int c;
		while ((c = r.read()) >= 0) {
			if (c < 256) {
				counts[c]++;
				total++;
			}
		}
		long[] tokenCounts = new long[64];
		for(int i=0; i < counts.length; i++) {
			int curToken = i;
			long curCount = counts[i];
			for(int j=0; j < tokens.length; j++) {
				if (curCount > tokenCounts[j]) {
					long nextCount = tokenCounts[j];
					int nextToken = tokens[j];
					tokenCounts[j] = curCount;
					tokens[j] = curToken;
					curCount = nextCount;
					curToken = nextToken;
				}
			}
		}
		total = 0;
		for(int j=0; j < tokens.length; j++) {
			total += tokenCounts[j];
		}
		for(int j=0; j < tokens.length; j++) {
			tokenMeans[j] = tokenCounts[j]/(double)total;
		}
		
	}
    
    /** hash with the given number of bits. Bits cannot exceed 64 */
    public long hash(int bits, Reader r) throws IOException {
    	assert(tokens != null);
    	assert(bits <= 64);
    	long[] tokenCounts = new long[64];
		long total = 0;
		int c;
		while ((c = r.read()) >= 0) {
			for(int j=0; j < tokens.length; j++) {	
				if (c == tokens[j]) {
					tokenCounts[j]++;
					total++;
					break;
				}
			}
		}
		long ret = 0;
		for(int j=0; j < tokens.length; j++) {
                    ret <<= 1;
				if ((double)tokenCounts[j] / total > tokenMeans[j]) {
					ret |= 1;
				}
		}
		return ret;
	}
    
    /** hash with the given number of bits. Bits cannot exceed 64 */
    public long hash(int bits, String s) {
        try {
            StringReader r = new StringReader(s);
            return hash(bits, r);
        } catch (IOException e) {
                // no f-ing way this can happen
                System.exit(1);
            }
        	return 0;
    }
	

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		PerceptualHash p = new PerceptualHash();
		p.train(new FileReader("/Users/jpalmucci/Desktop/job_samples"));
		long h = p.hash(32, new StringReader("foo bar baz"));

	}

}
