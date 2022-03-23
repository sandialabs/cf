/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.parts.listeners.ViewerSelectionKeepBackgroundColor;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.viewer.TableViewerHideSelection;
import gov.sandia.cf.parts.viewer.editors.GenericTableListContentProvider;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.web.services.global.IModelWebClient;

/**
 * The Class NewCFProcessWebProjectExistingSetupPage.
 *
 * @author Didier Verstraete
 */
public class NewCFProcessWebProjectExistingSetupPage extends WizardPage
		implements INewCFProcessWebProjectExistingSetupPage {
	/**
	 * the parent wizard
	 */
	private NewCFProcessWizard parent;

	/** The model. */
	private Model model;

	/** The table project selector. */
	private TableViewerHideSelection tableProjectSelector;

	/**
	 * The constructor
	 * 
	 * @param parent the parent wizard
	 */
	public NewCFProcessWebProjectExistingSetupPage(NewCFProcessWizard parent) {
		super(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_EXISTINGSETUP_PAGE_PAGENAME));
		setTitle(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_EXISTINGSETUP_PAGE_TITLE));
		setDescription(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_EXISTINGSETUP_PAGE_DESCRIPTION));

		this.parent = parent;

		this.model = null;
	}

	/** {@inheritDoc} */
	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		// label
		FormFactory.createLabel(container,
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_EXISTINGSETUP_PAGE_LBL));

		// project selector table
		tableProjectSelector = new TableViewerHideSelection(container,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION, true, false);

		GridData gdTable = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		tableProjectSelector.getTable().setLayoutData(gdTable);
		tableProjectSelector.getTable().setHeaderVisible(false);
		tableProjectSelector.getTable().setLinesVisible(true);
		gdTable.heightHint = 40;
		gdTable.widthHint = parent.getSize().x - 2 * ((GridLayout) container.getLayout()).horizontalSpacing;
		final TableLayout tableLayout = new TableLayout();
		tableProjectSelector.getTable().setLayout(tableLayout);
		List<String> columnProperties = new ArrayList<>();

		// 1 - Model
		TableViewerColumn modelColumn = new TableViewerColumn(tableProjectSelector, SWT.LEFT | SWT.WRAP);
		modelColumn.getColumn().setText(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBPROJECT_EXISTINGSETUP_PAGE_TABLE_COLUMN_MODEL));
		tableLayout.addColumnData(new ColumnWeightData(1));
		columnProperties.add(modelColumn.getColumn().getText());
		modelColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String format = "{0} - {1}"; //$NON-NLS-1$
				return (element instanceof Model)
						? MessageFormat.format(format, ((Model) element).getApplication(),
								((Model) element).getContact())
						: RscTools.empty();
			}

			@Override
			public Image getImage(Object element) {
				if (model != null && model.equals(element)) {
					return IconTheme.getIconImage(
							NewCFProcessWebProjectExistingSetupPage.this.parent.getResourceManager(),
							IconTheme.ICON_NAME_UPTODATE, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN));
				} else {
					return super.getImage(element);
				}
			}
		});

		// Tree editors, modifiers, providers
		tableProjectSelector.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		tableProjectSelector.setContentProvider(new GenericTableListContentProvider());

		// set row height
		tableProjectSelector.getTable().addListener(SWT.MeasureItem, new Listener() {
			private TableItem previousItem = null;

			@Override
			public void handleEvent(Event event) {
				event.height = 40;
				TableItem item = (TableItem) event.item;
				if (item != null && !item.equals(previousItem)) {
					previousItem = item;
				}
			}
		});

		// SWT.EraseItem: this event is called when repainting the tree not on removing
		// an element of the tree
		// In this case, it keeps cell colors when selection is active on cell
		tableProjectSelector.getTable().addListener(SWT.EraseItem,
				new ViewerSelectionKeepBackgroundColor(tableProjectSelector) {

					@Override
					public boolean isConditionFulfilled(Object data) {
						return true;
					}
				});

		tableProjectSelector.addSelectionChangedListener(this::handleToggleButtonSelection);

		// load data
		reload();

		setControl(container);
	}

	/**
	 * Reload.
	 */
	public void reload() {

		List<Model> models = new ArrayList<>();

		if (isCurrentPage()) {

			// load model list
			List<Model> list = parent.getWebClientManager().getService(IModelWebClient.class).list();
			if (list != null) {
				models.addAll(list);
			}
		}

		tableProjectSelector.setInput(models);

		// refresh the viewer
		tableProjectSelector.refresh();
	}

	/**
	 * Handle toggle button selection.
	 *
	 * @param event the event
	 */
	private void handleToggleButtonSelection(SelectionChangedEvent event) {

		// get selection
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		Model modelSelected = (Model) selection.getFirstElement();

		// if selection has already been done on the same element, deselect
		if (Objects.equals(model, modelSelected)) {
			model = null;
		} else {
			model = modelSelected;
		}

		// refresh table
		tableProjectSelector.refresh();

		// refresh Next button
		setPageComplete(isPageValid());
	}

	/** {@inheritDoc} */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			reload();
		}
	}

	@Override
	public IWizardPage getPreviousPage() {
		return parent.getPageWebProjectType();
	}

	@Override
	public boolean isPageComplete() {
		return super.isPageComplete() && isPageValid();
	}

	/**
	 * Checks if is page valid.
	 *
	 * @return true, if is page valid
	 */
	private boolean isPageValid() {
		return this.model != null;
	}

	@Override
	public Model getSelectedModel() {
		return model;
	}

}
