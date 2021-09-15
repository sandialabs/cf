/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.GenericCFDialog;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMTagDateLabelProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMTagDescriptionLabelProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMTagNameLabelProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMTagUserLabelProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.TagViewerCellModifier;
import gov.sandia.cf.parts.viewer.TableFactory;
import gov.sandia.cf.parts.viewer.TableViewerHideSelection;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.ColumnViewerSupport;
import gov.sandia.cf.parts.viewer.editors.GenericTableListContentProvider;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to assess a PCMM subelement
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMManageTagDialog extends GenericCFDialog<PCMMViewManager> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMManageTagDialog.class);

	private static final int MIN_DIALOG_WIDTH = 600;
	private static final int MIN_DIALOG_HEIGHT = 250;

	/**
	 * Table columns width
	 */
	/** PCMM NAME column width */
	public static final int COL_NAME_WIDTH = 100;
	/** PCMM DATE column width */
	public static final int COL_DATE_WIDTH = 150;
	/** PCMM USER column width */
	public static final int COL_USER_WIDTH = 100;
	/** PCMM DESCRIPTION column width */
	public static final int COL_DESC_WIDTH = 250;

	/**
	 * Table editors
	 */
	private Map<TableItem, TableEditor> viewEditors;

	/**
	 * the table viewer
	 */
	private TableViewerHideSelection tableTag;

	/**
	 * Use this constructor to create tag
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 */
	public PCMMManageTagDialog(PCMMViewManager viewManager, Shell parentShell) {
		super(viewManager, parentShell);

		// Make sure you dispose these buttons when viewer input changes
		viewEditors = new HashMap<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_TITLE));
		setMessage(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_MSG), IMessageProvider.INFORMATION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		// form container
		Composite formContainer = new Composite(container, SWT.NONE);
		formContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout(1, true);
		formContainer.setLayout(gridLayout);
		gridLayout.verticalSpacing = PartsResourceConstants.DEFAULT_GRIDDATA_V_INDENT;

		/**
		 * viewer tag
		 */
		// viewer general properties initialization
		tableTag = new TableViewerHideSelection(formContainer,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		tableTag.getTable().setLayoutData(gdViewer);
		tableTag.getTable().setHeaderVisible(true);
		tableTag.getTable().setLinesVisible(true);

		// Header colors
		tableTag.getTable().setHeaderForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));
		tableTag.getTable().setHeaderBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY));

		// Tree - Layout
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(tableTag);
		tableTag.getTable().setLayout(viewerLayout);

		// Height
		gdViewer.heightHint = tableTag.getTable().getItemHeight();

		/**
		 * construct fixed viewer columns
		 */
		List<String> columnList = new ArrayList<>();
		// Name
		TableViewerColumn nameColumn = new TableViewerColumn(tableTag, SWT.LEFT);
		nameColumn.getColumn().setText(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_LBL));
		nameColumn.setLabelProvider(new PCMMTagNameLabelProvider(getViewManager()));
		viewerLayout.addColumnData(new ColumnWeightData(COL_NAME_WIDTH, true));
		columnList.add(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_LBL));

		// Date Creation
		TableViewerColumn dateColumn = new TableViewerColumn(tableTag, SWT.LEFT);
		dateColumn.getColumn().setText(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_DATE));
		dateColumn.setLabelProvider(new PCMMTagDateLabelProvider());
		viewerLayout.addColumnData(new ColumnWeightData(COL_DATE_WIDTH, true));
		columnList.add(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_DATE));

		// User Creation
		TableViewerColumn userColumn = new TableViewerColumn(tableTag, SWT.LEFT);
		userColumn.getColumn().setText(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_USER));
		userColumn.setLabelProvider(new PCMMTagUserLabelProvider());
		viewerLayout.addColumnData(new ColumnWeightData(COL_USER_WIDTH, true));
		columnList.add(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_USER));

		// Description
		TableViewerColumn descriptionColumn = new TableViewerColumn(tableTag, SWT.LEFT);
		descriptionColumn.getColumn().setText(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_DESC));
		descriptionColumn.setLabelProvider(new PCMMTagDescriptionLabelProvider());
		int descriptionColWidth = parent.getSize().x == 0 ? COL_DESC_WIDTH : parent.getSize().x - COL_NAME_WIDTH;
		viewerLayout.addColumnData(new ColumnWeightData(descriptionColWidth, true));
		columnList.add(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_DESC));

		// Description - Action View
		TableViewerColumn actionViewColumn = new TableViewerColumn(tableTag, SWT.LEFT);
		actionViewColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_VIEW));
		actionViewColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TableItem item = (TableItem) cell.getItem();
				Object element = cell.getElement();

				// View editor
				TableEditor editor = null;
				if (!viewEditors.containsKey(element)) {

					// Button
					ButtonTheme btnViewItem = TableFactory.createViewButtonColumnAction(getViewManager().getRscMgr(),
							cell);
					btnViewItem.addListener(SWT.Selection, event -> {
						if (element instanceof Tag) {
							ManagePCMMTagViewDialog dialog = new ManagePCMMTagViewDialog(getViewManager(), getShell());
							dialog.openDialog((Tag) element);
						}
					});

					// Draw cell
					editor = new TableEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnViewItem, item, cell.getColumnIndex());

					viewEditors.put(item, editor);
				}
			}
		});
		viewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnList.add(RscTools.getString(RscConst.MSG_BTN_VIEW));

		// table settings
		String[] columnProperties = columnList.stream().toArray(String[]::new);
		tableTag.setColumnProperties(columnProperties);
		tableTag.setContentProvider(new GenericTableListContentProvider());
		TagViewerCellModifier pcmmEvidenceCellModifier = new TagViewerCellModifier(this, columnProperties);
		tableTag.setCellModifier(pcmmEvidenceCellModifier);

		// modifications on double click
		ColumnViewerSupport.enableDoubleClickEditing(tableTag);

		// Key press on tree events
		tableTag.getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent event) {
				if (SWT.DEL == event.keyCode) {
					deleteTags();
				}
			}

		});

		// set table row height to DEFAULT
		tableTag.getTable().addListener(SWT.MeasureItem, new Listener() {
			private TableItem previousItem = null;

			@Override
			public void handleEvent(Event event) {
				event.height = PartsResourceConstants.TABLE_ROW_HEIGHT;
				TableItem item = (TableItem) event.item;
				if (item != null && !item.equals(previousItem)) {
					previousItem = item;
					refreshTableButtons(item);
				}
			}
		});

		// Reload
		reload();

		return container;
	}

	/**
	 * Refresh the table buttons
	 * 
	 * @param item
	 */
	private void refreshTableButtons(TableItem item) {
		ViewTools.refreshTableEditor(viewEditors.get(item));
	}

	/**
	 * Reload the view data
	 */
	public void reload() {
		// Refresh
		if (viewEditors != null) {
			viewEditors.forEach((item, editor) -> ViewTools.disposeViewerEditor(editor));
			viewEditors.clear();
		}

		tableTag.setInput(getViewManager().getAppManager().getService(IPCMMApplication.class).getTags());
		tableTag.refresh();
		tableTag.getTable().layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Set the new title of the dialog
		newShell.setText(RscTools.getString(RscConst.MSG_TAG_DIALOG_VIEWTITLE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);

		switch (buttonId) {
		case IDialogConstants.PROCEED_ID:
			deleteTags();
			break;
		case IDialogConstants.FINISH_ID:
			// save the credibility process
			getViewManager().getCredibilityEditor().doSave(new NullProgressMonitor());

			// close it
			close();
			break;
		default:
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// Options
		Map<String, Object> btnOptions = new HashMap<>();
		btnOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_DELETE));
		btnOptions.put(ButtonTheme.OPTION_OUTLINE, false);
		btnOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_DELETE);
		btnOptions.put(ButtonTheme.OPTION_ICON_SIZE, 14);
		btnOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_RED);

		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		ButtonTheme button = new ButtonTheme(getViewManager().getRscMgr(), parent, SWT.PUSH, btnOptions);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(Integer.valueOf(IDialogConstants.PROCEED_ID));
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonPressed(((Integer) e.widget.getData()).intValue());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				buttonPressed(((Integer) e.widget.getData()).intValue());

			}
		});
		setButtonLayoutData(button);

		// Ok button
		createButton(parent, IDialogConstants.FINISH_ID, RscTools.getString(RscConst.MSG_BTN_DONE), true);
	}

	private void deleteTags() {

		// Get all tags selected
		List<Tag> tagsToDelete = new ArrayList<>();
		List<String> tagsToDeleteName = new ArrayList<>();
		tagsToDeleteName.add(RscTools.empty());
		ISelection selection = tableTag.getSelection();
		boolean containsCurrentSelectedTag = false;

		if (selection != null && !selection.isEmpty()) {

			// get viewer selection to delete selected
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			List<?> objectList = structuredSelection.toList();

			String toDeleteNameFormat = "{0} ({1})"; //$NON-NLS-1$

			for (Object obj : objectList) {
				if (obj instanceof Tag) {
					tagsToDelete.add((Tag) obj);
					tagsToDeleteName.add(MessageFormat.format(toDeleteNameFormat, ((Tag) obj).getName(),
							DateTools.formatDate(((Tag) obj).getDateTag(), DateTools.getDateTimeFormat())));

					if (obj.equals(getViewManager().getSelectedTag())) {
						containsCurrentSelectedTag = true;
						break;
					}
				}
			}
		}

		// check if the list of tags to delete does not contain the currently selected
		// tag in the PCMM view
		if (containsCurrentSelectedTag) {
			MessageDialog.openWarning(getShell(), RscTools.getString(RscConst.MSG_TAG_DIALOG_VIEWTITLE),
					RscTools.getString(RscConst.ERR_TAG_DIALOG_DELETING_SELECTEDTAG_MSG));
		} else {
			if (!tagsToDelete.isEmpty()) {

				String joinChars = "\n-"; //$NON-NLS-1$
				// ask the user to confirm deletion
				boolean confirmDelete = MessageDialog.openConfirm(getShell(),
						RscTools.getString(RscConst.MSG_TAG_DIALOG_VIEWTITLE),
						RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_DELETE_CONFIRM_QUESTION,
								String.join(joinChars, tagsToDeleteName)));

				if (confirmDelete) {
					// delete the selected tags
					for (Tag tag : tagsToDelete) {
						try {
							getViewManager().getAppManager().getService(IPCMMApplication.class).deleteTag(tag);
						} catch (CredibilityException e) {
							MessageDialog.openWarning(getShell(), RscTools.getString(RscConst.MSG_TAG_DIALOG_VIEWTITLE),
									RscTools.getString(RscConst.ERR_TAG_DIALOG_DELETING_MSG, tag.getName()));
							logger.error("An error has occurred while deleting tag {}:\n{}", tag.getName(), //$NON-NLS-1$
									e.getMessage(), e);
						}
					}

					// inform of the deletion success
					MessageDialog.openInformation(getShell(), RscTools.getString(RscConst.MSG_TAG_DIALOG_VIEWTITLE),
							RscTools.getString(RscConst.MSG_TAG_DIALOG_DELETE_SUCCESS));

					// reload the view
					reload();
				}
			}
		}
	}

}
