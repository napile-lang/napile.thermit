/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.napile.thermit.taskdefs.optional.javacc;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.napile.thermit.BuildException;
import org.napile.thermit.Project;
import org.napile.thermit.Task;
import org.napile.thermit.taskdefs.Execute;
import org.napile.thermit.taskdefs.LogStreamHandler;
import org.napile.thermit.types.Commandline;
import org.napile.thermit.types.CommandlineJava;
import org.napile.thermit.types.Path;
import org.napile.thermit.util.JavaEnvUtils;

/**
 * Runs the JJTree compiler compiler.
 */
public class JJTree extends Task
{

	// keys to optional attributes
	private static final String OUTPUT_FILE = "OUTPUT_FILE";
	private static final String BUILD_NODE_FILES = "BUILD_NODE_FILES";
	private static final String MULTI = "MULTI";
	private static final String NODE_DEFAULT_VOID = "NODE_DEFAULT_VOID";
	private static final String NODE_FACTORY = "NODE_FACTORY";
	private static final String NODE_SCOPE_HOOK = "NODE_SCOPE_HOOK";
	private static final String NODE_USES_PARSER = "NODE_USES_PARSER";
	private static final String STATIC = "STATIC";
	private static final String VISITOR = "VISITOR";

	private static final String NODE_PACKAGE = "NODE_PACKAGE";
	private static final String VISITOR_EXCEPTION = "VISITOR_EXCEPTION";
	private static final String NODE_PREFIX = "NODE_PREFIX";

	private final Hashtable optionalAttrs = new Hashtable();

	private String outputFile = null;

	private static final String DEFAULT_SUFFIX = ".jj";

	// required attributes
	private File outputDirectory = null;
	private File targetFile = null;
	private File javaccHome = null;

	private CommandlineJava cmdl = new CommandlineJava();

	private String maxMemory = null;

