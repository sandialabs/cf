/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.listeners.SortTableColumnListener;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTQueryResultTableLabelProvider;
import gov.sandia.cf.parts.viewer.editors.GenericTableListContentProvider;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Quantity of interest view: it is the QoI home page to select, open and delete
 * a QoI
 * 
 * @author Didier Verstraete
 * @param <T> the result type class
 *
 */
public class PIRTQueryResultView<T> extends ACredibilitySubView<PIRTQueryResultViewController<T>> {

	/**
	 * the quantity of interest table viewer
	 */
	private TableViewer tableResult;

	/**
	 * Instantiates a new PIRT query result view.
	 *
	 * @param viewController the parent view manager
	 * @param parent         the parent composite
	 * @param style          the view style
	 */
	public PIRTQueryResultView(PIRTQueryResultViewController<T> viewController, Composite parent, int style) {
		super(viewController, parent, style);

		// create the view
		if (getViewController().getPirtQuery() != null) {
			createPage();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_PIRT_QUERYRSVIEW_TITLE, RscTools.getString(RscConst.MSG_TITLE_EMPTY),
				RscTools.getString(RscConst.MSG_TITLE_EMPTY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_PIRT_QUERYRSVIEW_ITEMTITLE);
	}

	/**
	 * Creates the phenomena view and all its components
	 * 
	 * @param parent the parent composite
	 */
	private void createPage() {

		GridLayout gridLayout = new GridLayout();
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.setLayout(gridLayout);

		/**
		 * Header
		 */
		// reset title with the query name
		setTitle(RscTools.getString(RscConst.MSG_PIRT_QUERYRSVIEW_ITEMTITLE,
				getViewController().getPirtQuery().getName(),
				DateTools.formatDate(DateTools.getCurrentDate(), DateTools.getDateTimeFormat())));

		/**
		 * table Result
		 */
		// table general properties initialization
		tableResult = new TableViewer(this,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.MULTI);
		tableResult.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableResult.getTable().setHeaderVisible(true);
		tableResult.getTable().setLinesVisible(true);
		final TableLayout tablePhenomenaLayout = new TableLayout();
		tableResult.getTable().setLayout(tablePhenomenaLayout);

		// sort column listener
		SortTableColumnListener sortListener = new SortTableColumnListener(tableResult);

		List<String> columnProperties = new ArrayList<>();

		// sort column listener
		new SortTableColumnListener(tableResult);

		// table editors, modifiers, providers
		tableResult.addDoubleClickListener(event -> {
			// TODO implement clicking on a result
		});

		List<T> queryResult = getViewController().getQueryResult();
		if (queryResult != null && !queryResult.isEmpty()) {
			for (Field field : queryResult.get(0).getClass().getDeclaredFields()) {

				// ignore static fields (in particular serialVersionUID)
				if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
					columnProperties.add(field.getName());
					TableViewerColumn tempColumn = new TableViewerColumn(tableResult, SWT.LEFT);
					tempColumn.getColumn().addListener(SWT.Selection, sortListener);
					tempColumn.getColumn().setText(field.getName());
					tablePhenomenaLayout.addColumnData(new ColumnWeightData(1, true));
					new TextCellEditor(tableResult.getTable());
				}
			}
		}

		// table editors, modifiers, providers
		tableResult.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		tableResult.setContentProvider(new GenericTableListContentProvider());
		tableResult.setLabelProvider(new PIRTQueryResultTableLabelProvider(tableResult));

		/**
		 * Footer
		 */
		// Composite for buttons
		Composite compositeButtons = new Composite(this, SWT.NONE);
		compositeButtons.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtons.setLayout(new RowLayout());

		// button Back

		Map<String, Object> btnBackOptions = new HashMap<>();
		btnBackOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BACK));
		btnBackOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnBackOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_BACK);
		btnBackOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnBackOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewController().getViewManager().openHomePage());
		Button btnBack = new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtons, SWT.CENTER,
				btnBackOptions);

		// button Close
		Map<String, Object> btnCloseOptions = new HashMap<>();
		btnCloseOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_CLOSE));
		btnCloseOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnCloseOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_CLOSE);
		btnCloseOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnCloseOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewController().getViewManager().closePage(getViewController().getPirtQuery()));
		Button btnClose = new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtons,
				SWT.PUSH | SWT.CENTER, btnCloseOptions);

		// buttons global settings
		FontTools.setButtonFont(getViewController().getViewManager().getRscMgr(), btnBack);
		FontTools.setButtonFont(getViewController().getViewManager().getRscMgr(), btnClose);

		// layout view
		compositeButtons.layout();

		// Refresh
		refresh();
	}

	/**
	 * Sets the table data.
	 *
	 * @param data the new table data
	 */
	void setTableData(Object data) {
		if (tableResult != null) {
			tableResult.setInput(data);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {
		getViewController().reloadData();
	}

	/**
	 * @return the qoi view shell
	 */
	public Shell getQoIShell() {
		return getShell();
	}

	/**
	 * refreshes table viewers
	 */
	public void refreshViewer() {
		tableResult.refresh();
	}

	/**
	 * @see gov.sandia.cf.parts.ui.pirt.PIRTViewManager#close(gov.sandia.cf.model.QuantityOfInterest)
	 * 
	 * @param qoi the qoi to close associated page
	 */
	public void closePage(QuantityOfInterest qoi) {
		getViewController().getViewManager().closePage(qoi);
	}

}
