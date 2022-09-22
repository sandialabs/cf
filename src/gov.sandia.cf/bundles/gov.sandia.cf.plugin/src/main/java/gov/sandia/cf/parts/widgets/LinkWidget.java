/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.model.dto.configuration.ParameterLinkGson;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.widgets.filebrowser.FileChooser;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.GsonTools;
import gov.sandia.cf.tools.LinkTools;
import gov.sandia.cf.tools.NetTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * The Link widget to select a file or an URL
 * 
 * @author Didier Verstraete
 *
 */
public class LinkWidget extends AHelperWidget {

	// editable fields
	private Button fileBtn;
	private Button urlBtn;
	private Composite urlComposite;
	private Text urlInput;
	private Composite captionContainer;
	private Text captionText;

	// non editable fields
	private Label textNonEditable;
	private Label captionTextNonEditable;

	// actions
	private Button openBtn;

	// data
	private FormFieldType type;

	/** Is required. */
	private boolean onlyWarning = false;

	/**
	 * List of change listeners (element type: <code>LinkChangedListener</code>).
	 */
	private ListenerList<LinkChangedListener> listeners = new ListenerList<>();
	private StackLayout containerLayout;
	private FileChooser fileChooser;
	private boolean withValidation;
	private IViewManager viewManager;

	/**
	 * @param parent      the parent composite
	 * @param viewManager the view manager
	 * @param style       the style
	 * @param editable    is editable
	 */
	public LinkWidget(Composite parent, IViewManager viewManager, int style, boolean editable) {
		this(parent, viewManager, style, editable, true);
	}

	/**
	 * @param parent         the parent composite
	 * @param viewManager    the view manager
	 * @param style          the style
	 * @param editable       is editable
	 * @param withValidation add link validation
	 */
	public LinkWidget(Composite parent, IViewManager viewManager, int style, boolean editable, boolean withValidation) {
		super(viewManager.getRscMgr(), parent, style, editable);

		Assert.isNotNull(viewManager);
		this.viewManager = viewManager;

		this.withValidation = withValidation;
		this.type = FormFieldType.LINK_FILE;

		// create widget
		createControl();
	}

	/**
	 * @param parent         the parent composite
	 * @param viewManager    the view manager
	 * @param rscMgr         the system resource manager
	 * @param style          the style
	 * @param editable       is editable
	 * @param withValidation add link validation
	 */
	public LinkWidget(Composite parent, IViewManager viewManager, ResourceManager rscMgr, int style, boolean editable,
			boolean withValidation) {
		super(rscMgr, parent, style, editable);

		Assert.isNotNull(viewManager);
		this.viewManager = viewManager;

		this.withValidation = withValidation;
		this.type = FormFieldType.LINK_FILE;

		// create widget
		createControl();
	}

	/**
	 * Create the link content
	 */
	private void createControl() {

		// render control
		if (super.isEditable()) {
			renderEditableField();
		} else {
			renderNonEditableField();
		}

		// create helper
		GridData layoutHelper = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutHelper.horizontalSpan = 2;
		super.createHelper(layoutHelper);
	}

