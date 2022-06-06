package com.evo.vcs;

import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

public class App {
	public static void main(String[] args) {
		int exitCode = new CommandLine(new EvoCommand())
				.setExecutionExceptionHandler(new IExecutionExceptionHandler() {
					public int handleExecutionException(Exception ex,
					                                    CommandLine commandLine,
					                                    ParseResult parseResult) throws Exception {
						throw ex;
						/*commandLine.getErr().println(ex.getMessage());
						commandLine.usage(commandLine.getErr());
						return commandLine.getCommandSpec().exitCodeOnExecutionException();*/
					}
				})
				.execute(args);
		System.exit(exitCode);
	}
}