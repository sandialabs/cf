/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.dialogs.importation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.ImportSchema;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.parts.dialogs.GenericCFScrolledDialog;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.configuration.ConfigurationViewManager;
import gov.sandia.cf.parts.viewer.TreeViewerHideSelection;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.widgets.CollapsibleWidget;
import gov.sandia.cf.parts.widgets.FancyToolTipSupport;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to manage configuration importation.
 * 
 * @author Didier Verstraete
 *
 */
public class ImportDialog extends GenericCFScrolledDialog<ConfigurationViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportDialog.class);

	/**
	 * The import analysis
	 */
	private Map<Class<?>, Map<ImportActionType, List<?>>> analysis;

	/**
	 * The changes approved
	 */
	private Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> changesApproved;

	/**
	 * The import schema
	 */
	private ImportSchema importSchema;

	/**
	 * The constructor
	 * 
	 * @param viewManager  the view manager
	 * @param parentShell  the parent shell
	 * @param analysis     the analysis to initialize the import dialog
	 * @param importSchema the import schema
	 */
	public ImportDialog(ConfigurationViewManager viewManager, Shell parentShell,
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis, ImportSchema importSchema) {
		super(viewManager, parentShell);
		this.analysis = analysis;
		this.importSchema = importSchema;

		this.changesApproved = new HashMap<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_IMPORTDLG_SUBTITLE, importSchema.getName()));
		setMessage(RscTools.getString(RscConst.MSG_IMPORTDLG_DESC, importSchema.getName()),
				IMessageProvider.INFORMATION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Composite createDialogScrolledContent(Composite parent) {

		logger.debug("Create ImportDialog content"); //$NON-NLS-1$

		// content
		Composite content = new Composite(parent, SWT.FILL | SWT.BORDER);
		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout();
		content.setLayout(gridLayout);
		content.setBackground(ColorTools.toColor(getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));
		content.setLayout(gridLayout);

		// render analysis content
		if (analysis != null) {
			for (Entry<Class<?>, Map<ImportActionType, List<?>>> entry : analysis.entrySet()) {

				Class<?> key = entry.getKey();
				Map<ImportActionType, List<?>> value = entry.getValue();

				// class container
				Composite classContainer = new Composite(content, SWT.NONE);
				GridData analysisData = new GridData(SWT.FILL, SWT.FILL, true, true);
				classContainer.setLayoutData(analysisData);
				GridLayout gdClassContainer = new GridLayout();
				gdClassContainer.marginWidth = 0;
				classContainer.setLayout(gdClassContainer);
				classContainer.setBackground(ColorTools.toColor(getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));

				// collapsible widget
				String importableName = getViewManager().getAppManager().getService(IImportApplication.class)
						.getImportableName(key);
				new CollapsibleWidget(getViewManager().getRscMgr(), content, SWT.BORDER, classContainer, importableName,
						false, true);

				// to add tree
				renderImportElement(classContainer, ImportActionType.TO_ADD, key, value);

				// to update tree
				renderImportElement(classContainer, ImportActionType.TO_UPDATE, key, value);

				// to delete tree
				renderImportElement(classContainer, ImportActionType.TO_DELETE, key, value);

				// no changes tree
				renderImportElement(classContainer, ImportActionType.NO_CHANGES, key, value);
			}
		}

		return content;
	}

	/**
	 * Render the components to select the changes to apply.
	 * 
	 * @param parent       the parent composite
	 * @param importClass  the import class
	 * @param importAction the import action
	 * @param value        the import analysis
	 */
	private void renderImportElement(Composite parent, ImportActionType importAction, Class<?> importClass,
			Map<ImportActionType, List<?>> value) {

		// class container
		Composite importActionComposite = new Composite(parent, SWT.NONE);
		GridData scData = new GridData(SWT.FILL, SWT.FILL, true, true);
		importActionComposite.setLayoutData(scData);
		importActionComposite.setLayout(new GridLayout());

		// label for importable
		String label = null;
		String className = getViewManager().getAppManager().getService(IImportApplication.class)
				.getImportableName(importClass);
		if (ImportActionType.TO_ADD.equals(importAction)) {
			label = RscTools.getString(RscConst.MSG_IMPORTDLG_IMPORTCLASS_TOADD, className);
		} else if (ImportActionType.TO_UPDATE.equals(importAction)) {
			label = RscTools.getString(RscConst.MSG_IMPORTDLG_IMPORTCLASS_TOUPDATE, className);
		} else if (ImportActionType.TO_DELETE.equals(importAction)) {
			label = RscTools.getString(RscConst.MSG_IMPORTDLG_IMPORTCLASS_TODELETE, className);
		} else if (ImportActionType.NO_CHANGES.equals(importAction)) {
			label = RscTools.getString(RscConst.MSG_IMPORTDLG_IMPORTCLASS_NOCHANGES, className);
		}
		Label labelAction = FormFactory.createLabel(importActionComposite, label);
		FontTools.setBoldFont(getViewManager().getRscMgr(), labelAction);

		// changes tree
		if (value.containsKey(importAction) && !value.get(importAction).isEmpty()) {

			// create tree
			boolean withCheckbox = !ImportActionType.NO_CHANGES.equals(importAction);
			final TreeViewer treeImport = renderTreeInit(importActionComposite, withCheckbox);

			// Tree - Label Provider
			treeImport.setLabelProvider(new ImportChangeTreeLabelProvider());

			// Tree - populate tree
			if (PCMMPlanningQuestion.class.equals(importClass)) {
				treeImport.setContentProvider(new ImportChangePCMMPlanningQuestionTreeContentProvider());
			} else {
				treeImport.setContentProvider(new ImportChangeTreeContentProvider());
			}
			treeImport.setInput(value.get(importAction));
			treeImport.expandAll();

			// add tree selection listener
			addTreeListener(treeImport, importClass, importAction);

			// checked default configuration
			if (ImportActionType.TO_ADD.equals(importAction) || ImportActionType.TO_UPDATE.equals(importAction)) {
				boolean checked = true;
				Arrays.stream(treeImport.getTree().getItems()).forEach(itemTmp -> {
					checkItems(itemTmp, checked);
					changeSelectionAction(itemTmp, importClass, importAction);
				});
			} else if (ImportActionType.TO_DELETE.equals(importAction)) {
				boolean checked = false;
				Arrays.stream(treeImport.getTree().getItems()).forEach(itemTmp -> checkItems(itemTmp, checked));
			}

		} else {
			FormFactory.createFormLabel(importActionComposite,
					RscTools.getString(RscConst.MSG_IMPORTDLG_NOCHANGES, label));
		}
	}

	/**
	 * Initialize import tree.
	 *
	 * @param parent       the parent composite
	 * @param withCheckbox the with checkbox
	 * @return the tree viewer created
	 */
	private TreeViewer renderTreeInit(Composite parent, boolean withCheckbox) {

		int style = 0;
		if (withCheckbox) {
			style = SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION;
		} else {
			style = SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION;
		}

		// Tree - Create
		TreeViewerHideSelection treeViewer = new TreeViewerHideSelection(parent, style);
		GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		treeViewer.getTree().setLayoutData(gdViewer);
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);

		FancyToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE);

		// Tree - Layout
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(treeViewer);
		treeViewer.getTree().setLayout(viewerLayout);

		// Tree - Hint
		gdViewer.minimumHeight = 150;
		gdViewer.heightHint = treeViewer.getTree().getItemHeight();
		gdViewer.widthHint = getViewManager().getSize().x - 2 * ((GridLayout) parent.getLayout()).horizontalSpacing;

		// Tree - Customize
		treeViewer.getTree().setHeaderBackground(ColorTools.toColor(getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		treeViewer.getTree().setHeaderForeground(ColorTools.toColor(getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));

		return treeViewer;
	}

	/**
	 * Check/Uncheck all the associated subelements to this treeItem.
	 *
	 * @param treeImport   the tree import
	 * @param importClass  the import class
	 * @param importAction the import action
	 */
	private void addTreeListener(TreeViewer treeImport, Class<?> importClass, ImportActionType importAction) {

		treeImport.getTree().addListener(SWT.Selection, event -> {
			if (event.item instanceof TreeItem && event.detail == SWT.CHECK) {
				changeSelectionAction((TreeItem) event.item, importClass, importAction);
			}
		});
	}

	/**
	 * Add/Remove all the checked item into the item changes map
	 * 
	 * @param treeItem     the treeItem to verify
	 * @param importClass  the import class
	 * @param importAction the import action
	 */
	private void changeSelectionAction(TreeItem treeItem, Class<?> importClass, ImportActionType importAction) {
		boolean checked = treeItem.getChecked();
		List<TreeItem> changedItems = checkItems(treeItem, checked);
		changedItems.add(treeItem);

		// add/remove from changes list
		if (!ImportActionType.NO_CHANGES.equals(importAction)) {
			if (!changesApproved.containsKey(importClass)) {
				changesApproved.put(importClass, new EnumMap<>(ImportActionType.class));
			}
			if (!changesApproved.get(importClass).containsKey(importAction)) {
				changesApproved.get(importClass).put(importAction, new ArrayList<>());
			}
			List<IImportable<?>> itemsImportableValues = getItemsImportableValues(changedItems);
			if (checked) {
				changesApproved.get(importClass).get(importAction).addAll(itemsImportableValues);
			} else {
				changesApproved.get(importClass).get(importAction).removeAll(itemsImportableValues);
			}
		}

		// set ok button action
		setOkButtonEnabled(hasChanges());
	}

	/**
	 * @return true if the there is at least one change selected. Otherwise false.
	 */
	private boolean hasChanges() {
		if (!changesApproved.isEmpty()) {
			for (Map<ImportActionType, List<IImportable<?>>> map : changesApproved.values()) {
				if (!map.isEmpty()) {
					long nbValues = map.values().stream().filter(list -> list != null && !list.isEmpty()).count();
					if (nbValues > 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Check tree items
	 * 
	 * @param item    the tree item to check/uncheck
	 * @param checked check value
	 * @return the list of changed items
	 */
	private List<TreeItem> checkItems(TreeItem item, boolean checked) {
		List<TreeItem> itemChanged = new ArrayList<>();
		item.setGrayed(false);
		item.setChecked(checked);
		TreeItem[] items = item.getItems();
		itemChanged.addAll(Arrays.asList(items));
		for (int i = 0; i < items.length; i++) {
			itemChanged.addAll(checkItems(items[i], checked));
		}
		return itemChanged;
	}

	private List<IImportable<?>> getItemsImportableValues(List<TreeItem> items) {
		List<IImportable<?>> data = new ArrayList<>();
		for (TreeItem item : items) {
			if (item.getData() instanceof IImportable) {
				data.add((IImportable<?>) item.getData());
			}
		}
		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		// Set the new title of the dialog
		newShell.setText(RscTools.getString(RscConst.MSG_IMPORTDLG_TITLE));
	}

	/**
	 * Enable/Disable the ok button
	 * 
	 * @param enabled enable/disable
	 */
	private void setOkButtonEnabled(boolean enabled) {
		// enable/disable ok button
		Button button = ImportDialog.super.getButton(IDialogConstants.OK_ID);
		if (button != null) {
			button.setEnabled(enabled);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, RscTools.getString(RscConst.MSG_IMPORTDLG_BTN_CONFIRM), true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

		// change ok button
		setOkButtonEnabled(hasChanges());
	}

	/**
	 * @return the quantity of interest to create
	 */
	public Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> openDialog() {
		if (open() == Window.OK) {

			boolean openConfirm = true;

			// add confirmation to delete data
			if (containsDeleteAction()) {
				openConfirm = MessageDialog.openConfirm(getShell(),
						RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_TITLE),
						RscTools.getString(RscConst.MSG_IMPORTDLG_WRN_DATADELETION_CONFIRM));
			}

			return openConfirm ? this.changesApproved : null;
		}

		return null;
	}

	private boolean containsDeleteAction() {
		if (this.changesApproved != null) {
			for (Map<ImportActionType, List<IImportable<?>>> entry : this.changesApproved.values()) {
				if (entry != null && entry.containsKey(ImportActionType.TO_DELETE)) {
					return true;
				}
			}
		}
		return false;
	}
}
