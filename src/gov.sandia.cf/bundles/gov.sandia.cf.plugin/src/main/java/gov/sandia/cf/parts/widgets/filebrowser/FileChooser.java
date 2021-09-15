/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets.filebrowser;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * The file chooser widget to select a file into the active project.
 * 
 * @author Didier Verstraete
 *
 */
public class FileChooser extends Composite {

	/**
	 * The path
	 */
	private Text pathText;

	/**
	 * The title
	 */
	private String title = null;

	/**
	 * Restrict the file selection to the browse button (no copy-paste, text
	 * edition)
	 */
	private boolean onlyBrowse;

	/**
	 * The resource manager
	 */
	private ResourceManager rscMgr;

	/**
	 * Constructor:
	 * 
	 * By default, onlyBrowse parameter is setted to true
	 * 
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param parent the parent composite
	 */
	public FileChooser(ResourceManager rscMgr, Composite parent) {
		this(rscMgr, parent, true);
	}

	/**
	 * Constructor
	 * 
	 * @param rscMgr     the resource manager used to manage the resources (fonts,
	 *                   colors, images, cursors...)
	 * @param parent     the parent composite
	 * @param onlyBrowse restrict the file selection to the browse button
	 */
	public FileChooser(ResourceManager rscMgr, Composite parent, boolean onlyBrowse) {
		super(parent, SWT.NULL);

		Assert.isNotNull(rscMgr);
		this.rscMgr = rscMgr;

		this.onlyBrowse = onlyBrowse;
		createContent();
	}

	/**
	 * Create composite content
	 */
	public void createContent() {
		// Layout
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);

		// Path - Text field
		pathText = new Text(this, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.grabExcessVerticalSpace = false;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.CENTER;
		pathText.setLayoutData(gd);
		pathText.setEditable(!onlyBrowse);

		// Browse button
		Map<String, Object> optionsBtnBrowse = new HashMap<>();
		optionsBtnBrowse.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowse.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowse.put(ButtonTheme.OPTION_OUTLINE, true);
		Button browseButton = FormFactory.createButton(rscMgr, this, null, optionsBtnBrowse);
		((GridData) browseButton.getLayoutData()).verticalAlignment = SWT.CENTER;
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Initialize
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
						Display.getDefault().getActiveShell(), new WorkbenchLabelProvider(),
						new BaseWorkbenchContentProvider());
				dialog.setTitle("Open"); //$NON-NLS-1$
				dialog.setInput(WorkspaceTools.getActiveProject());
				dialog.setAllowMultiple(false);
				dialog.setInitialSelection(getFile());

				// Open
				if (dialog.open() == IDialogConstants.OK_ID) {
					final IResource resource = (IResource) dialog.getFirstResult();
					if (resource != null && pathText != null) {
						final String path = resource.getFullPath().toPortableString();
						pathText.setText(path);
						pathText.notifyListeners(SWT.CHANGED, new Event());
					}
				}
			}
		});
	}

	/**
	 * Sets the file chooser default value
	 * 
	 * @param defaultValue the default value
	 */
	public void setDefaultValue(String defaultValue) {
		this.pathText.setText(defaultValue);
	}

	/**
	 * @return the file chooser path
	 */
	public String getText() {
		return pathText.getText();
	}

	/**
	 * @return the file chooser text control
	 */
	public Text getTextControl() {
		return pathText;
	}

	/**
	 * Get file
	 * 
	 * @return The file
	 */
	public IFile getFile() {
		if (pathText == null || pathText.getText() == null || pathText.getText().length() == 0) {
			return null;
		}
		return WorkspaceTools.getFileInWorkspaceForPath(new Path(pathText.getText()));
	}

	@SuppressWarnings("javadoc")
	public String getTitle() {
		return title;
	}

	@SuppressWarnings("javadoc")
	public void setTitle(String title) {
		this.title = title;
	}

	@SuppressWarnings("javadoc")
	public boolean isOnlyBrowse() {
		return onlyBrowse;
	}

	@SuppressWarnings("javadoc")
	public void setOnlyBrowse(boolean onlyBrowse) {
		this.onlyBrowse = onlyBrowse;
		pathText.setEditable(!onlyBrowse);
	}

	/**
	 * Adds a new listener
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(Listener listener) {
		pathText.addListener(SWT.CHANGED, listener);
	}
}