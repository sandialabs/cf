/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.tools.RscTools;

/**
 * @author Didier Verstraete
 *
 */
public class PIRTQueryResultTableLabelProvider extends ColumnLabelProvider
		implements ITableColorProvider, ITableFontProvider, ITableLabelProvider {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTQueryResultTableLabelProvider.class);

	/**
	 * the result table
	 */
	private TableViewer tableResult;

	/**
	 * Constructor
	 * 
	 * @param tableResult the result table
	 */
	public PIRTQueryResultTableLabelProvider(TableViewer tableResult) {
		this.tableResult = tableResult;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element != null) {
			String fieldName = (String) tableResult.getColumnProperties()[columnIndex];
			String columnText = RscTools.empty();

			for (Field field : element.getClass().getDeclaredFields()) {

				// ignore static fields
				if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getName().equals(fieldName)) {
					PropertyDescriptor pdEntityUpdated;
					try {
						pdEntityUpdated = new PropertyDescriptor(field.getName(), element.getClass());
						Object columnTextObject = pdEntityUpdated.getReadMethod().invoke(element);
						if (columnTextObject != null) {
							columnText = columnTextObject.toString();
						}
					} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}

			return columnText;
		}
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		return null;
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return null;
	}

	@Override
	public Font getFont(Object element, int columnIndex) {
		return null;
	}

}
