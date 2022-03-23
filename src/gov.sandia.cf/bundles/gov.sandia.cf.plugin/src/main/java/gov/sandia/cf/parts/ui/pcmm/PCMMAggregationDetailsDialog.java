/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.GenericCFDialog;
import gov.sandia.cf.parts.listeners.ExpandBarListener;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.ExpandBarTheme;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to view the assessment details
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAggregationDetailsDialog extends GenericCFDialog<PCMMViewManager> {

	/**
	 * the assessment list
	 */
	private List<PCMMAssessment> assessmentList;

	/**
	 * The element/sub-element selected
	 */
	protected IAssessable item;

	/**
	 * Use this constructor to update @param subElement
	 * 
	 * @param viewManager    the view manager
	 * @param parentShell    the parent shell
	 * @param assessmentList the assessment list
	 * @param item           the assessable item (PCMM Element/Subelement) to show
	 */
	public PCMMAggregationDetailsDialog(PCMMViewManager viewManager, Shell parentShell,
			List<PCMMAssessment> assessmentList, IAssessable item) {
		super(viewManager, parentShell);
		this.assessmentList = assessmentList;
		this.item = item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Set the new title of the dialog
		newShell.setText(RscTools.getString(RscConst.MSG_PCMM_DIALOG_AGGREG_TITLE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			setTitle((null != ((PCMMSubelement) item).getElement()) ? ((PCMMSubelement) item).getElement().getName()
					: null);
			String message = (((PCMMSubelement) item).getCode() != null ? ((PCMMSubelement) item).getCode()
					: RscTools.HYPHEN) + RscTools.COLON
					+ (((PCMMSubelement) item).getName() != null ? ((PCMMSubelement) item).getName() : RscTools.HYPHEN);
			setMessage(message);
		} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
			setTitle(((PCMMElement) item).getName());
			setMessage(null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		// form container
		if (assessmentList != null) {
			// Background
			int i = 1;

			/**
			 * Expand bar container
			 */
			ExpandBar barHeader = ExpandBarTheme.createExpandBar(container, getViewManager().getRscMgr());
			GridData gridDataExpandBar = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			gridDataExpandBar.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
			gridDataExpandBar.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
			barHeader.setLayoutData(gridDataExpandBar);
			int expandBarItemCount = 0;

			// construct details for each user - role
			for (PCMMAssessment assessment : assessmentList) {
				if (assessment != null) {

					// construct expand bar title
					StringBuilder expandBarTitle = new StringBuilder();
					expandBarTitle
							.append((assessment.getUserCreation() != null ? assessment.getUserCreation().getUserID()
									: RscTools.getString(RscConst.MSG_PCMMAGGREG_DETAILS_EMPTY_USER)));
					expandBarTitle.append(" " + RscTools.HYPHEN + " "); //$NON-NLS-1$ //$NON-NLS-2$
					expandBarTitle.append((assessment.getRoleCreation() != null ? assessment.getRoleCreation().getName()
							: RscTools.getString(RscConst.MSG_PCMMAGGREG_DETAILS_EMPTY_ROLE)));

					// ExpandBar Item
					ExpandItem expandItem = new ExpandItem(barHeader, SWT.FILL, expandBarItemCount);
					expandItem.setExpanded(true);
					expandItem.setText(expandBarTitle.toString());
					expandBarItemCount++;

					// Form container
					Composite formContainer = new Composite(barHeader, SWT.NONE);
					GridData scData = new GridData(SWT.FILL, SWT.FILL, true, true);
					scData.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
					scData.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
					formContainer.setLayoutData(scData);
					GridLayout gridLayout = new GridLayout(3, false);
					formContainer.setLayout(gridLayout);
					gridLayout.verticalSpacing = PartsResourceConstants.DEFAULT_GRIDDATA_V_INDENT;
					Color defaultBg = formContainer.getBackground();

					// Change bg
					Color bg = defaultBg;
					if ((i % 2) == 0) {
						bg = ColorTools.toColor(getViewManager().getRscMgr(),
								ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY_LIGHT));
					}
					i++;

					// Set bg
					formContainer.setBackground(bg);

					// label empty for level
					Label lblEmpty = new Label(formContainer, SWT.LEFT);
					lblEmpty.setBackground(bg);
					lblEmpty.setLayoutData(new GridData());
					lblEmpty.setText(RscTools.empty());

					// Color level
					Label txtLevel = new Label(formContainer, SWT.LEFT);
					GridData dataLvl = new GridData();
					dataLvl.heightHint = PartsResourceConstants.DIALOG_TXT_INPUT_HEIGHT;
					dataLvl.widthHint = PartsResourceConstants.DIALOG_TXT_INPUT_HEIGHT;
					txtLevel.setLayoutData(dataLvl);
					txtLevel.setText(RscTools.empty());
					// set the level color background color
					PCMMSpecification configuration = getViewManager().getPCMMConfiguration();
					if (assessment.getLevel() != null && assessment.getLevel().getCode() != null
							&& configuration != null && configuration.getLevelColors() != null
							&& configuration.getLevelColors().containsKey(assessment.getLevel().getCode())) {
						Color levelColor = new Color(Display.getCurrent(), ColorTools.stringRGBToColor(
								configuration.getLevelColors().get(assessment.getLevel().getCode()).getFixedColor()));
						txtLevel.setBackground(levelColor);
					}

					// text level name
					Text txtLevelName = new Text(formContainer, SWT.LEFT);
					txtLevelName.setBackground(bg);
					GridData dataLvlName = new GridData();
					dataLvlName.grabExcessHorizontalSpace = true;
					dataLvlName.horizontalAlignment = GridData.FILL;
					dataLvlName.heightHint = PartsResourceConstants.DIALOG_TXT_INPUT_HEIGHT;
					txtLevelName.setLayoutData(dataLvlName);
					String levelName = assessment.getLevel() != null && assessment.getLevel().getName() != null
							? assessment.getLevel().getName()
							: RscTools.getString(RscConst.MSG_PCMMAGGREG_DETAILS_EMPTY_NAME);
					txtLevelName.setText(levelName);
					txtLevelName.setEditable(false);

					// label comments
					Label lblComments = new Label(formContainer, SWT.ON_TOP);
					lblComments.setBackground(bg);
					GridData dataCommentsLbl = new GridData();
					dataCommentsLbl.heightHint = PartsResourceConstants.DIALOG_TXT_INPUT_HEIGHT * 2;
					lblComments.setLayoutData(dataCommentsLbl);
					lblComments.setText(RscTools.getString(RscConst.MSG_PCMM_DIALOG_AGGREG_LBL_COMMENTS));

					// text comments
					Browser txtCommentsBrowser = new Browser(formContainer, SWT.LEFT | SWT.WRAP);
					txtCommentsBrowser.setBackground(bg);

					GridData dataComments = new GridData(SWT.FILL, SWT.FILL, true, true);
					dataComments.grabExcessHorizontalSpace = true;
					dataComments.grabExcessVerticalSpace = true;
					dataComments.heightHint = PartsResourceConstants.DIALOG_TXT_INPUT_HEIGHT * 15;
					dataComments.horizontalSpan = 2;
					txtCommentsBrowser.setLayoutData(dataComments);
					String comment = assessment.getComment() != null ? assessment.getComment() : RscTools.empty();
					String header = "<!DOCTYPE html>\r\n<html>\r\n<head>\r\n" + //$NON-NLS-1$
							"<style>\r\n" + //$NON-NLS-1$
							"body {overflow: scroll;}\r\n" + //$NON-NLS-1$
							"</style>\r\n" + //$NON-NLS-1$
							"</head>\r\n" + //$NON-NLS-1$
							"<body>"; //$NON-NLS-1$
					String footer = "</body>\r\n</html>"; //$NON-NLS-1$
					txtCommentsBrowser.setText(header + comment + footer);
					txtCommentsBrowser.setFocus();

					// Set the Composite as the control for the ExpandItem
					expandItem.setControl(formContainer);
					expandItem.setHeight(formContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
				}
			}

			// layout current view depending of the bar state (collapsed/expanded)
			barHeader.addExpandListener(new ExpandBarListener(parent, barHeader, true));
		}

		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		String okButtonName = IDialogConstants.OK_LABEL;
		createButton(parent, IDialogConstants.OK_ID, okButtonName, true);
	}

	/**
	 * open the details
	 */
	public void openDialog() {
		open();
	}

}
