package org.paxdb;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Util {

	public static final Util instance = new Util();
	
	private Util() {
		
	}

	public String join(String separator, Collection<? extends Object> sequence) {
		StringBuffer sb = new StringBuffer();
		for (Iterator<? extends Object> i = sequence.iterator(); i.hasNext(); ) {
			sb.append(i.next().toString());
			if (i.hasNext()) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}
	
	public List<String> readFile(String file) throws FileNotFoundException {
		FileInputStream fstream = new FileInputStream(file);
		try {
			return readFile(fstream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				fstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> readFile(InputStream istream) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(istream)));
		String strLine;
		while ((strLine = br.readLine()) != null) {
			lines.add(strLine.trim());
		}
		if (lines.isEmpty()) {
			throw new IOException("empty file: " + istream);
		}
		return lines;
	}
	
	public  void writeFile(String filename, String content) throws FileNotFoundException {
		OutputStream o = new FileOutputStream(filename);
		boolean failed = false;
		try {
			BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(new DataOutputStream(o)));
			writer.write(content);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			failed = true;
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (failed) {
			new File(filename).delete();
		}
	}
	
}
