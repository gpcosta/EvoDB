To generate the jar, run `mvn clean compile assembly:single`

To help you to run the jar, please see `evo.cmd`, in the root of the project.
In this file, change `%EVO_JAR%` variable for the location of the jar and
set `%JDBC_JAR_PATH%` variable for the path of the internal jdbc that you intend that Evo DB uses.

For better usage, put the location of the `evo.cmd` file in global path.
Then, use `evo` followed by the command and its respective parameters.

To know more about it, run `evo --help`.