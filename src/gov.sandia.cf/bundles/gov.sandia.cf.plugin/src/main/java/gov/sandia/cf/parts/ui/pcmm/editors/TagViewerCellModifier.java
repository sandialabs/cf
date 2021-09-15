/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.parts.ui.pcmm.PCMMManageTagDialog;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Defines the tag table cell modifier and all the constants of the table
 * 
 * @author Didier Verstraete
 *
 */
public class TagViewerCellModifier implements ICellModifier {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(TagViewerCellModifier.class);

	/**
	 * the column properties
	 */
	private List<String> columnProperties;

	/**
	 * the view manager
	 */
	private PCMMManageTagDialog parent;

	private static final int DESCRIPTION_INDEX = 3;

	private final int[] modifiableIndexes = { DESCRIPTION_INDEX };

	/**
	 * The constructor
	 * 
	 * @param parent           the parent tag dialog view
	 * @param columnProperties the column properties
	 */
	public TagViewerCellModifier(PCMMManageTagDialog parent, String[] columnProperties) {
		this.parent = parent;
		this.columnProperties = Arrays.asList(columnProperties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item) {
			Item item = (Item) element;

			// update evidence
			if (item.getData() instanceof Tag) {

				Tag tag = (Tag) item.getData();

				int index = columnProperties.indexOf(property);
				if (index == DESCRIPTION_INDEX) {

					tag.setDescription((String) value);

					try {
						// update tag in database
						parent.getViewManager().getAppManager().getService(IPCMMApplication.class).updateTag(tag);

						parent.reload();

					} catch (CredibilityException e) {
						logger.error(e.getMessage(), e);
						MessageDialog.openError(item.getDisplay().getActiveShell(),
								RscTools.getString(RscConst.MSG_TAG_PART_TITLE),
								RscTools.getString(RscConst.ERR_TAG_PART_UPDATING) + property
										+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue(Object element, String property) {
		int index = columnProperties.indexOf(property);
		if (element instanceof Tag && index == DESCRIPTION_INDEX) {
			return ((Tag) element).getDescription() != null ? ((Tag) element).getDescription() : RscTools.empty();
		}
		return RscTools.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canModify(Object element, String property) {
		int currentIndex = columnProperties.indexOf(property);
		return isEditableColumnIndex(currentIndex);
	}

	/**
	 * @param columnIndex the column index
	 * @return true if the parameter value is in the modifiable index list,
	 *         otherwise false
	 */
	public boolean isEditableColumnIndex(int columnIndex) {
		return IntStream.of(modifiableIndexes).anyMatch(x -> x == columnIndex);
	}
}
