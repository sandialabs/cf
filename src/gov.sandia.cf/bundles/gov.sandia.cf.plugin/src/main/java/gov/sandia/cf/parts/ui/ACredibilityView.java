/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.opal.breadcrumb.Breadcrumb;
import org.eclipse.nebula.widgets.opal.breadcrumb.BreadcrumbItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.parts.model.BreadcrumbItemParts;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Qn abstract class to force the credibility views to implement methods needed
 * for the view managers
 * 
 * @author Didier Verstraete
 *
 * @param <V> the view manager
 */
public abstract class ACredibilityView<V extends IViewManager> extends Composite implements ICredibilityView {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ACredibilityView.class);

	/**
	 * The view manager
	 */
	private V viewManager;

	/**
	 * the breadcrumb
	 */
	private Breadcrumb breadCrumb;

	/**
	 * Title elements
	 */
	protected CLabel lblTitle;

	/**
	 * Save State elements
	 */
	private Composite saveState;
	private ButtonTheme btnState;
	private CLabel lblState;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parent      the parent composite
	 * @param style       the style
	 */
	protected ACredibilityView(V viewManager, Composite parent, int style) {

		// Initialize
		super(parent, style);
		logger.debug("Create view"); //$NON-NLS-1$
		this.viewManager = viewManager;

		// Main composite (this)
		GridLayout gridLayout = new GridLayout(1, false);
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.setLayout(gridLayout);

		// Header
		GridLayout gridTitleLayout = new GridLayout(3, false);
		Composite headerComposite = new Composite(this, SWT.FILL);
		headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		headerComposite.setLayout(gridTitleLayout);
		gridTitleLayout.marginHeight = 0;
		gridTitleLayout.verticalSpacing = 0;

		// Header - Save state
		saveState = new Composite(headerComposite, SWT.LEFT);
		saveState.setLayout(new GridLayout(3, false));
		saveState.setLayoutData(new GridData());

		// Initialize save label
		lblState = new CLabel(saveState, SWT.LEFT);
		lblState.setLayoutData(new GridData());
		FontTools.setBoldFont(viewManager.getRscMgr(), lblState);

		// Button save
		Map<String, Object> btnStateOptions = new HashMap<>();
		btnStateOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_SAVE));
		btnStateOptions.put(ButtonTheme.OPTION_OUTLINE, false);
		btnStateOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_SAVE);
		btnStateOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		btnStateOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> {
			if (event != null) {
				getViewManager().getCredibilityEditor().doSave(new NullProgressMonitor());
				refresh();
			}
		});
		btnState = new ButtonTheme(viewManager.getRscMgr(), saveState, SWT.LEFT, btnStateOptions);
		btnState.setLayoutData(new GridData());
		btnState.setVisible(false);

		refreshSaveState();

		// Header - label title
		lblTitle = new CLabel(headerComposite, SWT.CENTER);
		FontTools.setTitleFont(viewManager.getRscMgr(), lblTitle);
		lblTitle.setText(getTitle());
		lblTitle.setForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY));
		GridData gdTitle = new GridData(SWT.CENTER, SWT.NONE, true, true);
		lblTitle.setLayoutData(gdTitle);

		// Header - button Configuration
		Map<String, Object> btnManageTagOptions = new HashMap<>();
		btnManageTagOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_CONFIGURATION));
		btnManageTagOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnManageTagOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_CONFIG);
		btnManageTagOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		ButtonTheme btnConfig = new ButtonTheme(viewManager.getRscMgr(), headerComposite, SWT.CENTER,
				btnManageTagOptions);
		getViewManager().plugConfigurationButton(btnConfig);

		// initialize lbl information variables
		GC gcButtonConf = new GC(btnConfig);
		final int iconSize = 25;
		final int lblVersionSize = gcButtonConf.textExtent(RscTools.getString(RscConst.MSG_BTN_CONFIGURATION)).x
				+ iconSize;
		gcButtonConf.dispose();

		GC gcLblTitle = new GC(lblTitle);
		final int lblTitleSize = gcLblTitle.textExtent(lblTitle.getText()).x;
		gcLblTitle.dispose();

		// add listener to adapt the title size and the lbl version depending of the
		// composite size
		headerComposite.addListener(SWT.Resize, new Listener() {

			boolean previousVisible = true;
			int margin = 15;

			@Override
			public void handleEvent(Event event) {
				if (lblTitleSize > 0) {
					// delete the lbl version if there is no space
					boolean visible = headerComposite.getSize().x > saveState.getSize().x + lblTitleSize
							+ lblVersionSize + (margin * 2);
					if (previousVisible != visible) {
						if (visible) {
							btnConfig.setText(RscTools.getString(RscConst.MSG_BTN_CONFIGURATION));
						} else {
							btnConfig.setText(""); //$NON-NLS-1$
						}
						headerComposite.layout();
						previousVisible = visible;
					}
				}
			}
		});
	}

	/**
	 * @return the view manager
	 */
	public V getViewManager() {
		return viewManager;
	}

	/**
	 * @param parent the parent composite
	 * @return the breadcrumb component
	 */
	protected Breadcrumb createBreadcrumb(Composite parent) {

		if (breadCrumb == null) {

			breadCrumb = new Breadcrumb(parent, SWT.NONE);
			FontTools.setButtonFont(viewManager.getRscMgr(), breadCrumb);

			Queue<BreadcrumbItemParts> items = getViewManager().getBreadcrumbItems(this);

			for (BreadcrumbItemParts itemPart : items) {
				if (itemPart != null) {
					final BreadcrumbItem item = new BreadcrumbItem(breadCrumb, SWT.PUSH);
					item.setText(itemPart.getName());
					item.setTextColor(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK));
					item.setTextColorSelected(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY));
					item.setWidth(item.getWidth() + 15);
					item.setAlignment(SWT.CENTER);
					addItemListener(item, itemPart);

					// set the current view item selected
					if (getItemTitle() != null && getItemTitle().equals(itemPart.getName())) {
						item.setSelection(true);
					}
				}
			}

			GridData lblSubtitleGridData = new GridData();
			lblSubtitleGridData.grabExcessHorizontalSpace = true;
			breadCrumb.setLayoutData(lblSubtitleGridData);
		}

		return breadCrumb;
	}

	/**
	 * Add a selection listener to the breadcrumb item to do breadcrumb itemPart
	 * action.
	 * 
	 * @param item     the item to add
	 * @param itemPart the item part to add
	 */
	private void addItemListener(BreadcrumbItem item, BreadcrumbItemParts itemPart) {

		if (item != null && itemPart != null && itemPart.getListener() != null) {
			item.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					itemPart.getListener().doBreadcrumbAction(itemPart);
				}
			});
		}
	}

	/**
	 * Set the title
	 * 
	 * @param title the view title
	 */
	public void setTitle(String title) {

		// set text
		lblTitle.setText(title);

		// repaint the title
		lblTitle.requestLayout();

		// notify the title parent that the title size changed
		lblTitle.getParent().notifyListeners(SWT.Resize, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GridLayout getLayout() {
		return (GridLayout) super.getLayout();
	}

	/**
	 * Refresh data and view state elements
	 */
	public void refresh() {
		reload();
		refreshSaveState();
	}

	/**
	 * Refresh save state
	 */
	public void refreshSaveState() {
		if (!getViewManager().getCredibilityEditor().isDirty()) {
			// Saved
			lblState.setText(RscTools.getString(RscConst.MSG_SAVED));
			lblState.setImage(IconTheme.getIconImage(getViewManager().getRscMgr(), IconTheme.ICON_NAME_UPTODATE,
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));
			lblState.setForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));
			lblState.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN));

			// Hide save button
			btnState.setVisible(false);
		} else {
			// Not saved
			lblState.setText(RscTools.getString(RscConst.MSG_NOT_SAVED));
			lblState.setImage(IconTheme.getIconImage(getViewManager().getRscMgr(), IconTheme.ICON_NAME_CLOSE,
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK)));
			lblState.setForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK));
			lblState.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_YELLOW));

			// Show save button
			btnState.setVisible(true);
		}

		// Layout
		saveState.layout();
	}

	/**
	 * Display a new information message
	 * 
	 * @param title   the message title
	 * @param message the message
	 */
	public void displayInfo(String title, String message) {
		MessageDialog.openInformation(getShell(), title, message);
	}

	/**
	 * Display a new question message
	 * 
	 * @param title   the message title
	 * @param message the message
	 */
	public void displayQuestion(String title, String message) {
		MessageDialog.openQuestion(getShell(), title, message);
	}

	/**
	 * Display a new warning message
	 * 
	 * @param title   the message title
	 * @param message the message
	 */
	public void displayWarning(String title, String message) {
		MessageDialog.openWarning(getShell(), title, message);
	}

	/**
	 * Display a new error message
	 * 
	 * @param title   the message title
	 * @param message the message
	 */
	public void displayError(String title, String message) {
		MessageDialog.openError(getShell(), title, message);
	}
}
