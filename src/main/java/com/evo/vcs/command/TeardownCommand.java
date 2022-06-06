package com.evo.vcs.command;

import com.evo.exception.SimpleException;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "teardown", description = "Drop all schemas of the Evo Project.")
public class TeardownCommand extends AbstractWithDbConnCommand implements Runnable {
	
	@Override
	public void run() {
		super.loadProjectWithTempConfig();
		try {
			ProjectHelper.teardownProject(this.getProject());
		} catch (SimpleException e) {
			System.err.println(CommandLine.Help.Ansi.AUTO.string(e.getMessage()));
			System.err.println();
			e.printStackTrace();
		}
	}
}
