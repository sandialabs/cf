/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.comparator.TagComparatorByDateTag;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.ui.IViewController;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The PCMM linear Progression Bar composite
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMTagPart extends Composite {

	/**
	 * The PCMM elements
	 */
	private List<Tag> tagList;

	/**
	 * The action listener
	 */
	private ITagAction actionListener;

	/**
	 * The selected tag
	 */
	private Tag selectedTag;

	/**
	 * The tag button
	 */
	private ButtonTheme btnNewTag;

	/**
	 * The tag combo
	 */
	private ComboViewer tagCombo;

	/**
	 * The view manager
	 */
	private IViewController viewController;

	/**
	 * The constructor
	 * 
	 * @param viewController the view controller
	 * @param parent         the parent view
	 * @param actionListener the action listener
	 * @param tagList        the tag list
	 * @param selectedTag    the selected tag
	 * @param style          the style
	 */
	public PCMMTagPart(IViewController viewController, Composite parent, ITagAction actionListener, List<Tag> tagList,
			Tag selectedTag, int style) {
		super(parent, style);

		Assert.isNotNull(viewController);
		this.viewController = viewController;
		this.tagList = new ArrayList<>();
		this.actionListener = actionListener;
		this.selectedTag = selectedTag;

		// create part
		createPart(tagList);
	}

	/**
	 * Creates the part components
	 * 
	 * @param tagList the tag list
	 */
	private void createPart(List<Tag> tagList) {

		// properties
		int compositeMinWidth = 120;

		GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, true, 1, 1);
		gridData.minimumWidth = compositeMinWidth;
		this.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(1, false);
		this.setLayout(gridLayout);

		// composite combo
		Composite comboComposite = new Composite(this, SWT.NONE);
		comboComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
		comboComposite.setLayout(new GridLayout(2, false));

		// label content
		Label label = new Label(comboComposite, SWT.NONE);
		label.setText(RscTools.getString(RscConst.MSG_TAG_PART_COMBO_LABEL));
		label.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, false, false, 1, 1));

		// combo-box Level
		tagCombo = new ComboViewer(comboComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData comboGridData = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1);
		comboGridData.horizontalAlignment = SWT.FILL;
		tagCombo.getCombo().setLayoutData(comboGridData);
		tagCombo.setContentProvider(new ArrayContentProvider());

		tagCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (((Tag) element).getId() == null) {
					return RscTools.getString(RscConst.MSG_TAG_PART_COMBO_DEFAULTTAG);
				} else {
					String tagLabelPattern = "{0} ({1})"; //$NON-NLS-1$
					return MessageFormat.format(tagLabelPattern, ((Tag) element).getName(),
							DateTools.formatDate(((Tag) element).getDateTag(), DateTools.getDateTimeFormat()));
				}
			}
		});
		setTagList(tagList);
		tagCombo.getCombo().addKeyListener(new ComboDropDownKeyListener());
		tagCombo.getCombo().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tagCombo.getSelection() != null) {

					// retrieve selected tag
					selectedTag = (Tag) ((IStructuredSelection) tagCombo.getSelection()).getFirstElement();
					if (selectedTag != null && selectedTag.getId() == null) {
						selectedTag = null;
					}

					// enable/disable tag button
					btnNewTag.setEnabled(isTagSelected());

					// fire tag selection changed
					actionListener.tagSelectionChanged(selectedTag);
				}
			}
		});

		// composite buttons
		Composite buttonComposite = new Composite(this, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
		buttonComposite.setLayout(new RowLayout());

		// Button - New Tag - Create
		Map<String, Object> btnNewTagOptions = new HashMap<>();
		btnNewTagOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_TAG_PART_BTN_NEWTAG));
		btnNewTagOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnNewTagOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_TAG);
		btnNewTagOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BROWN);
		btnNewTagOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) e -> actionListener.tagCurrentPCMM());
		btnNewTag = new ButtonTheme(viewController.getViewManager().getRscMgr(), buttonComposite, SWT.CENTER,
				btnNewTagOptions);
		btnNewTag.setToolTipText(RscTools.getString(RscConst.MSG_TAG_PART_BTN_NEWTAG_TOOLTIP));
		btnNewTag.setEnabled(isTagSelected());

		// Button - Manage Tag - Create
		Map<String, Object> btnManageTagOptions = new HashMap<>();
		btnManageTagOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_TAG_PART_BTN_MANAGETAG));
		btnManageTagOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnManageTagOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_CONFIG);
		btnManageTagOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnManageTagOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) e -> actionListener.manageTags());
		new ButtonTheme(viewController.getViewManager().getRscMgr(), buttonComposite, SWT.CENTER, btnManageTagOptions);

		// Set min height for the progress composite
		Point computedSize = this.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		gridData.minimumHeight = computedSize.y;
	}

	/**
	 * Set the tag list
	 * 
	 * @param tagList the tag list
	 */
	public void setTagList(List<Tag> tagList) {

		this.tagList = new ArrayList<>();

		// add the first empty element to select the non-tagged state
		Tag noTag = new Tag();
		this.tagList.add(noTag);

		if (tagList != null) {

			// sort tag list by date tag
			tagList.sort(new TagComparatorByDateTag());
			Collections.reverse(tagList);
			this.tagList.addAll(tagList);
		}

		tagCombo.setInput(this.tagList);
		if (selectedTag == null || selectedTag.getId() == null) {
			tagCombo.setSelection(new StructuredSelection(noTag));
			selectedTag = noTag;
		} else {
			tagCombo.setSelection(new StructuredSelection(selectedTag));
		}
		tagCombo.refresh();
		this.requestLayout();
	}

	/**
	 * @return the selected tag
	 */
	public Tag getSelectedTag() {
		return selectedTag;
	}

	/**
	 * @return true if the selected tag is null
	 */
	public boolean isTagSelected() {
		return selectedTag == null || selectedTag.getId() == null;
	}
}
