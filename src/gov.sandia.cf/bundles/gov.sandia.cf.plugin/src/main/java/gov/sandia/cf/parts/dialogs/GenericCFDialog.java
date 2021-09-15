/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.dialogs;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

import gov.sandia.cf.model.IEntity;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.tools.ValidationTools;

/**
 * Generic Credibility Dialog (resizable)
 * 
 * @author Didier Verstraete
 *
 * @param <V> the view manager type
 */
public class GenericCFDialog<V extends IViewManager> extends TitleAreaDialog {

	/**
	 * The view manager
	 */
	private V viewManager;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 */
	public GenericCFDialog(V viewManager, Shell parentShell) {
		super(parentShell);
		this.viewManager = viewManager;
		this.setHelpAvailable(false);
		setDialogHelpAvailable(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isResizable() {
		return true;
	}

	/**
	 * @return the view manager
	 */
	public V getViewManager() {
		return viewManager;
	}

	/**
	 * Validate the dialog
	 * 
	 * @param entity the entity to validate
	 * @return true if the entity is validated, otherwise false
	 */
	protected boolean validate(IEntity<?, ?> entity) {

		// validate the input
		Set<ConstraintViolation<IEntity<?, ?>>> validation = getViewManager().getAppManager().getValidator()
				.validate(entity);

		if (validation != null && !validation.isEmpty()) {
			setErrorMessage(ValidationTools.constraintsToString(validation));
		} else {
			setErrorMessage(null);
		}

		return validation == null || validation.isEmpty();
	}

}
