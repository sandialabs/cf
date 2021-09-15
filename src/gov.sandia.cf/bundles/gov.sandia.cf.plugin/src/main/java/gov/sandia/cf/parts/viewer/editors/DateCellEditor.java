/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;

/**
 * A date time cell editor for tables and trees.
 * 
 * @author Didier Verstraete
 *
 */
public class DateCellEditor extends CellEditor {

	protected DateTime date;

	/**
	 * Constructor with default style to calendar.
	 * 
	 * @param parent the parent composite for the control
	 */
	public DateCellEditor(Composite parent) {
		this(parent, SWT.CALENDAR);
	}

	/**
	 * Constructor
	 * 
	 * @param parent the parent composite for the control
	 * @param style  the style
	 */
	public DateCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createControl(Composite parent) {
		date = new DateTime(parent, getStyle());
		date.addTraverseListener(e -> {
			if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
				e.doit = false;
			}
		});
		date.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				DateCellEditor.this.focusLost();
			}
		});
		date.setFont(parent.getFont());
		date.setBackground(parent.getBackground());
		return date;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doGetValue() {
		Calendar cal = Calendar.getInstance();
		cal.set(date.getYear(), date.getMonth(), date.getDay(), 0, 0);
		return cal.getTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetFocus() {
		if (date != null) {
			date.setFocus();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetValue(Object value) {
		Assert.isTrue(date != null && (value instanceof Date));
		Date dateValue = (Date) value;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateValue);
		date.setYear(cal.get(Calendar.YEAR));
		date.setMonth(cal.get(Calendar.MONTH));
		date.setDay(cal.get(Calendar.DAY_OF_MONTH));
	}
}
