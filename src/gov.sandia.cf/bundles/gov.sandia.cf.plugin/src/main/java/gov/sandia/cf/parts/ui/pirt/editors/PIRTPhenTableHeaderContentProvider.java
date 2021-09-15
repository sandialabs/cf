/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.QoIHeader;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.constants.TableHeaderBarButtonType;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.parts.model.QoIHeaderParts;
import gov.sandia.cf.parts.ui.pirt.PIRTPhenomenaView;
import gov.sandia.cf.parts.ui.pirt.dialogs.PIRTQoIDescriptionDialog;
import gov.sandia.cf.parts.ui.pirt.dialogs.PIRTQoITagDescriptionDialog;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * Provides the content of the PIRT table
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTPhenTableHeaderContentProvider implements IStructuredContentProvider {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTPhenTableHeaderContentProvider.class);

	/**
	 * Parent view
	 */
	private PIRTPhenomenaView parent;

	/**
	 * Constructor
	 * @param parent the parent view
	 */
	public PIRTPhenTableHeaderContentProvider(PIRTPhenomenaView parent) {
		this.parent = parent;
	}

	@Override
	public Object[] getElements(Object inputElement) {

		List<QoIHeaderParts> data = new ArrayList<>();

		if (inputElement instanceof QuantityOfInterest) {

			QuantityOfInterest qoi = (QuantityOfInterest) inputElement;

			/**
			 * fixed columns of QoI
			 */
			// Name
			QoIHeaderParts nameHeader = new QoIHeaderParts();
			nameHeader.setName(PIRTPhenTableHeaderDescriptor.getRowNameLabel());
			nameHeader.setValue(qoi.getSymbol());
			nameHeader.setQoi(qoi);
			data.add(nameHeader);

			// Description
			QoIHeaderParts descriptionHeader = new QoIHeaderParts();
			descriptionHeader.setName(PIRTPhenTableHeaderDescriptor.getRowDescriptionLabel());
			descriptionHeader.setValue(StringTools.clearHtml(qoi.getDescription(), true));
			descriptionHeader.setQoi(qoi);
			descriptionHeader.setBtnType(
					(null != qoi.getTagDate()) ? TableHeaderBarButtonType.VIEW : TableHeaderBarButtonType.EDIT);
			descriptionHeader.setBtnListener(event -> {
				// open description dialog
				PIRTQoIDescriptionDialog qoiDescriptionDialog = new PIRTQoIDescriptionDialog(parent.getViewManager(),
						parent.getShell(), (null != qoi.getTagDate()) ? DialogMode.VIEW : DialogMode.UPDATE);
				// update qoi
				updateQoI(qoiDescriptionDialog.openDialog(qoi));
			});
			data.add(descriptionHeader);

			// Creation Date
			QoIHeaderParts creationDateHeader = new QoIHeaderParts();
			creationDateHeader.setName(PIRTPhenTableHeaderDescriptor.getRowCreationDateLabel());
			creationDateHeader.setValue(DateTools.formatDate(qoi.getCreationDate()));
			creationDateHeader.setQoi(qoi);
			data.add(creationDateHeader);

			if (null != qoi.getTagDate()) {
				// Is Tagged
				QoIHeaderParts isTaggedHeader = new QoIHeaderParts();
				isTaggedHeader.setName(PIRTPhenTableHeaderDescriptor.getRowIsTaggedLabel());
				isTaggedHeader.setValue(qoi.getTagDate() != null ? RscTools.getString(RscConst.MSG_YES)
						: RscTools.getString(RscConst.MSG_NO));
				isTaggedHeader.setQoi(qoi);
				data.add(isTaggedHeader);

				// Tag Date
				QoIHeaderParts tagDateHeader = new QoIHeaderParts();
				tagDateHeader.setName(PIRTPhenTableHeaderDescriptor.getRowTagDateLabel());
				tagDateHeader.setValue(DateTools.formatDate(qoi.getTagDate()));
				tagDateHeader.setQoi(qoi);
				data.add(tagDateHeader);

				// Tag Description
				QoIHeaderParts tagDescriptionHeader = new QoIHeaderParts();
				tagDescriptionHeader.setName(PIRTPhenTableHeaderDescriptor.getRowTagDescriptionLabel());
				tagDescriptionHeader.setValue(StringTools.clearHtml(qoi.getTagDescription(), true));
				tagDescriptionHeader.setQoi(qoi);
				data.add(tagDescriptionHeader);
				tagDescriptionHeader.setBtnType(TableHeaderBarButtonType.VIEW);
				tagDescriptionHeader.setBtnListener(event -> {
					// open tag description dialog
					PIRTQoITagDescriptionDialog qoiDescriptionDialog = new PIRTQoITagDescriptionDialog(
							parent.getViewManager(), parent.getShell());
					// update qoi
					updateQoI(qoiDescriptionDialog.openDialog(qoi));
				});
			}

			/**
			 * variable qoi header list
			 */
			if (qoi.getQoiHeaderList() != null) {
				for (QoIHeader qoiHeader : qoi.getQoiHeaderList()) {
					QoIHeaderParts qoiHeaderParts = new QoIHeaderParts(qoiHeader.getName(), qoiHeader.getValue(), qoi);
					qoiHeaderParts.setQoiHeader(qoiHeader);
					data.add(qoiHeaderParts);
				}
			}
		}

		return data.toArray();
	}

	/**
	 * Update the qoi in database
	 * 
	 * @param qoiModified the qoi to update
	 */
	private void updateQoI(QuantityOfInterest qoiModified) {
		try {
			if (qoiModified != null) {
				parent.getViewManager().getAppManager().getService(IPIRTApplication.class).updateQoI(qoiModified,
						parent.getViewManager().getCache().getUser());

				// Refresh
				parent.refresh();

				// fire view change to save credibility file
				parent.getViewManager().viewChanged();
			}
		} catch (CredibilityException e) {
			logger.error("An error occured while updating QoI: {}\n{}", qoiModified, e.getMessage(), e); //$NON-NLS-1$
		}
	}

}