	/**
	 * Sets the BUILD_NODE_FILES grammar option.
	 *
	 * @param buildNodeFiles a <code>boolean</code> value.
	 */
	public void setBuildnodefiles(boolean buildNodeFiles)
	{
		optionalAttrs.put(BUILD_NODE_FILES, buildNodeFiles ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Sets the MULTI grammar option.
	 *
	 * @param multi a <code>boolean</code> value.
	 */
	public void setMulti(boolean multi)
	{
		optionalAttrs.put(MULTI, multi ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Sets the NODE_DEFAULT_VOID grammar option.
	 *
	 * @param nodeDefaultVoid a <code>boolean</code> value.
	 */
	public void setNodedefaultvoid(boolean nodeDefaultVoid)
	{
		optionalAttrs.put(NODE_DEFAULT_VOID, nodeDefaultVoid ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Sets the NODE_FACTORY grammar option.
	 *
	 * @param nodeFactory a <code>boolean</code> value.
	 */
	public void setNodefactory(boolean nodeFactory)
	{
		optionalAttrs.put(NODE_FACTORY, nodeFactory ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Sets the NODE_SCOPE_HOOK grammar option.
	 *
	 * @param nodeScopeHook a <code>boolean</code> value.
	 */
	public void setNodescopehook(boolean nodeScopeHook)
	{
		optionalAttrs.put(NODE_SCOPE_HOOK, nodeScopeHook ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Sets the NODE_USES_PARSER grammar option.
	 *
	 * @param nodeUsesParser a <code>boolean</code> value.
	 */
	public void setNodeusesparser(boolean nodeUsesParser)
	{
		optionalAttrs.put(NODE_USES_PARSER, nodeUsesParser ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Sets the STATIC grammar option.
	 *
	 * @param staticParser a <code>boolean</code> value.
	 */
	public void setStatic(boolean staticParser)
	{
		optionalAttrs.put(STATIC, staticParser ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Sets the VISITOR grammar option.
	 *
	 * @param visitor a <code>boolean</code> value.
	 */
	public void setVisitor(boolean visitor)
	{
		optionalAttrs.put(VISITOR, visitor ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Sets the NODE_PACKAGE grammar option.
	 *
	 * @param nodePackage the option to use.
	 */
	public void setNodepackage(String nodePackage)
	{
		optionalAttrs.put(NODE_PACKAGE, nodePackage);
	}

	/**
	 * Sets the VISITOR_EXCEPTION grammar option.
	 *
	 * @param visitorException the option to use.
	 */
	public void setVisitorException(String visitorException)
	{
		optionalAttrs.put(VISITOR_EXCEPTION, visitorException);
	}

	/**
	 * Sets the NODE_PREFIX grammar option.
	 *
	 * @param nodePrefix the option to use.
	 */
	public void setNodeprefix(String nodePrefix)
	{
		optionalAttrs.put(NODE_PREFIX, nodePrefix);
	}

	/**
	 * The directory to write the generated JavaCC grammar and node files to.
	 * If not set, the files are written to the directory
	 * containing the grammar file.
	 *
	 * @param outputDirectory the output directory.
	 */
	public void setOutputdirectory(File outputDirectory)
	{
		this.outputDirectory = outputDirectory;
	}

	/**
	 * The outputfile to write the generated JavaCC grammar file to.
	 * If not set, the file is written with the same name as
	 * the JJTree grammar file with a suffix .jj.
	 *
	 * @param outputFile the output file name.
	 */
	public void setOutputfile(String outputFile)
	{
		this.outputFile = outputFile;
	}

	/**
	 * The jjtree grammar file to process.
	 *
	 * @param targetFile the grammar file.
	 */
	public void setTarget(File targetFile)
	{
		this.targetFile = targetFile;
	}

	/**
	 * The directory containing the JavaCC distribution.
	 *
	 * @param javaccHome the directory containing JavaCC.
	 */
	public void setJavacchome(File javaccHome)
	{
		this.javaccHome = javaccHome;
	}

	/**
	 * Corresponds -Xmx.
	 *
	 * @param max max memory parameter.
	 * @since Ant 1.8.3
	 */
	public void setMaxmemory(String max)
	{
		maxMemory = max;
	}

	/**
	 * Constructor
	 */
	public JJTree()
	{
		cmdl.setVm(JavaEnvUtils.getJreExecutable("java"));
	}

	/**
	 * Run the task.
	 *
	 * @throws BuildException on error.
	 */
	public void execute() throws BuildException
	{

		// load command line with optional attributes
		Enumeration iter = optionalAttrs.keys();
		while(iter.hasMoreElements())
		{
			String name = (String) iter.nextElement();
			Object value = optionalAttrs.get(name);
			cmdl.createArgument().setValue("-" + name + ":" + value.toString());
		}

		if(targetFile == null || !targetFile.isFile())
		{
			throw new BuildException("Invalid target: " + targetFile);
		}

		File javaFile = null;

		// use the directory containing the target as the output directory
		if(outputDirectory == null)
		{
			// convert backslashes to slashes, otherwise jjtree will
			// put this as comments and this seems to confuse javacc
			cmdl.createArgument().setValue("-OUTPUT_DIRECTORY:" + getDefaultOutputDirectory());

			javaFile = new File(createOutputFileName(targetFile, outputFile, null));
		}
		else
		{
			if(!outputDirectory.isDirectory())
			{
				throw new BuildException("'outputdirectory' " + outputDirectory + " is not a directory.");
			}

			// convert backslashes to slashes, otherwise jjtree will
			// put this as comments and this seems to confuse javacc
			cmdl.createArgument().setValue("-OUTPUT_DIRECTORY:" + outputDirectory.getAbsolutePath().replace('\\', '/'));

			javaFile = new File(createOutputFileName(targetFile, outputFile, outputDirectory.getPath()));
		}

		if(javaFile.exists() && targetFile.lastModified() < javaFile.lastModified())
		{
			log("Target is already built - skipping (" + targetFile + ")", Project.MSG_VERBOSE);
			return;
		}

		if(outputFile != null)
		{
			cmdl.createArgument().setValue("-" + OUTPUT_FILE + ":" + outputFile.replace('\\', '/'));
		}

		cmdl.createArgument().setValue(targetFile.getAbsolutePath());

		final Path classpath = cmdl.createClasspath(getProject());
		final File javaccJar = JavaCC.getArchiveFile(javaccHome);
		classpath.createPathElement().setPath(javaccJar.getAbsolutePath());
		classpath.addJavaRuntime();

		cmdl.setClassname(JavaCC.getMainClass(classpath, JavaCC.TASKDEF_TYPE_JJTREE));

		cmdl.setMaxmemory(maxMemory);
		final Commandline.Argument arg = cmdl.createVmArgument();
		arg.setValue("-Dinstall.root=" + javaccHome.getAbsolutePath());

		final Execute process = new Execute(new LogStreamHandler(this, Project.MSG_INFO, Project.MSG_INFO), null);
		log(cmdl.describeCommand(), Project.MSG_VERBOSE);
		process.setCommandline(cmdl.getCommandline());

		try
		{
			if(process.execute() != 0)
			{
				throw new BuildException("JJTree failed.");
			}
		}
		catch(IOException e)
		{
			throw new BuildException("Failed to launch JJTree", e);
		}
	}

	private String createOutputFileName(File destFile, String optionalOutputFile, String outputDir)
	{
		optionalOutputFile = validateOutputFile(optionalOutputFile, outputDir);
		String jjtreeFile = destFile.getAbsolutePath().replace('\\', '/');

		if((optionalOutputFile == null) || optionalOutputFile.equals(""))
		{
			int filePos = jjtreeFile.lastIndexOf("/");

			if(filePos >= 0)
			{
				jjtreeFile = jjtreeFile.substring(filePos + 1);
			}

			int suffixPos = jjtreeFile.lastIndexOf('.');

			if(suffixPos == -1)
			{
				optionalOutputFile = jjtreeFile + DEFAULT_SUFFIX;
			}
			else
			{
				String currentSuffix = jjtreeFile.substring(suffixPos);

				if(currentSuffix.equals(DEFAULT_SUFFIX))
				{
					optionalOutputFile = jjtreeFile + DEFAULT_SUFFIX;
				}
				else
				{
					optionalOutputFile = jjtreeFile.substring(0, suffixPos) + DEFAULT_SUFFIX;
				}
			}
		}

		if((outputDir == null) || outputDir.equals(""))
		{
			outputDir = getDefaultOutputDirectory();
		}

		return (outputDir + "/" + optionalOutputFile).replace('\\', '/');
	}

	/**
	 * When running JJTree from an Ant taskdesk the -OUTPUT_DIRECTORY must
	 * always be set. But when -OUTPUT_DIRECTORY is set, -OUTPUT_FILE is
	 * handled as if relative of this -OUTPUT_DIRECTORY. Thus when the
	 * -OUTPUT_FILE is absolute or contains a drive letter we have a problem.
	 *
	 * @param destFile
	 * @param outputDir
	 * @return
	 * @throws BuildException
	 */
	private String validateOutputFile(String destFile, String outputDir) throws BuildException
	{
		if(destFile == null)
		{
			return null;
		}

		if((outputDir == null) && (destFile.startsWith("/") || destFile.startsWith("\\")))
		{
			String relativeOutputFile = makeOutputFileRelative(destFile);
			setOutputfile(relativeOutputFile);

			return relativeOutputFile;
		}

		String root = getRoot(new File(destFile)).getAbsolutePath();

		if((root.length() > 1) && destFile.startsWith(root.substring(0, root.length() - 1)))
		{
			throw new BuildException("Drive letter in 'outputfile' not " + "supported: " + destFile);
		}

		return destFile;
	}

	private String makeOutputFileRelative(String destFile)
	{
		StringBuffer relativePath = new StringBuffer();
		String defaultOutputDirectory = getDefaultOutputDirectory();
		int nextPos = defaultOutputDirectory.indexOf('/');
		int startPos = nextPos + 1;

		while(startPos > -1 && startPos < defaultOutputDirectory.length())
		{
			relativePath.append("/..");
			nextPos = defaultOutputDirectory.indexOf('/', startPos);

			if(nextPos == -1)
			{
				startPos = nextPos;
			}
			else
			{
				startPos = nextPos + 1;
			}
		}

		relativePath.append(destFile);

		return relativePath.toString();
	}

	private String getDefaultOutputDirectory()
	{
		return getProject().getBaseDir().getAbsolutePath().replace('\\', '/');
	}

	/**
	 * Determine root directory for a given file.
	 *
	 * @param file
	 * @return file's root directory
	 */
	private File getRoot(File file)
	{
		File root = file.getAbsoluteFile();

		while(root.getParent() != null)
		{
			root = root.getParentFile();
		}

		return root;
	}
}
