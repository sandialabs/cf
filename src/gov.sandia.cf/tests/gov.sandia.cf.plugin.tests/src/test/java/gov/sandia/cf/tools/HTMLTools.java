/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.util.List;
import java.util.Objects;

import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.IGenericTableItem;
import gov.sandia.cf.model.PCMMPlanningParam;

/**
 * HTML Tools class
 * 
 * @author Didier Verstraete
 *
 */
public class HTMLTools {

	/**
	 * Translate PCMM Planning field to html
	 * 
	 * @param value the value to display
	 * @return the generic field value to html string
	 */
	public static String genericFieldToHTML(GenericValue<?, ?> value) {

		StringBuilder str = new StringBuilder();

		if (value != null && value.getParameter() != null) {

			// label
			str.append("<h3>" + value.getParameter().getName() + "</h3>"); //$NON-NLS-1$ //$NON-NLS-2$

			if (FormFieldType.SELECT.getType().equals(value.getParameter().getType())
					|| FormFieldType.TEXT.getType().equals(value.getParameter().getType())
					|| FormFieldType.RICH_TEXT.getType().equals(value.getParameter().getType())) {

				// text comments
				str.append(value.getValue());

			}
		}
		return str.toString();
	}

	/**
	 * Translate PCMM Planning Table field to html
	 * 
	 * @param field the field to display
	 * @param items the items to display
	 * @return the generic field value to html table string
	 */
	public static String genericFieldToHTMLTable(GenericParameter<?> field, List<IGenericTableItem> items) {

		StringBuilder str = new StringBuilder();

		if (field instanceof PCMMPlanningParam && field.getChildren() != null && !field.getChildren().isEmpty()) {

			// table header
			str.append("<table style='border: 1px solid black; border-collapse: collapse; width: 100%;'>"); //$NON-NLS-1$
			str.append("<thead style='background: #043865; color: white'><tr>"); //$NON-NLS-1$
			field.getChildren().stream().filter(Objects::nonNull).filter(f -> f instanceof PCMMPlanningParam)
					.map(PCMMPlanningParam.class::cast)
					.forEach(f -> str.append("<th style='border: 1px solid black;'>" + f.getName() + "</th>")); //$NON-NLS-1$ //$NON-NLS-2$
			str.append("</tr></thead>"); //$NON-NLS-1$

			// translate data
			str.append("<tbody>"); //$NON-NLS-1$
			if (items != null) {
				items.stream().filter(Objects::nonNull).forEach(item -> {
					if (item.getValueList() != null) {
						str.append("<tr>"); //$NON-NLS-1$
						item.getValueList().stream().forEach(value -> str
								.append("<td style='border: 1px solid black;'>" + value.getValue() + "</td>")); //$NON-NLS-1$ //$NON-NLS-2$
						str.append("</tr>"); //$NON-NLS-1$
					}
				});
			}
			str.append("</tbody>"); //$NON-NLS-1$
			str.append("</table>"); //$NON-NLS-1$
		}
		return str.toString();
	}
}
