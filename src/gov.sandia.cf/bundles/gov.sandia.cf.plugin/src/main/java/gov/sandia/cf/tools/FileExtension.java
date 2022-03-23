package gov.sandia.cf.tools;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The file extension enum
 * 
 * @author Didier Verstraete
 *
 */
public enum FileExtension {

	// Images
	/** Bitmap extension */
	BITMAP("bmp", FileType.IMAGE), //$NON-NLS-1$
	/** GIF extension */
	GIF("gif", FileType.IMAGE), //$NON-NLS-1$
	/** JPG extension */
	JPG("jpg", FileType.IMAGE), //$NON-NLS-1$
	/** JPEG extension */
	JPEG("jpeg", FileType.IMAGE), //$NON-NLS-1$
	/** PNG extension */
	PNG("png", FileType.IMAGE), //$NON-NLS-1$
	/** SVG extension */
	SVG("svg", FileType.IMAGE), //$NON-NLS-1$
	// Documents
	/** WORD 1995-2003 extension */
	WORD_1995("doc", FileType.DOCUMENT), //$NON-NLS-1$
	/** WORD 2007+ extension */
	WORD_2007("docx", FileType.DOCUMENT), //$NON-NLS-1$
	/** LATEX extension */
	LATEX("tex", FileType.DOCUMENT), //$NON-NLS-1$
	/** PDF extension */
	PDF("pdf", FileType.DOCUMENT), //$NON-NLS-1$
	/** Text extension */
	TEXTE("txt", FileType.DOCUMENT), //$NON-NLS-1$
	/** YML extension */
	YML("yml", FileType.DOCUMENT), //$NON-NLS-1$
	/** YAML extension */
	YAML("yaml", FileType.DOCUMENT); //$NON-NLS-1$

	private String extension;
	private FileType type;

	private FileExtension(String extension, FileType type) {
		this.extension = extension;
		this.type = type;
	}

	/**
	 * @return the file extension
	 */
	public String getExtension() {
		return "." + extension; //$NON-NLS-1$
	}

	/**
	 * @return the file extension without dot
	 */
	public String getExtensionWithoutDot() {
		return extension;
	}

	/**
	 * @return the file type
	 */
	public FileType getType() {
		return type;
	}

	/**
	 * @param type the type to search
	 * @return the list of FileExtension with type file type
	 */
	public static List<FileExtension> getByType(FileType type) {
		return Arrays.asList(FileExtension.values()).stream().filter(ext -> ext.getType().equals(type))
				.collect(Collectors.toList());
	}

	/**
	 * @param name the extension name to search
	 * @return the extension with name
	 */
	public static FileExtension getByName(String name) {
		Optional<FileExtension> found = Arrays.asList(FileExtension.values()).stream()
				.filter(ext -> ext.getExtension().equals(name)).findFirst();
		if (found.isPresent()) {
			return found.get();
		}
		return null;
	}

	/**
	 * @return the yml file extensions
	 */
	public static List<FileExtension> getYmlExtensions() {
		return Arrays.asList(YML, YAML);
	}
}
