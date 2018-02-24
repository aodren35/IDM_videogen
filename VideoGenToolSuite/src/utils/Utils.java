package utils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;


/*
 * Classe final utilitaire
 */
public final class Utils {

	public static int countLine(String loc) throws IOException {
		long numberOfLines;
	    try (Stream<String> s = Files.lines(Paths.get(loc),
	            Charset.defaultCharset())) {

	        numberOfLines = s.count();

	    } catch (IOException e) {
	        throw e;
	    }
	    return (int) numberOfLines;
	}
	
	public static void writeInFile(String filename, String data) throws UnsupportedEncodingException, IOException {
		BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
		try {
			buffer.write(data);
		}
		catch(IOException e) {
			throw e;
		}
		finally {
			buffer.flush();
			buffer.close();
		}
	}
}
