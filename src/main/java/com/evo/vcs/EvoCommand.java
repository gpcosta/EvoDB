package com.evo.vcs;

import com.evo.vcs.command.*;
import picocli.CommandLine.Command;

@Command(name = "evo", version = "Evo DB 1.0", mixinStandardHelpOptions = true, subcommands = {
		InitCommand.class,
		StatusCommand.class,
		BundleCommand.class,
		DeployCommand.class,
		TeardownCommand.class,
		RedeployCommand.class
})

public class EvoCommand {
}
