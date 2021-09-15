/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.util.List;
import java.util.Optional;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.ISelectValue;
import gov.sandia.cf.tools.RscTools;

/**
 * The Select widget with helper
 * 
 * @author Didier Verstraete
 *
 * @param <T> the select value entity class
 */
public class SelectWidget<T extends ISelectValue> extends AHelperWidget {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SelectWidget.class);

	private Object id;

	// editable fields
	private ComboViewer selectViewer;
	private List<T> selectValues;

	// non editable fields
	private Label textSelectNonEditable;

	/**
	 * The constructor
	 * 
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param parent   the composite parent
	 * @param style    the style
	 * @param editable is editable?
	 * @param id       the id to set
	 */
	public SelectWidget(ResourceManager rscMgr, Composite parent, int style, boolean editable, Object id) {
		this(rscMgr, parent, style, editable, id, null);
	}

	/**
	 * The constructor
	 * 
	 * @param rscMgr       the resource manager used to manage the resources (fonts,
	 *                     colors, images, cursors...)
	 * @param parent       the composite parent
	 * @param style        the style
	 * @param editable     is editable?
	 * @param id           the id to set
	 * @param selectValues the values for combo field
	 */
	public SelectWidget(ResourceManager rscMgr, Composite parent, int style, boolean editable, Object id,
			List<T> selectValues) {
		super(rscMgr, parent, style, editable);

		// view Manager
		this.id = id;
		this.selectValues = selectValues;

		// create control
		createControl();
	}

	/**
	 * Create the form field content
	 */
	private void createControl() {

		logger.debug("Create form value field widget content"); //$NON-NLS-1$

		// render control
		if (super.isEditable()) {
			renderEditableField();
		} else {
			renderNonEditableField();
		}

		// create helper
		super.createHelper();

		setBackground(getParent().getBackground());
	}

	/**
	 * Render editable field
	 * 
	 * @param parent the parent composite
	 * @param value  the form value to display
	 */
	private void renderEditableField() {
		// Combo-box
		selectViewer = FormFactory.createCombo(this, id, selectValues, new LabelProvider() {
			@Override
			public String getText(Object element) {
				return element != null ? ((ISelectValue) element).getSelectName() : RscTools.empty();
			}
		});
	}

	/**
	 * Render non editable select
	 * 
	 * @param parent the parent composite
	 * @param value  the form value to display
	 */
	private void renderNonEditableField() {
		textSelectNonEditable = FormFactory.createNonEditableText(this, RscTools.empty());
	}

	@Override
	@SuppressWarnings("unchecked")
	public SelectWidget<?> getControl() {
		return this;
	}

	/**
	 * @return the current value
	 */
	public T getValue() {
		if (super.isEditable()) {
			return getEditableValue();
		} else {
			return getNonEditableValue();
		}
	}

	@SuppressWarnings("javadoc")
	public List<T> getSelectValues() {
		return selectValues;
	}

	@SuppressWarnings("javadoc")
	public void setSelectValues(List<T> selectValues) {
		this.selectValues = selectValues;
		if (selectViewer != null) {
			selectViewer.setInput(selectValues);
		}
	}

	/**
	 * @return the editable field value
	 */
	@SuppressWarnings("unchecked")
	private T getEditableValue() {
		// Get selected value
		IStructuredSelection selection = (IStructuredSelection) selectViewer.getSelection();
		if (!selection.isEmpty()) {
			// Get parameter value
			return (T) selection.getFirstElement();
		}
		return null;
	}

	/**
	 * @return the non editable field value
	 */
	private T getNonEditableValue() {

		if (selectValues == null) {
			return null;
		}

		String textValue = textSelectNonEditable.getText();

		// Get value text
		Optional<T> parameterValue = selectValues.stream()
				.filter(param -> param != null && param.getId().toString().equals(textValue)).findFirst();

		if (parameterValue.isPresent()) {
			return parameterValue.get();
		} else {
			return null;
		}
	}

	/**
	 * Set the form value
	 * 
	 * @param value the value to set
	 */
	public void setValue(T value) {
		if (super.isEditable()) {
			setEditableValue(value);
		} else {
			setNonEditableValue(value);
		}
	}

	/**
	 * Set the form value
	 * 
	 * @param id the id to set
	 */
	public void setIdValue(Integer id) {

		if (selectValues != null) {

			// search with id
			T value = selectValues.stream()
					.filter(parameterValueTemp -> id != null && id.equals(parameterValueTemp.getId())).findAny()
					.orElse(null);

			// set value
			setValue(value);
		}
	}

	/**
	 * Set editable field value
	 * 
	 * @param value the form value to set
	 */
	private void setEditableValue(T value) {
		setEditableSelectValue(value);
	}

	/**
	 * Set Combo select value
	 * 
	 * @param value the value to set
	 */
	private void setEditableSelectValue(T value) {

		if (selectValues != null) {

			// Set value
			ISelectValue parameterValue = selectValues.stream()
					.filter(parameterValueTemp -> value != null && value.equals(parameterValueTemp)).findAny()
					.orElse(null);

			// Set parameter value
			if (parameterValue != null) {
				final ISelection slValueToUpdate = new StructuredSelection(parameterValue);
				selectViewer.setSelection(slValueToUpdate);
			}
		}
	}

	/**
	 * Set select value
	 * 
	 * @param value the value to set
	 */
	private void setNonEditableValue(T value) {
		if (value != null) {
			textSelectNonEditable.setText(value.getSelectName());
		}
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		if (selectViewer != null && selectViewer.getCombo() != null)
			selectViewer.getCombo().addKeyListener(listener);
	}

	/**
	 * @param listener the selection changed listener
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (selectViewer != null && selectViewer.getCombo() != null)
			selectViewer.addSelectionChangedListener(listener);
	}
}
