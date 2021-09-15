/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.application.IPCMMPlanningApplication;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.GenericValueTaggable;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.listeners.ExpandBarListener;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.ExpandBarTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.viewer.TableFactory;
import gov.sandia.cf.parts.viewer.TreeViewerID;
import gov.sandia.cf.parts.viewer.editors.AGenericTableCellEditor;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.ColumnViewerSupport;
import gov.sandia.cf.parts.viewer.editors.GenericParameterTreeContentProvider;
import gov.sandia.cf.parts.viewer.editors.GenericTableLabelProvider;
import gov.sandia.cf.parts.widgets.FancyToolTipSupport;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.RichTextWidget;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The PCMM Planning View
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningView extends ACredibilityPCMMView {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMPlanningView.class);

	/**
	 * Controller
	 */
	private PCMMPlanningViewController viewCtrl;

	/**
	 * the pcmm element
	 */
	private PCMMElement elementSelected;
	/**
	 * the pcmm subelements expanded by the user
	 */
	private Set<PCMMSubelement> expandedSubelements;
	/**
	 * the pcmm elements
	 */
	private List<PCMMElement> elements;
	/**
	 * the pcmm planning parameters
	 */
	private List<PCMMPlanningParam> planningParameters;
	/**
	 * the pcmm planning questions
	 */
	private List<PCMMPlanningQuestion> planningQuestions;

	/**
	 * The aggregate table composite
	 */
	private Composite compositeWidget;

	/**
	 * The expand items map
	 */
	private Map<IAssessable, ExpandItem> expandItems;

	private static final String DATA_SUBELEMENT = "SUBELEMENT"; //$NON-NLS-1$

	private static final String TREE_ACTION_COLUMNS = "TREE_ACTION_COLUMNS"; //$NON-NLS-1$

	/**
	 * The constructor
	 * 
	 * @param parentView      the parent view
	 * @param elementSelected the element selected
	 * @param style           the style
	 */
	public PCMMPlanningView(PCMMViewManager parentView, PCMMElement elementSelected, int style) {
		super(parentView, parentView, style);
		this.elementSelected = elementSelected;

		this.viewCtrl = new PCMMPlanningViewController(this);

		// lists and maps instantiation
		expandedSubelements = new HashSet<>();
		elements = new ArrayList<>();
		expandItems = new HashMap<>();

		// create the view
		renderPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_PCMMPLANNING_TITLE, RscTools.getString(RscConst.MSG_TITLE_EMPTY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_PCMMPLANNING_ITEM_TITLE);
	}

	/**
	 * Creates the phenomena view and all its components
	 * 
	 * @param parent
	 * 
	 */
	private void renderPage() {

		logger.debug("Creating PCMM view components"); //$NON-NLS-1$

		// Reset title
		setTitle(RscTools.getString(RscConst.MSG_PCMMPLANNING_TITLE,
				this.elementSelected != null ? this.elementSelected.getName() : RscTools.empty()));

		// Render main table
		renderMainWidgetComposite();

		// Render footer
		renderFooter();

		// load view datas
		loadDatas();
	}

	/**
	 * Render main table
	 */
	private void renderMainWidgetComposite() {

		// Grid Layout
		compositeWidget = new Composite(this, SWT.FILL | SWT.BORDER);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		compositeWidget.setLayoutData(gridData);
		compositeWidget.setLayout(gridLayout);
		compositeWidget.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));
	}

	/**
	 * Refresh the main table
	 */
	private void refreshMainWidget() {

		ViewTools.disposeChildren(compositeWidget);

		expandItems.clear();

		// render the main table
		renderMainWidget();

		// relayout the table composite
		compositeWidget.layout();
	}

	/**
	 * Render main widget
	 */
	private void renderMainWidget() {

		if (elementSelected != null) {
			if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {

				// scroll container
				ScrolledComposite scrollContainer = new ScrolledComposite(compositeWidget, SWT.V_SCROLL);
				GridData scrollScData = new GridData(SWT.FILL, SWT.FILL, true, true);
				scrollContainer.setLayoutData(scrollScData);
				scrollContainer.setLayout(new GridLayout());

				// item content
				Composite content = new Composite(scrollContainer, SWT.NONE);
				GridData gridDataContent = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
				content.setLayoutData(gridDataContent);
				content.setLayout(new GridLayout());
				content.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREY_LIGHT));

				// create content
				createPlanningContent(content, elementSelected);

				// set scroll container size
				scrollContainer.setContent(content);
				scrollContainer.setExpandHorizontal(true);
				scrollContainer.setExpandVertical(true);

				// autoresize sub containers
				scrollContainer.addListener(SWT.Resize, event -> {
					int width = scrollContainer.getClientArea().width;
					Point computeSize = content.computeSize(width, SWT.DEFAULT);
					scrollContainer.setMinSize(computeSize);
					content.setSize(computeSize);
				});
				content.addListener(SWT.Resize, e -> {
					int width = scrollContainer.getClientArea().width;
					Point computeSize = content.computeSize(width, SWT.DEFAULT);
					scrollContainer.setMinSize(computeSize);
					content.setSize(computeSize);
				});

			} else if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())
					&& elementSelected.getSubElementList() != null) {

				// Sub Expand bar
				boolean withBorder = false;
				boolean grabVerticalSpace = true;
				ExpandBar subBarHeader = ExpandBarTheme.createExpandBar(compositeWidget, withBorder, grabVerticalSpace,
						SWT.V_SCROLL | SWT.FILL);
				FontTools.setBoldFont(getViewManager().getRscMgr(), subBarHeader);

				// create an expand bar item for each subelement
				int index = 0;
				for (PCMMSubelement subelement : elementSelected.getSubElementList().stream().filter(Objects::nonNull)
						.sorted(Comparator.comparing(PCMMSubelement::getCode)).collect(Collectors.toList())) {

					// item content
					Composite subItemContent = new Composite(subBarHeader, SWT.NONE);
					GridData gridDataSubItemContent = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					subItemContent.setLayoutData(gridDataSubItemContent);
					subItemContent.setLayout(new GridLayout());
					subItemContent.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREY_LIGHT));

					// Expand bar item - Title
					ExpandItem subItem = new ExpandItem(subBarHeader, SWT.FILL, index);
					index++;
					subItem.setText(
							RscTools.getString(RscConst.MSG_PCMM_CODENAME, subelement.getCode(), subelement.getName()));
					subItem.setData(DATA_SUBELEMENT, subelement);

					// Expand bar item - Title resize
					subItem.setHeight(subItemContent.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
					subItem.setControl(subItemContent);
				}

				// layout current view depending of the bar state (collapsed/expanded)
				subBarHeader.addExpandListener(new ExpandBarListener(compositeWidget, subBarHeader, false) {
					@Override
					public void itemExpanded(ExpandEvent event) {
						super.itemExpanded(event);
						Display.getCurrent().asyncExec(() -> createSubItemContent((ExpandItem) event.item));
						PCMMSubelement subelement = (PCMMSubelement) ((ExpandItem) event.item).getData(DATA_SUBELEMENT);
						expandedSubelements.add(subelement);
					}

					@Override
					public void itemCollapsed(ExpandEvent event) {
						super.itemCollapsed(event);
						PCMMSubelement subelement = (PCMMSubelement) ((ExpandItem) event.item).getData(DATA_SUBELEMENT);
						expandedSubelements.remove(subelement);
					}
				});

				// expand items if they were expanded by the user before reloading
				for (ExpandItem subItem : subBarHeader.getItems()) {
					boolean expanded = expandedSubelements.contains(subItem.getData(DATA_SUBELEMENT));
					subItem.setExpanded(expanded);
					if (expanded) {
						Display.getCurrent().asyncExec(() -> createSubItemContent(subItem));
					}
				}

			}
		}
	}

	/**
	 * Create the subitem content
	 * 
	 * @param item the expand bar item to construct
	 */
	private void createSubItemContent(ExpandItem item) {
		if (item.getControl() instanceof Composite && item.getData(DATA_SUBELEMENT) instanceof PCMMSubelement
				&& !expandItems.containsKey(item.getData(DATA_SUBELEMENT))) {

			Composite subItemComposite = (Composite) item.getControl();
			PCMMSubelement subelement = (PCMMSubelement) item.getData(DATA_SUBELEMENT);
			expandItems.put(subelement, item);

			// Create planning content
			createPlanningContent(subItemComposite, subelement);

			// redraw item and parent composite
			int itemHeight = subItemComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			item.setHeight(itemHeight);
			subItemComposite.layout();

			// add resize listener to redraw the composite
			subItemComposite.addListener(SWT.Resize,
					event -> item.setHeight(subItemComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y));
		}
	}

	/**
	 * Render the planning item content
	 * 
	 * @param parent     the parent composite
	 * @param assessable the pcmm element or subelement concerned
	 */
	private void createPlanningContent(Composite parent, IAssessable assessable) {

		// render questions
		renderPlanningQuestions(parent, assessable);

		// render fields
		renderPlanningFields(parent, assessable);
	}

	/**
	 * Render the planning item questions
	 * 
	 * @param parent     the parent composite
	 * @param assessable the pcmm element or subelement concerned
	 */
	private void renderPlanningQuestions(Composite parent, IAssessable assessable) {

		// render questions
		if (planningQuestions != null && parent != null && !parent.isDisposed() && assessable != null) {

			// render content
			if (isFromCurrentElement(assessable) && !getViewManager().isTagMode()) {
				if (PCMMMode.SIMPLIFIED.equals(getViewManager().getCache().getPCMMSpecification().getMode())) {
					planningQuestions.stream().filter(q -> assessable.equals(q.getElement()))
							.forEach(question -> renderEditableField(parent, question, assessable));
				} else if (PCMMMode.DEFAULT.equals(getViewManager().getCache().getPCMMSpecification().getMode())) {
					planningQuestions.stream().filter(q -> assessable.equals(q.getSubelement()))
							.forEach(question -> renderEditableField(parent, question, assessable));
				}
			} else {
				if (PCMMMode.SIMPLIFIED.equals(getViewManager().getCache().getPCMMSpecification().getMode())) {
					planningQuestions.stream().filter(q -> assessable.equals(q.getElement()))
							.forEach(question -> renderNonEditableField(parent, question, assessable));
				} else if (PCMMMode.DEFAULT.equals(getViewManager().getCache().getPCMMSpecification().getMode())) {
					planningQuestions.stream().filter(q -> assessable.equals(q.getSubelement()))
							.forEach(question -> renderNonEditableField(parent, question, assessable));
				}
			}
		}
	}

	/**
	 * Render the planning item fields.
	 * 
	 * @param parent     the parent composite
	 * @param assessable the pcmm element or subelement concerned
	 */
	private void renderPlanningFields(Composite parent, IAssessable assessable) {

		// render parameters
		if (planningParameters != null && parent != null && !parent.isDisposed()) {

			// render content
			if (isFromCurrentElement(assessable) && !getViewManager().isTagMode()) {
				planningParameters.stream().forEach(field -> renderEditableField(parent, field, assessable));
			} else {
				planningParameters.stream().forEach(field -> renderNonEditableField(parent, field, assessable));
			}
		}
	}

	/**
	 * Render Non Editable field.
	 * 
	 * @param parent     the parent composite
	 * @param column     the field to display
	 * @param assessable the pcmm element or subelement concerned
	 */
	private void renderNonEditableField(Composite parent, GenericParameter<?> field, IAssessable assessable) {
		if (field != null) {
			if (FormFieldType.TEXT.getType().equals(field.getType())) {

				// content
				Composite content = new Composite(parent, SWT.NONE);
				content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				content.setLayout(new GridLayout());

				// label
				FormFactory.createLabel(content, field.getName());

				// value
				Label text = FormFactory.createNonEditableText(content,
						viewCtrl.getPlanningValueAsText(field, assessable));
				text.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));

			} else if (FormFieldType.SELECT.getType().equals(field.getType())) {

				// content
				Composite content = new Composite(parent, SWT.NONE);
				content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				content.setLayout(new GridLayout());

				// label
				FormFactory.createLabel(content, field.getName());

				// get combo value if it exists and set it
				GenericValue<?, ?> planningValue = viewCtrl.getPlanningValue(field, assessable);
				String stringValue = null;
				if (planningValue != null && planningValue.getValue() != null && !planningValue.getValue().isEmpty()
						&& planningValue.getParameter() != null
						&& planningValue.getParameter().getParameterValueList() != null) {
					stringValue = planningValue.getParameter().getParameterValueList().stream().filter(Objects::nonNull)
							.filter(s -> s.getId() != null && planningValue.getValue().equals(s.getId().toString()))
							.findFirst().map(GenericParameterSelectValue::getName).orElse(RscTools.empty());
				}

				// value
				Label text = FormFactory.createNonEditableText(content, stringValue);
				text.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));

			} else if (FormFieldType.RICH_TEXT.getType().equals(field.getType())) {

				// text comments
				RichTextWidget richText = FormFactory.createRichTextCollapsible(getViewManager().getRscMgr(), parent,
						field.getName(), field, false);
				richText.setValue(viewCtrl.getPlanningValueAsText(field, assessable));
				richText.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREY_LIGHT_1));
				richText.setEnabled(false);

			} else if (field instanceof PCMMPlanningParam && field.getChildren() != null
					&& !field.getChildren().isEmpty()) {

				renderPlanningTypeTable(parent, field, assessable);
			}
		}
	}

	/**
	 * Render editable field.
	 * 
	 * @param parent     the parent composite
	 * @param column     the field to display
	 * @param assessable the pcmm element or subelement concerned
	 */
	private void renderEditableField(Composite parent, GenericParameter<?> field, IAssessable assessable) {
		if (field != null) {
			if (FormFieldType.TEXT.getType().equals(field.getType())) {

				renderEditableFieldText(parent, field, assessable);

			} else if (FormFieldType.RICH_TEXT.getType().equals(field.getType())) {

				renderEditableFieldRichText(parent, field, assessable);

			} else if (FormFieldType.SELECT.getType().equals(field.getType())) {

				renderEditableFieldSelect(parent, field, assessable);

			} else if (field instanceof PCMMPlanningParam && field.getChildren() != null
					&& !field.getChildren().isEmpty()) {

				renderPlanningTypeTable(parent, field, assessable);
			}
		}
	}

	/**
	 * Render Text field
	 * 
	 * @param parent     the parent composite
	 * @param column     the field to display
	 * @param assessable the pcmm element or subelement concerned
	 */
	private void renderEditableFieldText(Composite parent, GenericParameter<?> field, IAssessable assessable) {

		// content
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		content.setLayout(new GridLayout());

		// label
		FormFactory.createLabel(content, field.getName());

		// text
		Text text = FormFactory.createText(content, viewCtrl.getPlanningValue(field, assessable));
		text.setText(viewCtrl.getPlanningValueAsText(field, assessable));
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				viewCtrl.changeParameterValue(field, assessable, text.getText());
			}
		});
	}

	/**
	 * Render RichText field
	 * 
	 * @param parent     the parent composite
	 * @param column     the field to display
	 * @param assessable the pcmm element or subelement concerned
	 */
	private void renderEditableFieldRichText(Composite parent, GenericParameter<?> field, IAssessable assessable) {

		// richtext content collapsible
		RichTextWidget richText = FormFactory.createRichTextCollapsible(getViewManager().getRscMgr(), parent,
				field.getName(), field, false);
		richText.setValue(viewCtrl.getPlanningValueAsText(field, assessable));
		richText.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREY_LIGHT_1));
		richText.getRichtext().addModifyListener(
				event -> viewCtrl.changeParameterValue(field, assessable, richText.getRichtext().getText()));
		richText.getRichtext().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				viewCtrl.changeParameterValue(field, assessable, richText.getRichtext().getText());
			}
		});
		richText.addExpandListener(new ExpandListener() {

			@Override
			public void itemExpanded(ExpandEvent e) {
				resize();
			}

			@Override
			public void itemCollapsed(ExpandEvent e) {
				resize();
			}

			private void resize() {
				parent.setSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				parent.requestLayout();
			}
		});
	}

	/**
	 * Render Select field
	 * 
	 * @param parent     the parent composite
	 * @param column     the field to display
	 * @param assessable the pcmm element or subelement concerned
	 */
	private void renderEditableFieldSelect(Composite parent, GenericParameter<?> field, IAssessable assessable) {

		// content
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		content.setLayout(new GridLayout());

		// label
		Label label = FormFactory.createLabel(content, field.getName());
		label.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREY_LIGHT_1));

		// Combo-box
		ComboViewer combo = FormFactory.createCombo(content, field.getName(), field.getParameterValueList(),
				new LabelProvider() {

					@Override
					public String getText(Object element) {
						return ((GenericParameterSelectValue<?>) element).getName();
					}
				});

		// get combo value if it exists and set it
		GenericValue<?, ?> planningValue = viewCtrl.getPlanningValue(field, assessable);
		GenericParameterSelectValue<?> genericParameterSelectValue = null;
		if (planningValue != null && planningValue.getValue() != null && !planningValue.getValue().isEmpty()
				&& planningValue.getParameter() != null
				&& planningValue.getParameter().getParameterValueList() != null) {
			genericParameterSelectValue = planningValue.getParameter().getParameterValueList().stream()
					.filter(Objects::nonNull)
					.filter(s -> s.getId() != null && planningValue.getValue().equals(s.getId().toString())).findFirst()
					.orElse(null);
		}

		if (genericParameterSelectValue != null) {
			final ISelection slValue = new StructuredSelection(genericParameterSelectValue);
			combo.setSelection(slValue);
		}

		// add change listener to save changes
		combo.addSelectionChangedListener(event -> {
			IStructuredSelection structuredSelection = (IStructuredSelection) combo.getSelection();
			Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof PCMMPlanningSelectValue
					&& ((PCMMPlanningSelectValue) firstElement).getId() != null) {
				viewCtrl.changeParameterValue(field, assessable,
						((PCMMPlanningSelectValue) firstElement).getId().toString());
			}
		});
	}

	/**
	 * Render pcmm planning table
	 * 
	 * @param parent       the parent composite
	 * @param planningType the planning type
	 * @param assessable   the pcmm element or subelement concerned
	 */
	private void renderPlanningTypeTable(Composite parent, GenericParameter<?> planningType, IAssessable assessable) {

		if (planningType instanceof PCMMPlanningParam && planningType.getChildren() != null) {

			boolean enabled = isFromCurrentElement(assessable) && !getViewManager().isTagMode();

			// main composite
			Composite mainComposite = new Composite(parent, SWT.NONE);
			GridData gdContent = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
			mainComposite.setLayoutData(gdContent);
			GridLayout gridLayout = new GridLayout();
			gridLayout.marginWidth = 0;
			mainComposite.setLayout(gridLayout);
			mainComposite.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREY_LIGHT_1));

			// header composite
			Composite headerComposite = new Composite(mainComposite, SWT.NONE);
			GridData gdheaderComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
			headerComposite.setLayoutData(gdheaderComposite);
			GridLayout gridLayoutHeaderComposite = new GridLayout(2, false);
			headerComposite.setLayout(gridLayoutHeaderComposite);

			// label
			Label label = FormFactory.createLabel(headerComposite, planningType.getName());
			((GridData) label.getLayoutData()).grabExcessHorizontalSpace = true;

			// Button - Add Table parameter
			Map<String, Object> btnAddParameter = new HashMap<>();
			btnAddParameter.put(ButtonTheme.OPTION_TEXT,
					RscTools.getString(RscConst.MSG_PCMMPLANNING_TABLE_BTN_ADDPARAMETER, planningType.getName()));
			btnAddParameter.put(ButtonTheme.OPTION_OUTLINE, true);
			btnAddParameter.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_ADD);
			btnAddParameter.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
			btnAddParameter.put(ButtonTheme.OPTION_ENABLED, false);
			ButtonTheme addButton = FormFactory.createButton(getViewManager().getRscMgr(), headerComposite, null,
					btnAddParameter);

			// Initialize table
			TreeViewer treeViewer = renderTableInit(mainComposite);

			// Initialize data
			List<String> columnProperties = new ArrayList<>();

			for (PCMMPlanningParam field : planningType.getChildren().stream().filter(Objects::nonNull)
					.filter(PCMMPlanningParam.class::isInstance).map(PCMMPlanningParam.class::cast)
					.collect(Collectors.toList())) {
				TreeViewerColumn treeCol = renderPlanningFieldsColumn(treeViewer, field, enabled);
				if (treeCol != null) {
					columnProperties.add(treeCol.getColumn().getText());
				}
			}

			if (enabled) {
				// Columns - Actions
				renderMainTableActionColumns(treeViewer, columnProperties);

				// Add button listener
				addButton.setEnabled(true);
				addButton.addListener(SWT.Selection, event -> viewCtrl.addPlanningTableItem(treeViewer,
						(PCMMPlanningParam) planningType, assessable));

				ColumnViewerSupport.enableDoubleClickEditing(treeViewer);
			}

			// Add listeners
			renderMainTableAddEvents(treeViewer);

			// Tree - Properties
			treeViewer.setColumnProperties(columnProperties.stream().toArray(String[]::new));
			treeViewer.setContentProvider(new GenericParameterTreeContentProvider());

			// Set input
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(GenericValue.Filter.PARAMETER, planningType);
			filters.put(GenericValueTaggable.Filter.TAG, getViewManager().getSelectedTag());
			if (assessable instanceof PCMMElement) {
				filters.put(PCMMPlanningTableItem.Filter.ELEMENT, assessable);
			} else if (assessable instanceof PCMMSubelement) {
				filters.put(PCMMPlanningTableItem.Filter.SUBELEMENT, assessable);
			}
			List<PCMMPlanningTableItem> items = getViewManager().getAppManager()
					.getService(IPCMMPlanningApplication.class).getPlanningTableItemBy(filters);
			treeViewer.setInput(items);

			// Refresh
			treeViewer.refresh(true);
		}
	}

	/**
	 * Initialize pcmm planning table
	 * 
	 * @param parent the parent composite
	 * 
	 * @return the tree viewer created
	 */
	private TreeViewer renderTableInit(Composite parent) {

		// Tree - Create
		TreeViewerID treeViewer = new TreeViewerID(parent,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		treeViewer.getTree().setLayoutData(gdViewer);
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);

		FancyToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE);

		// Tree - Layout
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(treeViewer);
		treeViewer.setLayout(viewerLayout);

		// Tree - Hint
		gdViewer.minimumHeight = 150;
		gdViewer.heightHint = treeViewer.getTree().getItemHeight();
		gdViewer.widthHint = getViewManager().getSize().x - 2 * ((GridLayout) parent.getLayout()).horizontalSpacing;

		// Tree - Customize
		treeViewer.getTree().setHeaderBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY));
		treeViewer.getTree().setHeaderForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));

		return treeViewer;
	}

	/**
	 * Add pcmm planning field column to tree viewer
	 * 
	 * @param parent       the parent composite
	 * @param planningType the planning type
	 * @param enabled      the column is editable or not
	 * 
	 * @return the tree viewer column created
	 */
	private TreeViewerColumn renderPlanningFieldsColumn(TreeViewer treeViewer, PCMMPlanningParam field,
			boolean enabled) {

		// Initialize
		TreeViewerColumn tempColumn = TableFactory.createGenericParamTreeColumn(field, treeViewer,
				new GenericTableLabelProvider(field, getViewManager()));

		// Cell editor depending of column type
		if (enabled) {
			AGenericTableCellEditor cellEditor = TableFactory.createGenericParamTableCellEditor(field, treeViewer,
					this);
			if (cellEditor != null) {
				cellEditor.addValueChangedListener(viewCtrl::changePlanningTableValue);
				tempColumn.setEditingSupport(cellEditor);
			}
		}

		return tempColumn;
	}

	/**
	 * Add action column
	 * 
	 * @param columnProperties
	 */
	@SuppressWarnings("unchecked")
	private void renderMainTableActionColumns(TreeViewer treeViewer, List<String> columnProperties) {

		// Get Tree and layout
		Tree tree = treeViewer.getTree();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) tree.getLayout();
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_EDIT));

		// create editors data
		treeViewer.setData(TREE_ACTION_COLUMNS, new HashMap<>());

		// Tree - Column - Action delete
		TreeViewerColumn actionDeleteColumn = new TreeViewerColumn(treeViewer, SWT.CENTER);
		actionDeleteColumn.getColumn().setText(RscTools.getString(RscConst.MSG_BTN_DELETE));
		((Map<TreeViewerColumn, Map<TreeItem, TreeEditor>>) treeViewer.getData(TREE_ACTION_COLUMNS))
				.put(actionDeleteColumn, new HashMap<>());
		actionDeleteColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TreeItem item = (TreeItem) cell.getItem();
				Object element = cell.getElement();

				// Delete Editor
				TreeEditor editor = null;

				if (!((Map<TreeViewerColumn, Map<TreeItem, TreeEditor>>) treeViewer.getData(TREE_ACTION_COLUMNS))
						.get(actionDeleteColumn).containsKey(item)) {

					// Button
					ButtonTheme btnDeleteItem = TableFactory
							.createDeleteButtonColumnAction(getViewManager().getRscMgr(), cell);
					btnDeleteItem.addListener(SWT.Selection,
							event -> viewCtrl.deletePlanningTableItem(treeViewer, element));

					// Draw cell
					editor = new TreeEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnDeleteItem, item, cell.getColumnIndex());

					((Map<TreeViewerColumn, Map<TreeItem, TreeEditor>>) treeViewer.getData(TREE_ACTION_COLUMNS))
							.get(actionDeleteColumn).put(item, editor);
				}

			}
		});
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_BTN_DELETE));
	}

	/**
	 * Add tree viewer events
	 */
	private void renderMainTableAddEvents(TreeViewer treeViewer) {
		// Get tree
		Tree tree = treeViewer.getTree();

		tree.addListener(SWT.MeasureItem, new Listener() {
			private TreeItem previousItem = null;

			@Override
			public void handleEvent(Event event) {
				event.height = PartsResourceConstants.TABLE_ROW_HEIGHT;
				TreeItem item = (TreeItem) event.item;
				if (item != null && !item.equals(previousItem)) {
					previousItem = item;
					refreshTableItemActionButtons(treeViewer);
				}
			}
		});

		// disable selection when clicking on the container
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				treeViewer.setSelection(new StructuredSelection());
			}
		});
	}

	/**
	 * Render footer
	 */
	private void renderFooter() {
		// Footer buttons - Composite
		Composite compositeButtonsFooter = new Composite(this, SWT.FILL);
		GridLayout gridLayoutButtonsHeader = new GridLayout(2, true);
		compositeButtonsFooter.setLayout(gridLayoutButtonsHeader);
		compositeButtonsFooter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		// Composite for Footer left buttons
		Composite compositeButtonsFooterLeft = new Composite(compositeButtonsFooter, SWT.NONE);
		compositeButtonsFooterLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsFooterLeft.setLayout(new RowLayout());

		// Composite for header right buttons
		Composite compositeButtonsFooterRight = new Composite(compositeButtonsFooter, SWT.NONE);
		compositeButtonsFooterRight.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsFooterRight.setLayout(new RowLayout());

		// Button Back - Create
		Map<String, Object> btnBackOptions = new HashMap<>();
		btnBackOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BACK));
		btnBackOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnBackOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_BACK);
		btnBackOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnBackOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> getViewManager().openLastView());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER, btnBackOptions);

		// Footer buttons - Help - Create
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> HelpTools.openContextualHelp());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER, btnHelpOptions);
		HelpTools.addContextualHelp(compositeButtonsFooter, ContextualHelpId.PCMM_PLANNING_ITEM);

		// layout view
		compositeButtonsFooter.layout();
	}

	/**
	 * Loads view datas from database.
	 */
	private void loadDatas() {
		setPcmmElement(elementSelected);
	}

	/**
	 * Refreshes the view
	 */
	public void refreshViewer() {

		// reset the header controls
		setTitle(RscTools.getString(RscConst.MSG_PCMMPLANNING_TITLE,
				this.elementSelected != null ? this.elementSelected.getName() : RscTools.empty()));

		// layout view
		this.layout();
	}

	/**
	 * Reloads view
	 */
	@Override
	public void reload() {
		// Trigger GuidanceLevel View
		getViewManager().getCredibilityEditor().setPartProperty(
				CredibilityFrameworkConstants.PART_PROPERTY_ACTIVEVIEW_PCMM_SELECTED_ASSESSABLE,
				elementSelected.getAbbreviation());

		// Show role selection
		showRoleSelection();

		// Get Model
		Model model = getViewManager().getCache().getModel();
		if (model != null) {
			try {
				/**
				 * Load pcmm elements from database
				 */
				elements = getViewManager().getAppManager().getService(IPCMMApplication.class).getElementList(model);

				/**
				 * Load pcmm planning parameters from database
				 */
				Map<EntityFilter, Object> filters = new HashMap<>();
				filters.put(GenericParameter.Filter.MODEL, model);
				filters.put(GenericParameter.Filter.PARENT, null);
				planningParameters = getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
						.getPlanningFieldsBy(filters);

				/**
				 * Load pcmm planning questions from database
				 */
				planningQuestions = getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
						.getPlanningQuestionsByElement(elementSelected,
								getViewManager().getPCMMConfiguration().getMode());
				if (elements != null) {

					/**
					 * Refresh the table
					 */
					refreshMainWidget();

					// expand and select the selected element in the viewer input
					if (elementSelected != null && expandItems.containsKey(elementSelected)) {
						expandItems.get(elementSelected).setExpanded(true);
					}
				}
			} catch (CredibilityException e) {
				MessageDialog.openWarning(getShell(), RscTools.getString(RscConst.MSG_PCMMPLANNING_DIALOG_TITLE),
						RscTools.getString(RscConst.ERR_PCMMPLANNING_DIALOG_LOADING_MSG));
				logger.warn("An error has occurred while loading the planning data:\n{}", e.getMessage(), e); //$NON-NLS-1$
			}
		}

		// refresh the viewer
		refreshViewer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void roleChanged() {
		refresh();
	}

	/**
	 * @return the pcmm element
	 */
	public PCMMElement getPcmmElement() {
		return elementSelected;
	}

	/**
	 * Resets the pcmm element selected
	 * 
	 * @param pcmmElement the element to set
	 */
	public void setPcmmElement(PCMMElement pcmmElement) {
		this.elementSelected = pcmmElement;

		// Refresh
		refresh();
	}

	/**
	 * Trigger view change to save data
	 */
	public void viewChanged() {
		// Set view changed
		getViewManager().viewChanged();

		// Refresh
		refresh();
	}

	/**
	 * @param assessable the pcmm element or subelement
	 * @return true if the assessable is from currently selected PCMM Element
	 */
	private boolean isFromCurrentElement(IAssessable assessable) {

		PCMMElement elementSelectedTmp = assessable instanceof PCMMSubelement
				? ((PCMMSubelement) assessable).getElement()
				: (PCMMElement) assessable;
		return this.elementSelected != null && this.elementSelected.equals(elementSelectedTmp);
	}

	@SuppressWarnings("unchecked")
	void refreshTableItemActionButtons(TreeViewer treeViewer) {

		if (treeViewer != null && treeViewer.getData(TREE_ACTION_COLUMNS) != null) {

			// delete the action buttons for the treeItem found
			for (Map<TreeItem, TreeEditor> mapActionButtons : ((Map<TreeViewerColumn, Map<TreeItem, TreeEditor>>) treeViewer
					.getData(TREE_ACTION_COLUMNS)).values()) {

				List<TreeItem> toRemove = new ArrayList<>();

				for (Entry<TreeItem, TreeEditor> entry : mapActionButtons.entrySet()) {
					if (!Arrays.asList(treeViewer.getTree().getItems()).contains(entry.getKey())) {
						TreeEditor editor = entry.getValue();
						if (editor != null) {
							if (editor.getEditor() != null && !editor.getEditor().isDisposed()) {
								editor.getEditor().dispose();
							}
							editor.dispose();
						}
						toRemove.add(entry.getKey());
					}
				}

				// remove unused keys from map
				for (TreeItem key : toRemove) {
					mapActionButtons.remove(key);
				}
			}
		}
	}

}
