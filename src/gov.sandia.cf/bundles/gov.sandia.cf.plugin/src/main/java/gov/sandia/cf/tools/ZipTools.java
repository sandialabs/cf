/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;

/**
 * This class is a zip toolbox
 * 
 * @author Didier Verstraete
 *
 */
public class ZipTools {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ZipTools.class);

	/**
	 * The zip file extension
	 */
	public static final String ZIP_EXTENSION = "zip"; //$NON-NLS-1$

	/**
	 * The directory suffix
	 */
	public static final String DIRECTORY_SUFFIX = "/"; //$NON-NLS-1$

	/**
	 * Private constructor to not allow instantiation.
	 */
	private ZipTools() {
	}

	/**
	 * Zip a file or a directory @param pathToZip to a zip file
	 * 
	 * @param listSourceIPath the files to zip
	 * @param targetFilePath  the path of the final zip file
	 * @throws IOException          if an error occured while zipping the specified
	 *                              file to zip
	 * @throws CredibilityException if the @param pathToZip is null or does not
	 *                              reference an existing file
	 * @throws CoreException        if the generated zip can not be found in the
	 *                              workspace
	 */
	public static void zipIPath(List<IPath> listSourceIPath, IPath targetFilePath)
			throws IOException, CredibilityException, CoreException {
		if (listSourceIPath == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL));
		}

		// convert iFile to File
		List<File> listFileToZip = new ArrayList<>();
		for (IPath ipath : listSourceIPath) {
			listFileToZip.add(new File(WorkspaceTools.toOsPath(ipath)));
		}

		// zip File
		zipFile(listFileToZip, targetFilePath);
	}

	/**
	 * Zip a file or a directory @param iFileToZip to a zip file
	 * 
	 * @param listSourceIFile the ifile to zip
	 * @param targetFilePath  the path of the final zip file
	 * @throws IOException          if an error occured while zipping the specified
	 *                              file to zip
	 * @throws CredibilityException if the @param pathToZip is null or does not
	 *                              reference an existing file
	 * @throws CoreException        if the generated zip can not be found in the
	 *                              workspace
	 */
	public static void zipIFile(List<IFile> listSourceIFile, IPath targetFilePath)
			throws IOException, CredibilityException, CoreException {
		if (listSourceIFile == null || targetFilePath == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL));
		}

		// convert iFile to File
		List<File> listFileToZip = new ArrayList<>();
		for (IFile ifile : listSourceIFile) {
			listFileToZip.add(WorkspaceTools.toFile(ifile));
		}

		// zip file
		zipFile(listFileToZip, targetFilePath);
	}

	/**
	 * Zip a file or a directory @param fileToZip to a zip file
	 * 
	 * @param listSourceFile the ifile to zip
	 * @param targetFilePath the path of the final zip file
	 * @throws IOException          if an error occured while zipping the specified
	 *                              file to zip
	 * @throws CredibilityException if the @param pathToZip is null or does not
	 *                              reference an existing file
	 * @throws CoreException        if the generated zip can not be found in the
	 *                              workspace
	 */
	public static void zipFile(List<File> listSourceFile, IPath targetFilePath)
			throws IOException, CredibilityException, CoreException {
		if (listSourceFile == null || targetFilePath == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL));
		}

		// create zip file
		zipFile(listSourceFile, new File(WorkspaceTools.toOsPath(targetFilePath)));

		// refresh zip file in workspace
		WorkspaceTools.refreshPath(targetFilePath);
	}

	/**
	 * Zip a file or a directory @param fileToZip to a zip file
	 * 
	 * @param listSourceFile the ifile to zip
	 * @param targetFile     the final zip file
	 * @throws IOException          if an error occured while zipping the specified
	 *                              file to zip
	 * @throws CredibilityException if the @param pathToZip is null or does not
	 *                              reference an existing file
	 * @throws IOException          if the generated zip can not be found in the
	 *                              workspace
	 */
	public static void zipFile(List<File> listSourceFile, File targetFile) throws IOException, CredibilityException {
		if (listSourceFile == null || targetFile == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL));
		}

		// create zip file
		File finalZipFile = targetFile;
		if (!finalZipFile.exists()) {
			Files.createFile(finalZipFile.toPath());
		}

		// create stream
		FileOutputStream fos = new FileOutputStream(finalZipFile);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		// zip
		zipFile(listSourceFile, RscTools.empty(), zipOut);

		// close stream
		zipOut.close();
		fos.close();
	}

	/**
	 * Zip a file or a directory @param fileToZip to a zip file.
	 *
	 * @param listFileToZip the list file to zip
	 * @param filePrefix the file prefix
	 * @param zipOut             the zip output stream to pass through the recursive
	 *                           calls (for directory)
	 * @throws IOException if an error occured while zipping the specified file to
	 *                     zip
	 */
	private static void zipFile(List<File> listFileToZip, String filePrefix, ZipOutputStream zipOut)
			throws IOException {

		logger.debug("Zipping {}", listFileToZip); //$NON-NLS-1$

		for (File fileToZip : listFileToZip) {
			// if it is a directory, iterate the directory to add all its content to the
			// zipEntry map
			if (fileToZip.isDirectory()) {
				String suffix = DIRECTORY_SUFFIX;
				zipOut.putNextEntry(new ZipEntry(filePrefix + fileToZip.getName() + suffix));
				zipOut.closeEntry();
				File[] children = fileToZip.listFiles();
				if (children != null && children.length > 0) {
					zipFile(Arrays.asList(children), filePrefix + fileToZip.getName() + suffix, zipOut);
				}
			} else {

				// otherwise add the file to the zip input stream
				try (FileInputStream fis = new FileInputStream(fileToZip)) {
					ZipEntry zipEntry = new ZipEntry(filePrefix + fileToZip.getName());
					zipOut.putNextEntry(zipEntry);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0) {
						zipOut.write(bytes, 0, length);
					}
				}
			}
		}
	}

	/**
	 * Unzip @param fileZip to the specified @param destinationDir and
	 * refresh @param destinationDir in the workspace
	 * 
	 * @param fileZip        the zipped file
	 * @param destinationDir the destination directory
	 * @throws IOException          if the specified file are null, or does not
	 *                              exist
	 * @throws CoreException        if the file is not present in the workspace
	 * @throws CredibilityException if a parameter is not valid.
	 */
	public static void unzip(IFile fileZip, IFolder destinationDir)
			throws IOException, CoreException, CredibilityException {

		// unzip fileZip
		unzip(WorkspaceTools.toFile(fileZip), WorkspaceTools.toFile(destinationDir));

		// refresh unzipped resource in workspace
		WorkspaceTools.refreshPath(destinationDir.getFullPath());
	}

	/**
	 * Unzip @param fileZip to the specified @param destinationDir
	 * 
	 * @param fileZip        the zipped file
	 * @param destinationDir the destination directory
	 * @throws IOException          if the specified file are null, or does not
	 *                              exist
	 * @throws CredibilityException if a parameter is not valid.
	 */
	public static void unzip(File fileZip, File destinationDir) throws IOException, CredibilityException {

		// create a new stream for the zip entries
		byte[] buffer = new byte[1024];

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip))) {
			ZipEntry zipEntry = zis.getNextEntry();

			// iterate on zip entries
			while (zipEntry != null) {

				// create a new file or directory for the zip entry
				File newFile = newFile(destinationDir, zipEntry);

				// if the zip entry is a file, fill the new file
				if (zipEntry.getName() != null && !zipEntry.getName().endsWith(DIRECTORY_SUFFIX)) {
					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
				}
				zipEntry = zis.getNextEntry();
			}

			// close streams
			zis.closeEntry();
		}
	}

	/**
	 * 
	 * @param destinationDir the destination directory
	 * @param zipEntry       the zip entry
	 * @return a new file or directory with @param zipEntry
	 * @throws IOException          if destinationDir is not found or is not a
	 *                              directory
	 * @throws CredibilityException if a parameter is not valid.
	 */
	private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException, CredibilityException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		// test if the zip entry is inside the destinationDir
		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException(RscTools.getString(RscConst.EX_ZIPTOOLS_FILEOUTSIDE) + zipEntry.getName());
		}

		// create file or directory for the destfile
		boolean newResourceCreated = false;
		if (zipEntry.getName() != null && zipEntry.getName().endsWith(DIRECTORY_SUFFIX)) {
			newResourceCreated = destFile.mkdir();
		} else {
			newResourceCreated = true;
			if (destFile.getParent() != null && !new File(destFile.getParent()).exists()) {
				newResourceCreated &= new File(destFile.getParent()).mkdirs();
			}
			newResourceCreated &= destFile.createNewFile();
		}

		if (!newResourceCreated) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_ZIPTOOLS_CREATERSC_UNSUCCESSFUL, destFile));
		}

		return destFile;
	}
}
