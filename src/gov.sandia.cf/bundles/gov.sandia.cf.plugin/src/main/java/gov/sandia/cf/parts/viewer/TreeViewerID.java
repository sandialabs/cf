/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.tools.IDTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * 
 * The tree viewer class for phenomena description
 * 
 * @author Didier Verstraete
 *
 */
public class TreeViewerID extends TreeViewerHideSelection {

	private TreeViewerColumn idColumn;

	/**
	 * 
	 * The constructor
	 * 
	 * @param parent the parent composite
	 * @param style  the style of the component
	 */
	public TreeViewerID(Composite parent, int style) {
		this(parent, style, true, false);
	}

	/**
	 * 
	 * The constructor
	 * 
	 * @param parent         the parent composite
	 * @param style          the style of the component
	 * @param hideSelection  hide the selection on the table
	 * @param adaptTextWidth adapt the text width
	 */
	public TreeViewerID(Composite parent, int style, boolean hideSelection, boolean adaptTextWidth) {
		super(parent, style, hideSelection, adaptTextWidth);

		// render main table id column
		renderMainTableColumnId();
	}

	/**
	 * Add the id column
	 */
	private void renderMainTableColumnId() {

		// Tree - Columns - Element/Sub-element
		idColumn = new TreeViewerColumn(this, SWT.LEFT);
		idColumn.getColumn().setText(RscTools.getString(RscConst.MSG_TABLE_COLUMN_ID));
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return getIdColumnText(element);
			}
		});
	}

	/**
	 * Set tree layout
	 * 
	 * @param viewerLayout the new layout
	 */
	public void setLayout(TableLayout viewerLayout) {
		this.getTree().setLayout(viewerLayout);
		viewerLayout.addColumnData(new ColumnPixelData(PartsResourceConstants.TREE_IDCOLUMN_WIDTH, true));
	}

	/**
	 * @return the tree layout
	 */
	public Layout getLayout() {
		return this.getTree().getLayout();
	}

	/**
	 * @return the id column
	 */
	public TreeViewerColumn getIdColumn() {
		return idColumn;
	}

	/**
	 * @param element the element to get the id for
	 * @return a string containing the id column
	 */
	public String getIdColumnText(Object element) {
		if (element == null) {
			return RscTools.empty();
		}

		String text = RscTools.empty();
		IContentProvider contentProvider = TreeViewerID.this.getContentProvider();
		if (contentProvider instanceof ITreeContentProvider) {
			text = generateId(((ITreeContentProvider) contentProvider).getParent(element), element);
		}
		return text;
	}

	/**
	 * @param parent  the element parent
	 * @param element the element to generate an id for
	 * @return a generated id for the element
	 */
	private String generateId(Object parent, Object element) {

		if (element == null) {
			return RscTools.empty();
		}

		if (parent == null) {
			return generateAlphabeticalId(element);
		} else if (TreeViewerID.this.getContentProvider() instanceof ITreeContentProvider) {
			ITreeContentProvider treeContentProvider = (ITreeContentProvider) TreeViewerID.this.getContentProvider();

			int nbParent = getNbParent(element);
			String parentId = generateId(treeContentProvider.getParent(parent), parent);
			String elementId = null;
			if (nbParent % 2 == 0) {
				elementId = generateAlphabeticalId(element);
			} else {
				elementId = generateNumericalId(element);
			}
			return parentId + elementId;
		} else {
			return RscTools.empty();
		}
	}

	/**
	 * @param element the element to generate an id for
	 * @return an alphabetical id depending of the index of the element
	 */
	private String generateAlphabeticalId(Object element) {

		// Initialize
		String id;
		String generatedId = RscTools.empty();

		if (element == null) {
			return generatedId;
		}

		Object input = TreeViewerID.this.getInput();
		if (TreeViewerID.this.getContentProvider() instanceof ITreeContentProvider) {
			ITreeContentProvider treeContentProvider = (ITreeContentProvider) TreeViewerID.this.getContentProvider();

			List<Object> elements = null;
			Object parent = treeContentProvider.getParent(element);
			if (parent != null) {
				elements = Arrays.asList(treeContentProvider.getChildren(parent));
			} else {
				elements = Arrays.asList(treeContentProvider.getElements(input));
			}

			if (elements != null && !elements.isEmpty()) {
				int index = elements.indexOf(element);
				if (index < 0) {
					index = elements.size();
				}
				id = IDTools.generateAlphabeticId(index);
			} else {
				id = IDTools.ALPHABET.get(0);
			}
			generatedId = String.valueOf(id);
		}

		return generatedId;
	}

	/**
	 * @param element the element to generate an id for
	 * @return a numerical id depending of the index of the element
	 */
	private String generateNumericalId(Object element) {

		// Initialize
		int id = 1;
		String generatedId = RscTools.empty();

		if (element != null) {
			Object input = TreeViewerID.this.getInput();
			if (TreeViewerID.this.getContentProvider() instanceof ITreeContentProvider) {
				ITreeContentProvider treeContentProvider = (ITreeContentProvider) TreeViewerID.this
						.getContentProvider();

				List<Object> elements = null;
				Object parent = treeContentProvider.getParent(element);
				if (parent != null) {
					elements = Arrays.asList(treeContentProvider.getChildren(parent));
				} else {
					elements = Arrays.asList(treeContentProvider.getElements(input));
				}

				if (elements != null && !elements.isEmpty()) {
					int index = elements.indexOf(element);
					if (index >= 0) {
						id = index + 1;
					} else {
						id = elements.size() + 1;
					}
				} else {
					id = 1;
				}
				generatedId = String.valueOf(id);
			}
		}
		return generatedId;
	}

	/**
	 * @param element the element
	 * @return the number of parents of the element
	 */
	private int getNbParent(Object element) {
		if (TreeViewerID.this.getContentProvider() instanceof ITreeContentProvider) {
			ITreeContentProvider treeContentProvider = (ITreeContentProvider) TreeViewerID.this.getContentProvider();
			Object parent = treeContentProvider.getParent(element);
			if (parent == null) {
				return 0;
			} else {
				return 1 + getNbParent(parent);
			}
		} else {
			return 0;
		}
	}
}
