/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.nebula.widgets.richtext.RichTextEditorConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import gov.sandia.cf.application.IGenericParameterApplication;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.ISelectValue;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.NewFileTreeSelectionDialog;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.tools.CursorTools;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.tools.ImageTools;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.widgets.TextWidget.TextWidgetType;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The form components factory
 * 
 * @author Didier Verstraete
 *
 */
public class FormFactory {
	/**
	 * Column name property to set parameter id
	 */
	public static final String COLUMN_ID_PROPERTY = "COLUMN_ID"; //$NON-NLS-1$

	private static final int LABEL_HEIGHT_HINT = 28;

	/**
	 * Do not instantiate
	 */
	private FormFactory() {
	}

	/**
	 * @param parent the parent composite
	 * @param text   the text to display
	 * @return a new form label
	 */
	public static CLabel createFormLabel(Composite parent, String text) {
		CLabel formLabel = new CLabel(parent, SWT.ON_TOP);
		formLabel.setLayoutData(new GridData(SWT.LEFT, SWT.ON_TOP, false, false));
		formLabel.setTopMargin(8);
		formLabel.setText(text);
		return formLabel;
	}

	/**
	 * @param parent the parent composite
	 * @param text   the text to display
	 * @return a new label
	 */
	public static Label createLabel(Composite parent, String text) {
		GridData dataLabel = new GridData();
		dataLabel.heightHint = LABEL_HEIGHT_HINT;
		dataLabel.horizontalAlignment = GridData.FILL;

		return createLabel(parent, text, dataLabel, SWT.NONE);
	}

	/**
	 * @param parent the parent composite
	 * @param text   the text to display
	 * @param style  the style
	 * @return a new label
	 */
	public static Label createLabel(Composite parent, String text, int style) {
		GridData dataLabel = new GridData();
		dataLabel.heightHint = LABEL_HEIGHT_HINT;
		dataLabel.horizontalAlignment = GridData.FILL;

		return createLabel(parent, text, dataLabel, style);
	}

	/**
	 * @param parent the parent composite
	 * @return a new form label separator
	 */
	public static Label createLabelHorizontalSeparator(Composite parent) {

		// Create a horizontal separator
		Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData dataAdequacySeparator = new GridData();
		dataAdequacySeparator.horizontalSpan = 2;
		dataAdequacySeparator.grabExcessHorizontalSpace = true;
		dataAdequacySeparator.horizontalAlignment = GridData.FILL;
		separator.setLayoutData(dataAdequacySeparator);

		return separator;
	}

	/**
	 * @param parent     the parent composite
	 * @param text       the text to display
	 * @param layoutData the layout data
	 * @return a new label
	 */
	public static Label createLabel(Composite parent, String text, Object layoutData) {
		return createLabel(parent, text, layoutData, SWT.NONE);
	}

