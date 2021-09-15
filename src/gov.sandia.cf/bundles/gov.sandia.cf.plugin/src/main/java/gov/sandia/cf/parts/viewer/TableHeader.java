/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

/**
 * The table viewer class for qoi header description
 * 
 * @author Didier Verstraete
 *
 */
public class TableHeader extends TableViewerHideSelection {

	/**
	 * the column id property name
	 */
	public static final String COLUMN_KEY_PROPERTY = "KEY"; //$NON-NLS-1$
	/**
	 * the column phenomena property name
	 */
	public static final String COLUMN_VALUE_PROPERTY = "VALUE"; //$NON-NLS-1$

	/**
	 * the table containing all the column properties ordered
	 */
	public static final List<String> COLUMNS_PROPERTIES = Collections
			.unmodifiableList(Arrays.asList(COLUMN_KEY_PROPERTY, COLUMN_VALUE_PROPERTY));

	/**
	 * Constructor
	 * 
	 * @param parent the parent composite
	 * @param style  the SWT style
	 */
	public TableHeader(Composite parent, int style) {
		super(parent, style);
	}

}
