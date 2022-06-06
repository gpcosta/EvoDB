package com.evo.internal.rdbms.dump;

import com.evo.internal.rdbms.exception.SqlDumperException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

abstract public class AbstractDump {
	
	protected String dump(String command) throws SqlDumperException {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			if (this.isWindows()) {
				builder.command("cmd.exe", "/c", command);
			} else {
				builder.command("sh", "-c", command);
			}
			builder.directory(new File(System.getProperty("user.home")));
			Process process = builder.start();
			
			/*Scanner s = new Scanner(process.getInputStream()).useDelimiter("\\A");
			String content = s.hasNext() ? s.next() : "";
			s.close();
			return content;*/
			
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			for (int length; (length = process.getInputStream().read(buffer)) != -1; )
				result.write(buffer, 0, length);
			
			if (process.waitFor() != 0)
				throw new SqlDumperException();
			
			return result.toString(StandardCharsets.UTF_8.name());
		} catch (IOException | InterruptedException e) {
			throw new SqlDumperException(e);
		}
	}
	
	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}
}
