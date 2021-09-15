/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.requirement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.ISystemRequirementApplication;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.ui.requirement.editors.SystemRequirementTreeContentProvider;
import gov.sandia.cf.parts.viewer.TreeViewerID;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.GenericTableLabelProvider;
import gov.sandia.cf.parts.widgets.FancyToolTipSupport;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to select a system requirement
 * 
 * @author Didier Verstraete
 * @param <V> the view manager class
 *
 */
public class SystemRequirementSelectorDialog<V extends IViewManager> extends GenericCFSmallDialog<V> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SystemRequirementSelectorDialog.class);

	/**
	 * The requirement to create
	 */
	private SystemRequirement requirementSelected;

	/**
	 * the tree viewer
	 */
	private TreeViewerID treeViewer;

	/**
	 * The tree expanded state
	 */
	protected HashMap<Integer, Boolean> expandedState = new HashMap<>();

	private Composite formContainer;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 */
	public SystemRequirementSelectorDialog(V viewManager, Shell parentShell) {
		super(viewManager, parentShell);

		// Set mode
		mode = DialogMode.VIEW;

		requirementSelected = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_TITLE));
	}

	/**
	 * @return the requirement selected
	 */
	public SystemRequirement openDialog() {
		if (open() == Window.OK) {
			return requirementSelected;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_PAGENAME_VIEW));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, RscTools.getString(RscConst.MSG_BTN_SELECT), true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		logger.debug("Create System Requirement dialog area"); //$NON-NLS-1$

		Composite container = (Composite) super.createDialogArea(parent);

		// scroll container
		ScrolledComposite scrollContainer = new ScrolledComposite(container, SWT.V_SCROLL);
		GridData scrollScData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrollScData.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
		scrollScData.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
		scrollContainer.setLayoutData(scrollScData);
		scrollContainer.setLayout(new GridLayout());

		// form container
		formContainer = new Composite(scrollContainer, SWT.NONE);
		GridData scData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scData.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
		scData.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
		formContainer.setLayoutData(scData);
		GridLayout gridLayout = new GridLayout(2, false);
		formContainer.setLayout(gridLayout);

		// render System Requirements table
		renderSysRequirementsTable();

		// set scroll container size
		scrollContainer.setContent(formContainer);
		scrollContainer.setExpandHorizontal(true);
		scrollContainer.setExpandVertical(true);
		scrollContainer.setMinSize(formContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		formContainer.addListener(SWT.Resize,
				e -> scrollContainer.setMinSize(formContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT)));
		formContainer
				.addPaintListener(e -> scrollContainer.setMinSize(formContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT)));

		// set ok button to disabled
		setEnableOkButton(false);

		// Load data
		loadData();

		// Return Control
		return container;
	}

	/**
	 * Render system requirement tree
	 */
	private void renderSysRequirementsTable() {

		// Initialize table
		renderMainTableInit();

		// Initialize data
		List<String> columnProperties = new ArrayList<>();
		columnProperties.add(treeViewer.getIdColumn().getColumn().getText());
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) treeViewer.getTree().getLayout();

		// Create statement column
		TreeViewerColumn statementColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
		statementColumn.getColumn().setText(RscTools.getString(RscConst.MSG_SYSREQUIREMENT_STATEMENT));
		treeViewerLayout
				.addColumnData(new ColumnWeightData(PartsResourceConstants.GENPARAM_TABLE_TEXT_COLUMN_COEFF, true));
		statementColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String statement = null;
				if (element instanceof SystemRequirement) {
					statement = ((SystemRequirement) element).getStatement();
				}
				return statement;
			}

			@Override
			public Color getBackground(Object element) {
				return getTreeCellBackground(element);
			}

			@Override
			public Color getForeground(Object element) {
				return getTreeCellForeground(element);
			}
		});
		columnProperties.add(RscTools.getString(RscConst.MSG_SYSREQUIREMENT_STATEMENT));

		// Columns - Generic Parameters
		if (getViewManager().getCache().getSystemRequirementSpecification() != null) {
			for (SystemRequirementParam parameter : getViewManager().getCache().getSystemRequirementSpecification()
					.getParameters()) {
				TreeViewerColumn treeCol = renderMainTableAddParametersColumns(parameter);
				if (treeCol != null) {
					columnProperties.add(treeCol.getColumn().getText());
				}
			}
		}

		// Add listeners
		renderMainTableAddEvents();

		// Tree - Properties
		treeViewer.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		treeViewer.setContentProvider(new SystemRequirementTreeContentProvider());

		// Refresh
		treeViewer.refresh(true);

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		treeViewer.getTree().addListener(SWT.EraseItem, new ViewerSelectionKeepBackgroundColor(treeViewer) {

			@Override
			public boolean isConditionFulfilled(Object data) {
				return data instanceof SystemRequirement;
			}
		});

		// Layout
		treeViewer.getTree().layout();
	}

	/**
	 * Add tree viewer events
	 */
	private void renderMainTableAddEvents() {
		// Get tree
		Tree tree = treeViewer.getTree();

		tree.addListener(SWT.MeasureItem, event -> event.height = PartsResourceConstants.TABLE_ROW_HEIGHT);

		tree.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				requirementSelected = getSelected();

				// enable/disable ok button
				setEnableOkButton(requirementSelected != null);
			}
		});
	}

	/**
	 * Initialize Main table
	 */
	private void renderMainTableInit() {
		// Tree - Create
		treeViewer = new TreeViewerID(formContainer, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);

		Tree tree = treeViewer.getTree();
		tree.setLayoutData(gdViewer);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		FancyToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE);

		// Tree - Layout
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(treeViewer);
		treeViewer.setLayout(viewerLayout);

		// Tree - Hint
		gdViewer.heightHint = tree.getItemHeight();
		gdViewer.widthHint = formContainer.getSize().x - 2 * ((GridLayout) formContainer.getLayout()).horizontalSpacing;

		// Tree - Customize
		tree.setHeaderBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY));
		tree.setHeaderForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));

		// Set width
		treeViewer.getIdColumn().setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof SystemRequirement) {
					((SystemRequirement) element).setGeneratedId(treeViewer.getIdColumnText(element));
				}
				return treeViewer.getIdColumnText(element);
			}

			@Override
			public Color getBackground(Object element) {
				return getTreeCellBackground(element);
			}

			@Override
			public Color getForeground(Object element) {
				return getTreeCellForeground(element);
			}
		});

	}

	/**
	 * Add parameter column
	 * 
	 * @return the tree column created
	 */
	private TreeViewerColumn renderMainTableAddParametersColumns(SystemRequirementParam parameter) {
		// Get Tree and layout
		Tree tree = treeViewer.getTree();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) tree.getLayout();

		// Initialize
		TreeViewerColumn tempColumn = null;

		// Cell editor depending of column type
		if (FormFieldType.TEXT.getType().equals(parameter.getType())
				|| FormFieldType.RICH_TEXT.getType().equals(parameter.getType())
				|| FormFieldType.LINK.getType().equals(parameter.getType())) {
			tempColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
			tempColumn.getColumn().setText(parameter.getName());
			treeViewerLayout
					.addColumnData(new ColumnWeightData(PartsResourceConstants.GENPARAM_TABLE_TEXT_COLUMN_COEFF, true));
		} else if (FormFieldType.SELECT.getType().equals(parameter.getType())) {
			tempColumn = new TreeViewerColumn(treeViewer, SWT.CENTER);
			tempColumn.getColumn().setText(parameter.getName());
			treeViewerLayout.addColumnData(
					new ColumnWeightData(PartsResourceConstants.GENPARAM_TABLE_SELECT_COLUMN_COEFF, true));
		}

		if (null != tempColumn) {
			tempColumn.setLabelProvider(new GenericTableLabelProvider(parameter, getViewManager()) {

				@Override
				public Color getBackground(Object element) {
					return getTreeCellBackground(element);
				}

				@Override
				public Color getForeground(Object element) {
					return getTreeCellForeground(element);
				}
			});
		}
		return tempColumn;
	}

	/**
	 * Get First Requirement Selected
	 * 
	 * @return the first requirement selected of the requirement viewer
	 */
	private SystemRequirement getSelected() {
		ISelection selection = treeViewer.getSelection();
		if (selection != null && !selection.isEmpty()) {
			Object elt = ((IStructuredSelection) selection).getFirstElement();
			if (elt instanceof SystemRequirement) {
				return (SystemRequirement) elt;
			}
		}
		return null;
	}

	/**
	 * Get cell background color
	 * 
	 * @param element
	 * @return Color the color
	 */
	private Color getTreeCellBackground(Object element) {
		//
		if (element instanceof SystemRequirement) {
			SystemRequirement req = (SystemRequirement) element;

			if (req.getParent() != null && req.getChildren() != null && !req.getChildren().isEmpty()) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT_2);
			} else if (req.getParent() == null) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT);
			}
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE);
		}

		return null;
	}

	/**
	 * Get cell foreground color
	 * 
	 * @param element
	 * @return Color the color
	 */
	private Color getTreeCellForeground(Object element) {

		// Manage levels
		if (element instanceof SystemRequirement) {
			SystemRequirement req = (SystemRequirement) element;
			if (req.getParent() == null) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE);
			}

			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK);
		}

		return null;
	}

	/**
	 * Load Requirement data
	 */
	private void loadData() {

		// Get Model
		Model model = getViewManager().getCache().getModel();
		List<SystemRequirement> requirementList = new ArrayList<>();

		// Get data
		if (model != null) {
			// Get list
			requirementList = this.getViewManager().getAppManager().getService(ISystemRequirementApplication.class)
					.getRequirementRootByModel(model);
		}

		treeViewer.setInput(requirementList);
		refreshViewer();
	}

	/**
	 * Refresh view
	 * 
	 * @param expandGroup The flag to force expand group
	 */
	private void refreshViewer() {
		// Get tree data
		@SuppressWarnings("unchecked")
		List<SystemRequirement> input = (List<SystemRequirement>) treeViewer.getInput();

		// Initialize expanded states
		initExpandedState(input);

		// sort all lists
		if (input != null) {
			int index = 0;
			input.sort(Comparator.comparing(SystemRequirement::getId));

			for (SystemRequirement parent : input) {
				// Get tree item
				TreeItem item = treeViewer.getTree().getItems()[index];

				// Restore expanded states
				if (null != item && expandedState.containsKey(parent.getId())) {
					item.setExpanded(expandedState.get(parent.getId()));
				}

				// Sort
				if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
					parent.getChildren().sort(Comparator.comparing(SystemRequirement::getId));
				}
				index++;
			}
		}

		treeViewer.refresh(true);
	}

	/**
	 * Initialize expanded states
	 * 
	 * @param input The list of requirement group from tree
	 */
	private void initExpandedState(List<SystemRequirement> input) {
		if (expandedState.isEmpty()) {
			for (SystemRequirement parent : input) {
				expandedState.put(parent.getId(), true);
			}
		}
	}

}