	/**
	 * @param parent     the parent composite
	 * @param text       the text to display
	 * @param layoutData the layout data
	 * @param style      the style
	 * @return a new label
	 */
	public static Label createLabel(Composite parent, String text, Object layoutData, int style) {
		// label
		Label label = new Label(parent, SWT.NONE & style);
		label.setLayoutData(layoutData);
		label.setText(text + RscTools.COLON);
		label.setBackground(parent.getBackground());

		return label;
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param parent the parent composite
	 * @param text   the text to display
	 * @return a new label with title font
	 */
	public static Label createLabelTitle(ResourceManager rscMgr, Composite parent, String text) {
		Label label = createLabel(parent, text);
		FontTools.setTitleFont(rscMgr, label);
		return label;
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param parent the parent composite
	 * @param text   the text to display
	 * @return a new label with subtitle font
	 */
	public static Label createLabelSubtitle(ResourceManager rscMgr, Composite parent, String text) {
		Label label = createLabel(parent, text);
		FontTools.setSubtitleFont(rscMgr, label);
		return label;
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param parent the parent composite
	 * @param text   the text to display
	 * @return a new label with important font
	 */
	public static Label createLabelImportant(ResourceManager rscMgr, Composite parent, String text) {
		Label label = createLabel(parent, text);
		FontTools.setImportantTextFont(rscMgr, label);
		return label;
	}

	/**
	 * @param rscMgr        the resource manager used to manage the resources
	 *                      (fonts, colors, images, cursors...)
	 * @param parent        the parent composite
	 * @param layoutData    the layout data
	 * @param buttonOptions the button options
	 * @return a new button
	 */
	public static ButtonTheme createButton(ResourceManager rscMgr, Composite parent, Object layoutData,
			Map<String, Object> buttonOptions) {

		// button
		ButtonTheme button = new ButtonTheme(rscMgr, parent, SWT.CENTER, buttonOptions);
		button.setLayoutData(new GridData());
		button.setData(layoutData);

		return button;
	}

	/**
	 * @param rscMgr    the resource manager used to manage the resources (fonts,
	 *                  colors, images, cursors...)
	 * @param parent    the parent composite
	 * @param data      the data to attach to the button
	 * @param text      the text to display
	 * @param iconName  the icon name
	 * @param iconSize  the icon size
	 * @param colorName the button color
	 * @return a new button
	 */
	public static ButtonTheme createButton(ResourceManager rscMgr, Composite parent, Object data, String text,
			String iconName, int iconSize, String colorName) {

		// button
		Map<String, Object> buttonOptions = new HashMap<>();
		if (text != null) {
			buttonOptions.put(ButtonTheme.OPTION_TEXT, text);
		}
		buttonOptions.put(ButtonTheme.OPTION_OUTLINE, false);
		if (iconName != null) {
			buttonOptions.put(ButtonTheme.OPTION_ICON, iconName);
			buttonOptions.put(ButtonTheme.OPTION_ICON_SIZE, iconSize);
		}
		if (colorName != null) {
			buttonOptions.put(ButtonTheme.OPTION_COLOR, colorName);
		}
		return createButton(rscMgr, parent, data, buttonOptions);
	}

	/**
	 * @param parent the parent composite
	 * @param label  the text to display
	 * @return a new button
	 */
	public static Button createCheckbox(Composite parent, String label) {
		return createCheckbox(parent, new GridLayout(), label);
	}

	/**
	 * @param parent     the parent composite
	 * @param layoutData the layout data
	 * @param label      the text to display
	 * @return a new button
	 */
	public static Button createCheckbox(Composite parent, Object layoutData, String label) {

		// button
		Button button = new Button(parent, SWT.CHECK);
		button.setText(label);
		button.setLayoutData(layoutData);
		button.setData(layoutData);

		return button;
	}

	/**
	 * @param parent the parent composite
	 * @param id     the component id
	 * @param values the list values
	 * @return a new combobox correctly filled
	 */
	public static ComboViewer createCombo(Composite parent, Object id, List<Object> values) {
		return createCombo(parent, id, values, new LabelProvider());
	}

	/**
	 * @param parent        the parent composite
	 * @param id            the component id
	 * @param values        the list values
	 * @param labelProvider the label provider used to display the list values
	 * @return a new combobox correctly filled
	 */
	public static ComboViewer createCombo(Composite parent, Object id, List<?> values, LabelProvider labelProvider) {
		ComboViewer combobox = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;
		gridDataCombo.horizontalAlignment = GridData.FILL;
		combobox.getCombo().setLayoutData(gridDataCombo);
		combobox.setContentProvider(new ArrayContentProvider());
		combobox.setLabelProvider(labelProvider);
		combobox.setInput(values);
		combobox.getCombo().addKeyListener(new ComboDropDownKeyListener());
		combobox.setData(COLUMN_ID_PROPERTY, id);
		combobox.getCombo().setData(COLUMN_ID_PROPERTY, id);

		// disable mouse wheel change
		combobox.getCombo().addListener(SWT.MouseVerticalWheel, event -> event.doit = false);

		return combobox;
	}

	/**
	 * @param parent the parent composite
	 * @param id     the component id
	 * @return a new text viewer correctly filled
	 */
	public static Text createText(Composite parent, Object id) {
		return createText(parent, id, SWT.LEFT | SWT.SINGLE | SWT.WRAP | SWT.BORDER);
	}

	/**
	 * @param parent the parent composite
	 * @param id     the component id
	 * @param style  the swt style
	 * @return a new text viewer correctly filled
	 */
	public static Text createText(Composite parent, Object id, int style) {

		Text text = new Text(parent, style);
		GridData dataComments = new GridData();
		dataComments.grabExcessHorizontalSpace = true;
		dataComments.horizontalAlignment = GridData.FILL;
		dataComments.verticalAlignment = GridData.CENTER;
		text.setLayoutData(dataComments);
		text.setData(FormFactory.COLUMN_ID_PROPERTY, id);

		return text;
	}

	/**
	 * @param parent the parent composite
	 * @param value  the value to display
	 * @return a new label correctly filled
	 */
	public static Label createNonEditableText(Composite parent, String value) {

		Label text = new Label(parent, SWT.LEFT | SWT.WRAP | SWT.V_SCROLL);
		GridData dataComments = new GridData();
		dataComments.heightHint = LABEL_HEIGHT_HINT;
		dataComments.grabExcessHorizontalSpace = true;
		dataComments.horizontalAlignment = GridData.FILL;
		text.setLayoutData(dataComments);
		text.setText(value != null ? value : RscTools.empty());

		return text;
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param parent the parent composite
	 * @param value  the value to display
	 * @return a new link correctly filled
	 */
	public static Label createLink(ResourceManager rscMgr, Composite parent, String value) {

		Label link = new Label(parent, SWT.WRAP);
		GridData dataComments = new GridData();
		dataComments.heightHint = link.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		dataComments.grabExcessHorizontalSpace = true;
		dataComments.horizontalAlignment = GridData.FILL;
		link.setLayoutData(dataComments);
		link.setText(value);
		link.setEnabled(true);
		link.setForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLUE));
		CursorTools.setCursor(rscMgr, link, SWT.CURSOR_HAND);
		link.addListener(SWT.MouseDown, event -> link.notifyListeners(SWT.Selection, event));

		return link;
	}

	/**
	 * @param parent the parent composite
	 * @param id     the component id
	 * @return a new rich text editor correctly filled
	 */
	public static RichTextEditor createRichText(Composite parent, Object id) {

		// configure richtext editor
		RichTextEditorConfiguration editConfig = new RichTextEditorConfiguration();
		editConfig.setToolbarCollapsible(true);
		editConfig.setOption(RichTextEditorConfiguration.TOOLBAR_GROUPS, PartsResourceConstants.RICH_EDITOR_TOOLBAR);

		// create richText
		RichTextEditor richtext = new RichTextEditor(parent, editConfig, SWT.BORDER);
		GridData dataComments = new GridData();
		dataComments.minimumHeight = PartsResourceConstants.RICHTEXTEDITOR_MINHEIGHT;
		dataComments.heightHint = PartsResourceConstants.RICHTEXTEDITOR_MINHEIGHT;
		dataComments.grabExcessHorizontalSpace = true;
		dataComments.horizontalAlignment = GridData.FILL;
		richtext.setLayoutData(dataComments);
		richtext.setData(FormFactory.COLUMN_ID_PROPERTY, id);

		return richtext;
	}

	/**
	 * @param rscMgr    the resource manager used to manage the resources (fonts,
	 *                  colors, images, cursors...)
	 * @param parent    the parent composite
	 * @param labelText the richtext label
	 * @param id        the component id
	 * @param expanded  is expanded by default
	 * @return a new rich text editor correctly filled
	 */
	public static RichTextWidget createRichTextCollapsible(ResourceManager rscMgr, Composite parent, String labelText,
			Object id, boolean expanded) {

		RichTextWidget richtextWidget = new RichTextWidget(rscMgr, parent, SWT.NONE, labelText, expanded);
		richtextWidget.setData(FormFactory.COLUMN_ID_PROPERTY, id);
		richtextWidget.setBackground(parent.getBackground());

		return richtextWidget;
	}

	/**
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param parent   the parent composite
	 * @param expanded is expanded?
	 * @param editable is editable?
	 * @return a new rich text editor correctly filled
	 */
	public static RichTextWidget createRichTextWidget(ResourceManager rscMgr, Composite parent, boolean expanded,
			boolean editable) {
		return createRichTextWidget(rscMgr, parent, RscTools.getString(RscConst.MSG_RICHTEXT_CLICK_BAR_DEFAULT), null,
				expanded, editable);
	}

	/**
	 * @param rscMgr    the resource manager used to manage the resources (fonts,
	 *                  colors, images, cursors...)
	 * @param parent    the parent composite
	 * @param labelText the header label text
	 * @param id        the id data to associate
	 * @param expanded  is expanded?
	 * @param editable  is editable?
	 * @return a new rich text editor correctly filled and fitted
	 */
	public static RichTextWidget createRichTextWidget(ResourceManager rscMgr, Composite parent, String labelText,
			Object id, boolean expanded, boolean editable) {

		RichTextWidget richtextWidget = new RichTextWidget(rscMgr, parent, SWT.NONE, labelText, expanded, editable);
		richtextWidget.setData(FormFactory.COLUMN_ID_PROPERTY, id);
		richtextWidget.setBackground(parent.getBackground());

		return richtextWidget;
	}

	/**
	 * @param parent the parent composite
	 * @param value  the value to display
	 * @return a new browser correctly filled
	 */
	public static Browser createNonEditableRichText(Composite parent, String value) {

		Browser text = new Browser(parent, SWT.LEFT | SWT.WRAP);
		GridData dataComments = new GridData();
		dataComments.grabExcessHorizontalSpace = true;
		dataComments.horizontalAlignment = GridData.FILL;
		dataComments.heightHint = PartsResourceConstants.DIALOG_TXT_INPUT_HEIGHT * 15;
		text.setLayoutData(dataComments);
		String header = "<!DOCTYPE html>\r\n<html>\r\n<head>\r\n" + //$NON-NLS-1$
				"<style>\r\n" + //$NON-NLS-1$
				"body {overflow: scroll;}\r\n" + //$NON-NLS-1$
				"</style>\r\n" + //$NON-NLS-1$
				"</head>\r\n" + //$NON-NLS-1$
				"<body>"; //$NON-NLS-1$
		String footer = "</body>\r\n</html>"; //$NON-NLS-1$
		text.setText(header + value + footer);
		text.setFocus();
		text.setBackground(parent.getBackground());

		return text;
	}

	/**
	 * @param parent the parent composite
	 * @return a new sash form
	 */
	public static SashForm createVerticalSash(Composite parent) {

		// Create the SashForm with VERTICAL
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sashForm.setSashWidth(5);
		sashForm.setBackground(sashForm.getDisplay().getSystemColor(SWT.COLOR_GRAY));

		return sashForm;
	}

	/**
	 * @param parent      the parent composite
	 * @param viewManager the view manager
	 * @param id          the component id
	 * @param editable    is editable?
	 * @return a link widget selector correctly filled
	 */
	public static LinkWidget createLinkWidget(Composite parent, IViewManager viewManager, Object id, boolean editable) {

		LinkWidget linkWidget = new LinkWidget(parent, viewManager, SWT.NONE, editable);
		linkWidget.setData(FormFactory.COLUMN_ID_PROPERTY, id);
		linkWidget.setBackground(parent.getBackground());

		return linkWidget;
	}

	/**
	 * @param viewManager the view manager to request database
	 * @param parent      the parent composite
	 * @param id          the component id
	 * @param editable    is editable?
	 * @return a new pcmm element widget selector correctly filled
	 */
	public static PCMMElementSelectorWidget createCredibilityElementSelectorWidget(IViewManager viewManager,
			Composite parent, Object id, boolean editable) {

		return new PCMMElementSelectorWidget(viewManager, parent, SWT.NONE, editable, id);
	}

	/**
	 * @param viewManager the view manager to request database
	 * @param parent      the parent composite
	 * @param id          the component id
	 * @param editable    is editable?
	 * @return a new system requirement widget selector correctly filled
	 */
	public static SysRequirementSelectorWidget createSysRequirementSelectorWidget(IViewManager viewManager,
			Composite parent, Object id, boolean editable) {

		SysRequirementSelectorWidget widget = new SysRequirementSelectorWidget(viewManager, parent, SWT.NONE, editable);
		widget.setData(FormFactory.COLUMN_ID_PROPERTY, id);

		return widget;
	}

	/**
	 * @param <T>      the value class
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param parent   the parent composite
	 * @param editable is editable?
	 * @param id       the field id
	 * @return a new editable select widget
	 */
	public static <T extends ISelectValue> SelectWidget<T> createSelectWidget(ResourceManager rscMgr, Composite parent,
			boolean editable, String id) {
		return new SelectWidget<>(rscMgr, parent, SWT.NONE, editable, id);
	}

	/**
	 * @param <T>          the value class
	 * @param rscMgr       the resource manager used to manage the resources (fonts,
	 *                     colors, images, cursors...)
	 * @param parent       the parent composite
	 * @param editable     is editable?
	 * @param id           the field id
	 * @param selectValues the values
	 * @return a new editable select widget
	 */
	public static <T extends ISelectValue> SelectWidget<T> createSelectWidget(ResourceManager rscMgr, Composite parent,
			boolean editable, String id, List<T> selectValues) {
		return new SelectWidget<>(rscMgr, parent, SWT.NONE, editable, id, selectValues);
	}

	/**
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param parent   the parent composite
	 * @param editable is editable?
	 * @param id       the field id
	 * @return a new editable text widget
	 */
	public static TextWidget createTextWidget(ResourceManager rscMgr, Composite parent, boolean editable, String id) {
		return new TextWidget(rscMgr, parent, SWT.NONE, editable, id);
	}

	/**
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param parent   the parent composite
	 * @param editable is editable?
	 * @param type     the text widget type (string, integer...)
	 * @param id       the field id
	 * @return a new editable text widget
	 */
	public static TextWidget createTextWidget(ResourceManager rscMgr, Composite parent, boolean editable,
			TextWidgetType type, Object id) {
		return new TextWidget(rscMgr, parent, SWT.NONE, editable, type, id);
	}

	/**
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param parent   the parent composite
	 * @param editable is editable?
	 * @param type     the text widget type (string, integer...)
	 * @param id       the field id
	 * @param style    the style
	 * @return a new editable text widget
	 */
	public static TextWidget createTextWidget(ResourceManager rscMgr, Composite parent, boolean editable,
			TextWidgetType type, Object id, int style) {
		return new TextWidget(rscMgr, parent, style, editable, type, id);
	}

	/**
	 * @param <S>         the select value object type
	 * @param viewManager the view manager to request database
	 * @param parent      the parent composite
	 * @param type        the field type
	 * @param id          the field id to set
	 * @param name        the field name
	 * @return a new editable form field
	 */
	public static <S extends GenericParameterSelectValue<?>> FormFieldWidget<S> createEditableFormFieldWidget(
			IViewManager viewManager, Composite parent, FormFieldType type, String id, String name) {
		return new FormFieldWidget<>(viewManager, parent, type, true, id, name);
	}

	/**
	 * @param <S>         the select value object type
	 * @param viewManager the view manager to request database
	 * @param parent      the parent composite
	 * @param type        the field type
	 * @param id          the field id to set
	 * @param name        the field name
	 * @return a new non editable form field
	 */
	public static <S extends GenericParameterSelectValue<?>> FormFieldWidget<S> createNonEditableFormFieldWidget(
			IViewManager viewManager, Composite parent, FormFieldType type, String id, String name) {
		return new FormFieldWidget<>(viewManager, parent, type, false, id, name);
	}

	/**
	 * @param <P>         the generic parameter class
	 * @param viewManager the view manager to request database
	 * @param parent      the parent composite
	 * @param parameter   the generic parameter
	 * @return a new generic editable value field
	 */
	public static <P extends GenericParameter<P>> GenericValueFieldWidget<P> createEditableGenericValueWidget(
			IViewManager viewManager, Composite parent, P parameter) {

		// label
		renderGenericValueFieldLabel(viewManager, parent, parameter);

		return new GenericValueFieldWidget<>(viewManager, parent, parameter, true);
	}

	/**
	 * @param <P>         the generic parameter class
	 * @param viewManager the view manager to request database
	 * @param parent      the parent composite
	 * @param parameter   the generic parameter
	 * @return a new generic non editable value field
	 */
	public static <P extends GenericParameter<P>> GenericValueFieldWidget<P> createNonEditableGenericValueWidget(
			IViewManager viewManager, Composite parent, P parameter) {

		// label
		renderGenericValueFieldLabel(viewManager, parent, parameter);

		return new GenericValueFieldWidget<>(viewManager, parent, parameter, false);
	}

	/**
	 * Render generic parameter label
	 *
	 * @param <P>         the generic parameter type
	 * 
	 * @param viewManager the view manager to request database
	 * @param parent      the parent composite
	 * @param parameter   the generic parameter
	 */
	private static <P extends GenericParameter<P>> void renderGenericValueFieldLabel(IViewManager viewManager,
			Composite parent, P parameter) {

		if (parent != null && parameter != null && viewManager != null) {

			// the label name
			String labelName = viewManager.getAppManager().getService(IGenericParameterApplication.class)
					.getParameterNameWithRequiredPrefix(parameter);

			// label
			Label createLabel = FormFactory.createLabel(parent, labelName);
			if (FormFieldType.RICH_TEXT.getType().equals(parameter.getType())) {
				((GridData) createLabel.getLayoutData()).verticalAlignment = SWT.TOP;
			}
		}
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @return a new question image scaled 16x16
	 */
	public static Image getQuestionIcon(ResourceManager rscMgr) {
		return ImageTools.getImage(rscMgr,
				Display.getCurrent().getSystemImage(SWT.ICON_QUESTION).getImageData().scaledTo(16, 16));
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @return a new info image scaled 16x16
	 */
	public static Image getInfoIcon(ResourceManager rscMgr) {
		return ImageTools.getImage(rscMgr,
				Display.getCurrent().getSystemImage(SWT.ICON_INFORMATION).getImageData().scaledTo(16, 16));
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @return a new warning image scaled 16x16
	 */
	public static Image getWarningIcon(ResourceManager rscMgr) {
		return ImageTools.getImage(rscMgr,
				Display.getCurrent().getSystemImage(SWT.ICON_WARNING).getImageData().scaledTo(16, 16));
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @return a new error image scaled 16x16
	 */
	public static Image getErrorIcon(ResourceManager rscMgr) {
		return ImageTools.getImage(rscMgr,
				Display.getCurrent().getSystemImage(SWT.ICON_ERROR).getImageData().scaledTo(16, 16));
	}

	/**
	 * @param notification the notification
	 * @return the notification color associated
	 */
	public static Color getNotificationColor(Notification notification) {
		if (notification == null) {
			return null;
		}

		if (NotificationType.INFO.equals(notification.getType())) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLUE);
		} else if (NotificationType.WARN.equals(notification.getType())) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_ORANGE);
		} else if (NotificationType.ERROR.equals(notification.getType())) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_RED);
		}

		return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK);
	}

	/**
	 * @param rscMgr       the resource manager used to manage the resources (fonts,
	 *                     colors, images, cursors...)
	 * @param notification the notification
	 * @return the notification icon associated
	 */
	public static Image getNotificationIcon(ResourceManager rscMgr, Notification notification) {
		if (notification == null) {
			return null;
		}

		if (NotificationType.INFO.equals(notification.getType())) {
			return FormFactory.getInfoIcon(rscMgr);
		} else if (NotificationType.WARN.equals(notification.getType())) {
			return FormFactory.getWarningIcon(rscMgr);
		} else if (NotificationType.ERROR.equals(notification.getType())) {
			return FormFactory.getErrorIcon(rscMgr);
		}

		return null;
	}

	/**
	 * @return a new resource tree dialog
	 */
	public static ElementTreeSelectionDialog getResourceTreeDialog() {
		return new ElementTreeSelectionDialog(Display.getCurrent().getActiveShell(), new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @return a new resource tree dialog
	 */
	public static NewFileTreeSelectionDialog getNewResourceTreeDialog(ResourceManager rscMgr) {
		return new NewFileTreeSelectionDialog(rscMgr, Display.getCurrent().getActiveShell(),
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
	}

	/**
	 * @param rscMgr       the resource manager used to manage the resources (fonts,
	 *                     colors, images, cursors...)
	 * @param parent       the parent composite
	 * @param notification the notification to display
	 * @return a notification label with the associated icon and color
	 */
	public static CLabel getNotificationLabel(ResourceManager rscMgr, Composite parent, Notification notification) {
		CLabel textARGVersion = new CLabel(parent, SWT.NONE);
		textARGVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		textARGVersion.setBackground(parent.getBackground());
		if (notification.getMessages() != null)
			textARGVersion.setText(String.join(RscTools.CARRIAGE_RETURN, notification.getMessages()));
		textARGVersion.setForeground(getNotificationColor(notification));
		textARGVersion.setImage(getNotificationIcon(rscMgr, notification));
		return textARGVersion;
	}
}
