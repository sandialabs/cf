/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.launcher.CredibilityEditor;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.dto.configuration.PIRTQuery;
import gov.sandia.cf.parts.model.BreadcrumbItemParts;
import gov.sandia.cf.parts.ui.ACredibilityView;
import gov.sandia.cf.parts.ui.AViewManager;
import gov.sandia.cf.parts.ui.ICredibilityView;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.IDTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Quantity of interest view: it is the QoI home page to select, open and delete
 * a QoI
 * 
 * @author Didier Verstraete
 *
 */
/**
 * @author Didier Verstraete
 *
 */
public class PIRTViewManager extends AViewManager implements IViewManager {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTViewManager.class);

	/**
	 * Buttons events PIRT QOI
	 */
	/** PIRT ADD QOI button event name */
	public static final String BTN_EVENT_PIRT_QOI_ADD_QOI = "PIRT_QOI_ADD_QOI"; //$NON-NLS-1$
	/** PIRT DELETE QOI button event name */
	public static final String BTN_EVENT_PIRT_QOI_DELETE_QOI = "PIRT_QOI_DELETE_QOI"; //$NON-NLS-1$
	/** PIRT OPEN QOI button event name */
	public static final String BTN_EVENT_PIRT_QOI_OPEN_QOI = "PIRT_QOI_OPEN_QOI"; //$NON-NLS-1$

	/**
	 * QoI Home view id in mapQoIItem items map
	 */
	public static final String QOI_HOME_ITEM_ID = "-1"; //$NON-NLS-1$

	/**
	 * Map to store QoI items. Map key is the QoI id and map value is for the item
	 */
	private Map<String, CTabItem> mapQoIItem;

	/**
	 * Stores the items
	 */
	private PIRTTabFolder folder;

	/**
	 * The constructor
	 * 
	 * @param parent     the parent composite
	 * @param parentView the parent view
	 * @param style      the view style
	 */
	public PIRTViewManager(MainViewManager parentView, Composite parent, int style) {
		super(parentView, parent, style);
		mapQoIItem = new HashMap<>();
		createPage();
		openHomePage();
	}

	/**
	 * Creates the view with all the components
	 * 
	 * @param parent
	 * 
	 */
	private void createPage() {

		// set the layout
		this.setLayout(new FillLayout());

		// tab folder that manage tab items
		this.folder = new PIRTTabFolder(this, SWT.BOTTOM);

		// Tab QoI Home, can not be closed
		CTabItem tab = new CTabItem(folder, SWT.NONE);
		tab.setText(RscTools.getString(RscConst.MSG_QOITABBEDVIEW_QOIHOME));

		if (viewManager.getCache().getPIRTSpecification() != null) {
			tab.setControl(new PIRTQoIView(this, folder, SWT.NONE));
		}

		// add the home page tab
		mapQoIItem.put(QOI_HOME_ITEM_ID, tab);

		// tab item close listener
		this.folder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			@Override
			public void close(CTabFolderEvent event) {
				if (event != null && event.item != null) {
					// remove tab from mapQoIItem
					closePage(event.item.getData());
				} else {
					logger.error("Can not remove item from mapQoIItem"); //$NON-NLS-1$
				}
			}
		});
	}

	/**
	 * Adds a new page tab with Phenomena View for @param qoi. If page already
	 * exists, does nothing
	 * 
	 * @param qoi the qoi to add in a page
	 */
	public void addPage(QuantityOfInterest qoi) {

		if (qoi == null) {
			logger.error("Can not add tab item for null qoi"); //$NON-NLS-1$
		} else if (!containsPage(qoi)) {
			// create new qoi tab
			CTabItem tab = new CTabItem(folder, SWT.NONE | SWT.CLOSE);
			tab.setText(getQoiTabName(qoi));
			tab.setData(qoi);
			tab.setControl(new PIRTPhenomenaView(this, folder, SWT.NONE, qoi));

			// add tab to mapQoIItem to link item to qoi id
			addPage(qoi, tab);
		}
	}

	/**
	 * Adds a new page tab with Phenomena View for @param pirtQuery. If page already
	 * exists, does nothing. If result is empty or null, does nothing.
	 * 
	 * @param pirtQuery the pirt query to add in a page
	 * @param result    the pirt query result to display
	 */
	public void addPage(PIRTQuery pirtQuery, List<Object> result) {

		if (pirtQuery == null) {
			logger.error("Can not add tab item for null pirtQuery"); //$NON-NLS-1$
		} else if (result == null || result.isEmpty()) {
			logger.error("Can not add tab item for null result"); //$NON-NLS-1$
		} else if (!containsPage(pirtQuery)) {
			// create new result tab
			CTabItem tab = new CTabItem(folder, SWT.NONE | SWT.CLOSE);
			tab.setText(getQueryResultTabName(pirtQuery));
			tab.setData(pirtQuery);
			tab.setControl(new PIRTQueryResultView<Object>(this, folder, pirtQuery, result, SWT.NONE));

			// add tab to mapQoIItem to link item to qoi id
			addPage(pirtQuery, tab);
		}
	}

	/**
	 * Reloads qoi tab name
	 * 
	 * @param qoi the qoi to reload
	 */
	public void reloadTabName(QuantityOfInterest qoi) {
		if (qoi == null) {
			logger.error("Can not reload tab item for null qoi"); //$NON-NLS-1$
		} else if (qoi.getId() == null) {
			logger.error("Can not reload tab item for qoi id null"); //$NON-NLS-1$
		} else if (containsPage(qoi)) {
			// reload param qoi tab
			getPage(qoi).setText(getQoiTabName(qoi));
		}
	}

	/**
	 * @see gov.sandia.cf.parts.ui.MainViewManager#plugBackHomeButton(org.eclipse.swt.widgets.Button)
	 * @param button the button to plug on back home action
	 */
	@Override
	public void plugBackHomeButton(Button button) {
		viewManager.plugBackHomeButton(button);
	}

	/**
	 * If mapQoIItem contains @param qoi, open the phenomena view for @param qoi
	 * 
	 * @param qoi the qoi to open associated page
	 */
	public void openPage(QuantityOfInterest qoi) {
		if (qoi == null) {
			logger.error("Can not open tab item for null qoi"); //$NON-NLS-1$
		} else if (!containsPage(qoi)) {
			logger.warn("Can not open tab item for qoi: {}\nQoi is not present in view.", qoi); //$NON-NLS-1$
		} else {
			this.folder.setSelection(getPage(qoi));
		}
	}

	/**
	 * If mapQoIItem contains @param pirtQuery, open the phenomena view for @param
	 * pirtQuery
	 * 
	 * @param pirtQuery the pirt query to open associated page
	 */
	public void openPage(PIRTQuery pirtQuery) {
		if (pirtQuery == null) {
			logger.error("Can not open tab item for null pirtQuery"); //$NON-NLS-1$
		} else {
			this.folder.setSelection(getPage(pirtQuery));
		}
	}

	/**
	 * Open the qoi home view
	 */
	public void openHomePage() {
		this.folder.setSelection(mapQoIItem.get(QOI_HOME_ITEM_ID));
	}

	/**
	 * Closes page for @param qoi and drop it from mapQoIItem
	 * 
	 * @param qoi the qoi to close associated page
	 */
	public void close(QuantityOfInterest qoi) {
		if (qoi == null) {
			logger.error("Can not close tab item for null qoi"); //$NON-NLS-1$
		} else if (containsPage(qoi)) {
			closePage(qoi);
		}
	}

	/**
	 * Closes page for @param pirtQuery and drop it from mapQoIItem
	 * 
	 * @param pirtQuery the pirt query to close associated page
	 */
	public void close(PIRTQuery pirtQuery) {
		if (pirtQuery == null) {
			logger.error("Can not close tab item for null pirtQuery"); //$NON-NLS-1$
		} else if (containsPage(pirtQuery)) {
			closePage(pirtQuery);
		}
	}

	/**
	 * Adds a new item to the map with a unique identifier
	 * 
	 * @param obj the object to add in a page
	 * @param tab the tab to add
	 */
	private void addPage(Object obj, CTabItem tab) {
		mapQoIItem.put(IDTools.getObjInstanceUniqueId(obj), tab);
	}

	/**
	 * @param obj the object to check
	 * @return true if obj is in the item map
	 */
	private boolean containsPage(Object obj) {
		return mapQoIItem.containsKey(IDTools.getObjInstanceUniqueId(obj));
	}

	/**
	 * @param obj the object to check associated page
	 * @return the item associated to obj
	 */
	private CTabItem getPage(Object obj) {
		return mapQoIItem.get(IDTools.getObjInstanceUniqueId(obj));
	}

	/**
	 * Closes the page associated to obj
	 * 
	 * @param obj the object to close associated page
	 */
	public void closePage(Object obj) {
		if (obj != null) {
			String identityHashCode = IDTools.getObjInstanceUniqueId(obj);

			if (mapQoIItem.get(identityHashCode) != null) {

				// close param qoi tab
				mapQoIItem.get(identityHashCode).dispose();

				// remove tab from mapQoIItem
				mapQoIItem.remove(identityHashCode);
			}
		}
	}

	/**
	 * Reload the QOI home view
	 */
	public void reloadQOIView() {
		if (mapQoIItem != null && mapQoIItem.get(QOI_HOME_ITEM_ID) != null
				&& mapQoIItem.get(QOI_HOME_ITEM_ID).getControl() != null
				&& mapQoIItem.get(QOI_HOME_ITEM_ID).getControl() instanceof PIRTQoIView) {
			// Refresh
			((PIRTQoIView) mapQoIItem.get(QOI_HOME_ITEM_ID).getControl()).refresh();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void openHome() {
		refreshQOIView();
	}

	/**
	 * Refresh the QOI home view
	 */
	public void refreshQOIView() {
		if (mapQoIItem != null && mapQoIItem.get(QOI_HOME_ITEM_ID) != null
				&& mapQoIItem.get(QOI_HOME_ITEM_ID).getControl() != null
				&& mapQoIItem.get(QOI_HOME_ITEM_ID).getControl() instanceof PIRTQoIView) {
			((PIRTQoIView) mapQoIItem.get(QOI_HOME_ITEM_ID).getControl()).refresh();
		}
	}

	/**
	 * Refresh save state
	 */
	public void refreshSaveState() {
		mapQoIItem.forEach((key, view) -> {
			if (null != view.getControl()) {
				((ACredibilityView<?>) view.getControl()).refreshStatusComposite();
			}
		});
	}

	/**
	 * @param pirtQuery the pirt query
	 * @return the pirt query tab name
	 */
	private String getQueryResultTabName(PIRTQuery pirtQuery) {
		String tabText = RscTools.empty();
		if (pirtQuery == null) {
			logger.error("Can not get tab name for null pirtQuery"); //$NON-NLS-1$
		} else {
			tabText = RscTools.getString(RscConst.MSG_PIRT_TAB_QUERY, pirtQuery.getName() != null ? pirtQuery.getName()
					: RscTools.getString(RscConst.MSG_QOITABBEDVIEW_QOINONAME));
		}
		return tabText;
	}

	/**
	 * @param qoi the qoi
	 * @return the qoi tab name
	 */
	private String getQoiTabName(QuantityOfInterest qoi) {
		String tabText = RscTools.empty();
		if (qoi == null) {
			logger.error("Can not get tab name for null qoi"); //$NON-NLS-1$
		} else if (qoi.getId() == null) {
			logger.error("Can not get tab name for qoi id null"); //$NON-NLS-1$
		} else {

			if (qoi.getTagDate() == null) {
				tabText = (qoi.getSymbol() != null ? qoi.getSymbol()
						: RscTools.getString(RscConst.MSG_QOITABBEDVIEW_QOINONAME));
			} else {
				tabText = RscTools.getString(RscConst.MSG_QOITABBEDVIEW_TAB_NAME_TAGGED,
						(qoi.getSymbol() != null ? qoi.getSymbol()
								: RscTools.getString(RscConst.MSG_QOITABBEDVIEW_QOINONAME)),
						DateTools.formatDate(qoi.getTagDate(), RscTools.getString(RscConst.DATE_FORMAT_SHORT)));

			}
		}
		return tabText;
	}

	/**
	 * @return the credibility Editor
	 */
	@Override
	public CredibilityEditor getCredibilityEditor() {
		if (viewManager == null) {
			return null;
		}
		return viewManager.getCredibilityEditor();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Queue<BreadcrumbItemParts> getBreadcrumbItems(ACredibilityView<?> view) {
		Queue<BreadcrumbItemParts> breadcrumbItems = this.viewManager.getBreadcrumbItems(view);

		// add the PIRT home view
		BreadcrumbItemParts bcHomeItemPart = new BreadcrumbItemParts();
		if (mapQoIItem.get(QOI_HOME_ITEM_ID) != null && mapQoIItem.get(QOI_HOME_ITEM_ID).getControl() != null) {
			bcHomeItemPart
					.setName(((ACredibilityView<?>) mapQoIItem.get(QOI_HOME_ITEM_ID).getControl()).getItemTitle());
		} else {
			bcHomeItemPart.setName(view.getItemTitle());
		}
		bcHomeItemPart.setListener(this);
		breadcrumbItems.add(bcHomeItemPart);

		// if it exists, add the PIRT views that depends of this manager
		if (!(view instanceof PIRTQoIView)) {
			BreadcrumbItemParts bcItemPart = new BreadcrumbItemParts();
			bcItemPart.setName(view.getItemTitle());
			bcItemPart.setListener(this);
			breadcrumbItems.add(bcItemPart);
		}
		return breadcrumbItems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doBreadcrumbAction(BreadcrumbItemParts item) {
		if (item != null && item.getListener().equals(this) && item.getName()
				.equals(((ACredibilityView<?>) mapQoIItem.get(QOI_HOME_ITEM_ID).getControl()).getItemTitle())) {
			openHomePage();
		}
	}

	/**
	 * Open the help level view
	 */
	public void openPIRTHelpLevelView() {
		viewManager.openHelpLevelView();
	}

	@Override
	public void reload() {
		mapQoIItem.forEach((id, view) -> {
			if (view != null && view.getControl() instanceof ICredibilityView) {
				((ICredibilityView) view.getControl()).reload();
			}
		});
	}

	@Override
	public void reloadActiveView() {
		if (folder != null && folder.getSelection() != null
				&& folder.getSelection().getControl() instanceof ICredibilityView) {
			((ICredibilityView) folder.getSelection().getControl()).reload();
		}
	}
}