	/**
	 * Render the editable field content
	 */
	private void renderEditableField() {
		// layout data
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		this.setLayout(gridLayout);
		GridData gdContainer = new GridData(SWT.FILL, SWT.FILL, true, false);
		this.setLayoutData(gdContainer);
		this.setBackground(getParent().getBackground());

		// Create Radio buttons
		Composite groupBtn = new Composite(this, SWT.NONE);
		groupBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		groupBtn.setLayout(new RowLayout(SWT.HORIZONTAL));
		groupBtn.setBackground(getParent().getBackground());

		// composite layout
		Composite linkContainer = new Composite(this, SWT.NONE);
		GridData linkGdContainer = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		linkContainer.setLayoutData(linkGdContainer);
		linkContainer.setLayout(gridLayout);
		linkContainer.setBackground(getParent().getBackground());

		// Manage switch visible fields
		containerLayout = new StackLayout();
		linkContainer.setLayout(containerLayout);

		// Tree - Button
		fileBtn = new Button(groupBtn, SWT.RADIO);
		fileBtn.setText(RscTools.getString(RscConst.MSG_LINKWIDGET_BTN_FILE));
		fileBtn.setSelection(true);
		fileBtn.setBackground(getParent().getBackground());

		// URL - Button
		urlBtn = new Button(groupBtn, SWT.RADIO);
		urlBtn.setText(RscTools.getString(RscConst.MSG_LINKWIDGET_BTN_URL));
		urlBtn.setBackground(getParent().getBackground());

		/**
		 * Composite URL
		 */
		urlComposite = new Composite(linkContainer, SWT.NONE);
		urlComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		urlComposite.setLayout(new GridLayout(1, false));
		urlComposite.setBackground(getParent().getBackground());

		// Composite URL - Input text
		urlInput = new Text(urlComposite, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		GridData gdUrlInput = new GridData();
		gdUrlInput.grabExcessHorizontalSpace = true;
		gdUrlInput.grabExcessVerticalSpace = true;
		gdUrlInput.horizontalAlignment = GridData.FILL;
		urlInput.setLayoutData(gdUrlInput);
		urlInput.addListener(SWT.Modify, this::fireChanged);
		urlInput.setBackground(
				ColorTools.toColor(viewManager.getRscMgr(), ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));

		/**
		 * File chooser
		 */
		fileChooser = new FileChooser(viewManager.getRscMgr(), linkContainer);
		fileChooser.setOnlyBrowse(false);
		fileChooser.addListener(this::fireChanged);
		fileChooser.setBackground(getParent().getBackground());

		// select file chooser first
		containerLayout.topControl = fileChooser;

		/**
		 * Open
		 */
		// Open button
		Map<String, Object> optionsOpenBtn = new HashMap<>();
		optionsOpenBtn.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_OPEN));
		optionsOpenBtn.put(ButtonTheme.OPTION_ENABLED, false);
		optionsOpenBtn.put(ButtonTheme.OPTION_OUTLINE, false);
		optionsOpenBtn.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_OPEN);
		optionsOpenBtn.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_PRIMARY);
		openBtn = FormFactory.createButton(viewManager.getRscMgr(), this, null, optionsOpenBtn);
		((GridData) openBtn.getLayoutData()).verticalAlignment = SWT.CENTER;
		((GridData) openBtn.getLayoutData()).horizontalAlignment = SWT.LEFT;
		openBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LinkTools.openLinkValue(getGSONValue(), viewManager.getCache().getOpenLinkBrowserOpts());
			}
		});

		/**
		 * Caption (if image)
		 */
		captionContainer = new Composite(this, SWT.NONE);
		GridData captionGdContainer = new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1);
		captionContainer.setLayoutData(captionGdContainer);
		captionContainer.setLayout(gridLayout);

		FormFactory.createFormLabel(captionContainer,
				RscTools.getString(RscConst.MSG_LINKWIDGET_CAPTION_LBL) + RscTools.COLON);
		captionText = FormFactory.createText(captionContainer, null);
		captionText.addListener(SWT.Modify, this::fireChanged);

		boolean visibleCaption = FormFieldType.LINK_FILE.equals(this.type) && FileTools.isImage(getValue());
		captionContainer.setVisible(visibleCaption);
		((GridData) captionContainer.getLayoutData()).heightHint = visibleCaption ? SWT.DEFAULT : 0;

		// File - Button Listener
		fileBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				// Set visibility
				containerLayout.topControl = fileChooser;
				linkContainer.layout();

				LinkWidget.this.type = FormFieldType.LINK_FILE;

				// Trigger changed listener
				fireChanged(toEvent(event));
			}
		});

		// URL - Button Listener
		urlBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// Set visibility
				containerLayout.topControl = urlComposite;
				linkContainer.layout();

				LinkWidget.this.type = FormFieldType.LINK_URL;

				// Trigger changed listener
				fireChanged(toEvent(event));
			}
		});

		// value changed listener
		addChangedListener(e -> {
			if (!openBtn.isEnabled() && !StringUtils.isBlank(getValue()) && isValid()) {
				openBtn.setEnabled(true);
			} else if (openBtn.isEnabled() && (StringUtils.isBlank(getValue()) || !isValid())) {
				openBtn.setEnabled(false);
			}
		});
	}

	/**
	 * Render the non editable field content
	 */
	private void renderNonEditableField() {
		textNonEditable = FormFactory.createLink(viewManager.getRscMgr(), this, RscTools.empty());

		/**
		 * Caption (if image)
		 */
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;

		captionContainer = new Composite(this, SWT.NONE);
		GridData captionGdContainer = new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1);
		captionContainer.setLayoutData(captionGdContainer);
		captionContainer.setLayout(gridLayout);

		FormFactory.createLabel(captionContainer, RscTools.getString(RscConst.MSG_LINKWIDGET_CAPTION_LBL));

		captionTextNonEditable = FormFactory.createLabel(captionContainer, RscTools.empty());

		boolean visibleCaption = FormFieldType.LINK_FILE.equals(this.type) && FileTools.isImage(getValue());
		captionContainer.setVisible(visibleCaption);
		((GridData) captionContainer.getLayoutData()).heightHint = visibleCaption ? SWT.DEFAULT : 0;
	}

	/**
	 * @return the link type selected
	 */
	public FormFieldType getLinkTypeSelected() {
		return this.type;
	}

	/**
	 * Checks if is only warning.
	 *
	 * @return true, if is only warning
	 */
	public boolean isOnlyWarning() {
		return onlyWarning;
	}

	/**
	 * Sets the only warning.
	 *
	 * @param onlyWarning the new only warning
	 */
	public void setOnlyWarning(boolean onlyWarning) {
		this.onlyWarning = onlyWarning;
	}

	/**
	 * Set the form value
	 * 
	 * @param textValue the value to set
	 */
	public void setValue(String textValue) {
		if (super.isEditable()) {
			setEditableValue(textValue);
			validateLink(NotificationType.WARN);
		} else {
			setNonEditableValue(textValue);
		}
	}

	/**
	 * Set the form editable value
	 * 
	 * @param textValue the value to set
	 */
	public void setEditableValue(String textValue) {

		// Initialize
		ParameterLinkGson linkData = GsonTools.getFromGson(textValue, ParameterLinkGson.class);

		// Set value
		if (linkData != null) {
			this.type = linkData.type;
			if (FormFieldType.LINK_FILE.equals(linkData.type)) {
				setFile(linkData);
			} else if (FormFieldType.LINK_URL.equals(linkData.type)) {
				setURL(linkData.value);
			}
		}
	}

	/**
	 * Set the url value
	 * 
	 * @param url the url to set
	 */
	public void setURL(String url) {

		// Manage button
		fileBtn.setSelection(false);
		urlBtn.setSelection(true);

		String value = url != null ? url : RscTools.empty();
		urlInput.setText(value);

		this.type = FormFieldType.LINK_URL;

		urlBtn.notifyListeners(SWT.Selection, null);
	}

	/**
	 * Sets the file default browser value.
	 *
	 * @param filePath the new file default browser value
	 */
	public void setFileDefaultBrowserValue(String filePath) {
		fileChooser.setBrowserDefaultPathSelection(filePath);
	}

	/**
	 * Set the file.
	 *
	 * @param linkData the new file
	 */
	public void setFile(ParameterLinkGson linkData) {

		// Manage button
		fileBtn.setSelection(true);
		urlBtn.setSelection(false);

		if (linkData != null) {
			String value = linkData.value != null ? linkData.value : RscTools.empty();
			fileChooser.setDefaultValue(value);

			this.type = FormFieldType.LINK_FILE;

			// set caption
			captionText.setText(linkData.caption != null ? linkData.caption : RscTools.empty());
		}

		// notify listeners
		fileBtn.notifyListeners(SWT.Selection, null);
	}

	/**
	 * Set non editable field value
	 * 
	 * @param textValue the value to set
	 */
	private void setNonEditableValue(String textValue) {
		// Get value
		ParameterLinkGson linkData = GsonTools.getFromGson(textValue, ParameterLinkGson.class);
		String linkValue = linkData != null && linkData.value != null ? linkData.value : RscTools.empty();

		// value
		textNonEditable.setText(linkValue);
		textNonEditable.addListener(SWT.Selection,
				event -> LinkTools.openLinkValue(textValue, viewManager.getCache().getOpenLinkBrowserOpts()));

		if (linkData != null) {

			// set type
			this.type = linkData.type;

			// set caption
			captionTextNonEditable.setText(linkData.caption != null ? linkData.caption : RscTools.empty());
			boolean visibleCaption = FormFieldType.LINK_FILE.equals(this.type) && FileTools.isImage(getValue());
			captionContainer.setVisible(visibleCaption);
			((GridData) captionContainer.getLayoutData()).heightHint = visibleCaption ? SWT.DEFAULT : 0;
			captionContainer.requestLayout();
		}
	}

	/**
	 * @return the file selected
	 */
	public IFile getFileSelected() {
		return super.isEditable() ? fileChooser.getFile()
				: WorkspaceTools.getFileInWorkspaceForPath(new Path(textNonEditable.getText()));
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return getFileSelected() != null ? getFileSelected().getName() : null;
	}

	/**
	 * @return the file selected path
	 */
	public String getFileSelectedPath() {
		return super.isEditable() ? fileChooser.getText() : textNonEditable.getText();
	}

	/**
	 * @return the url text
	 */
	public String getURLSelected() {
		return super.isEditable() ? urlInput.getText() : textNonEditable.getText();
	}

	/**
	 * Gets the caption.
	 *
	 * @return the caption
	 */
	public String getCaption() {
		return super.isEditable() ? captionText.getText() : captionTextNonEditable.getText();
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		if (super.isEditable()) {
			return getEditableValue();
		} else {
			return getNonEditableValue();
		}
	}

	/**
	 * @return the editable field value
	 */
	private String getEditableValue() {
		if (FormFieldType.LINK_FILE.equals(getLinkTypeSelected())) {
			return getFileSelectedPath();
		} else if (FormFieldType.LINK_URL.equals(getLinkTypeSelected())) {
			return getURLSelected();
		}
		return RscTools.empty();
	}

	/**
	 * @return the non editable field value
	 */
	private String getNonEditableValue() {
		return textNonEditable.getText();
	}

	/**
	 * @return the value
	 */
	public String getGSONValue() {

		ParameterLinkGson jsonObject = new ParameterLinkGson();

		if (FormFieldType.LINK_FILE.equals(getLinkTypeSelected())) {
			jsonObject.type = FormFieldType.LINK_FILE;
			jsonObject.value = getFileSelectedPath();
			jsonObject.caption = getCaption();
		} else if (FormFieldType.LINK_URL.equals(getLinkTypeSelected())) {
			jsonObject.type = FormFieldType.LINK_URL;
			jsonObject.value = getURLSelected();
			jsonObject.caption = getCaption();
		}

		// Encode JSON
		return GsonTools.toGson(jsonObject);
	}

	/**
	 * @param listener the changed listener
	 */
	public void addChangedListener(LinkChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener the changed listener
	 */
	public void removeChangedListener(LinkChangedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notifies all registered cell editor listeners if the item is changed
	 * 
	 * @param event the event
	 */
	protected void fireChanged(Event event) {

		if (withValidation) {
			validateLink(onlyWarning ? NotificationType.WARN : NotificationType.ERROR);
		}

		// update caption
		boolean visibleCaption = FormFieldType.LINK_FILE.equals(this.type) && FileTools.isImage(getValue());
		captionContainer.setVisible(visibleCaption);
		((GridData) captionContainer.getLayoutData()).heightHint = visibleCaption ? SWT.DEFAULT : 0;
		captionContainer.requestLayout();

		for (LinkChangedListener l : listeners) {
			SafeRunnable.run(new SafeRunnable() {
				@Override
				public void run() {
					l.linkChanged(event);
				}
			});
		}
	}

	/**
	 * @return true if the link is valid, otherwise false.
	 */
	public boolean isValid() {
		Notification notification = checkLink(onlyWarning ? NotificationType.WARN : NotificationType.ERROR);
		return notification == null;
	}

	/**
	 * Apply helpers if the link is not valid. Clear the existing one.
	 */
	public void validateLink() {
		validateLink(onlyWarning ? NotificationType.WARN : NotificationType.ERROR);
	}

	/**
	 * Apply helpers if the link is not valid. Clear the existing one.
	 * 
	 * @param type the notification type
	 */
	public void validateLink(NotificationType type) {

		// clear helper
		clearHelper();

		// apply notification
		Notification notification = checkLink(type);
		if (notification != null) {
			setHelper(notification);
		}
	}

	/**
	 * Check the link input.
	 * 
	 * @param type the notification type
	 * @return the notification if the link is not valid, otherwise null.
	 */
	public Notification checkLink(NotificationType type) {

		Notification notification = null;

		// check link
		if (FormFieldType.LINK_FILE.equals(getLinkTypeSelected())) {
			return checkFile(fileChooser.getText(), type);
		} else if (FormFieldType.LINK_URL.equals(getLinkTypeSelected())) {
			return checkUrl(getURLSelected(), type);
		}

		return notification;
	}

	/**
	 * Check the URL change.
	 * 
	 * @param urlInputValue the url to verify
	 * @param type          the notification type
	 * @return true if the url is valid, otherwise false.
	 */
	private Notification checkUrl(String urlInputValue, NotificationType type) {

		Notification notification = null;

		if (urlInputValue != null && !urlInputValue.isEmpty() && !NetTools.isValidURL(urlInputValue)) {
			notification = NotificationFactory.getNew(type, RscTools.getString(RscConst.ERR_LINKWIDGET_URL_NOTVALID));
		}

		return notification;
	}

	/**
	 * Check the file change.
	 *
	 * @param text the text
	 * @param type the notification type
	 * @return true if the file is valid, otherwise false.
	 */
	private Notification checkFile(String text, NotificationType type) {

		// Get tree results
		if (!StringUtils.isBlank(text)) {
			IFile iFile = null;
			try {
				iFile = WorkspaceTools.getFileInWorkspaceForPath(new Path(text));
			} catch (Exception e) {
				return NotificationFactory.getNew(type, RscTools.getString(RscConst.ERR_LINKWIDGET_FILE_NOTFILE));
			}

			if (iFile == null) {
				return NotificationFactory.getNew(type, RscTools.getString(RscConst.ERR_LINKWIDGET_FILE_NOTFILE));
			}

			IPath location = iFile.getLocation();
			if (location != null) {
				File file = location.toFile();
				if (!file.isFile()) {
					return NotificationFactory.getNew(type, RscTools.getString(RscConst.ERR_LINKWIDGET_FILE_NOTFILE));
				}
			} else {
				return NotificationFactory.getNew(type, RscTools.getString(RscConst.ERR_LINKWIDGET_FILE_NOTFILE));
			}
		}

		return null;
	}

	/**
	 * Lock path.
	 *
	 * @param lock the lock
	 */
	public void lockPath(boolean lock) {
		if (super.isEditable()) {
			fileBtn.setEnabled(!lock);
			urlBtn.setEnabled(!lock);
			fileChooser.setEnabled(!lock);
			urlInput.setEnabled(!lock);
		}
	}

	/**
	 * @param e the SelectionEvent to cast
	 * @return a new Event with the SelectionEvent fields
	 */
	private Event toEvent(SelectionEvent e) {
		Event newEvent = new Event();

		if (e != null) {
			newEvent.widget = e.widget;
			newEvent.x = e.x;
			newEvent.y = e.y;
			newEvent.width = e.width;
			newEvent.height = e.height;
			newEvent.data = e.data;
			newEvent.display = e.display;
		}

		return newEvent;
	}

	/**
	 * The Link changed listener
	 * 
	 * @author Didier Verstraete
	 *
	 */
	public interface LinkChangedListener {
		/**
		 * The link changed method
		 * 
		 * @param event the event triggered
		 */
		public void linkChanged(Event event);
	}

	@Override
	@SuppressWarnings("unchecked")
	public LinkWidget getControl() {
		return this;
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		if (urlInput != null)
			urlInput.addKeyListener(listener);
	}
}
