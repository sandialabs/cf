The credibility framework allow the use of variables. It must encapsulated with `${`MY_VAR`}` where MY_VAR is the variable to get.

The following predefined system variables:

* `user.name` – the username of the account that is running the workflow.
* `user.home` – the home directory of the user that is running the workflow.
* `java.version` – the value of the Java system property "java.version".
* `os.name` – the value of the Java system property "os.name". Returns a human-readable label for the running operating system – i.e., "Mac OS X"
* `hostname` – a hostname for the system on which the workflow is running.

The following predefined file/resource variables:

* `cf.homedir` – the absolute path to the current CF's home directory (typically the location of the CF file.) Relative paths in home_file properties are relative to this directory.
* `cf.workdir` – the absolute path to the current CF's base working directory.
* `cf.filename` – the name (only) of the currently executing CF file.
* `cf.filedir` – the absolute path to the directory in which the file named in cf.filename exists.
* `eclipse.workspace` – the absolute path to the Eclipse workspace on the local filesystem.
* `eclipse.project` – the name of the Eclipse project in which the CF file exists.