package utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

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
}
