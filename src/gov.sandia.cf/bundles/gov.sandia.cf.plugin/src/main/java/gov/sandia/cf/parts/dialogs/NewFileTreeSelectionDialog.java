/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.dialogs;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import gov.sandia.cf.parts.widgets.TextWidget;
import gov.sandia.cf.tools.RscTools;

/**
 * A dialog to select/create a new file in the Eclipse workspace
 * 
 * @author Didier Verstraete
 *
 */
public class NewFileTreeSelectionDialog extends ElementTreeSelectionDialog {

	private TextWidget text;
	private String newFileValue;
	/**
	 * The resource manager
	 */
	private ResourceManager rscMgr;

	/**
	 * Constructor
	 * 
	 * @param rscMgr          the resource manager used to manage the resources
	 *                        (fonts, colors, images, cursors...)
	 * @param parent          the parent composite
	 * @param labelProvider   the label provider
	 * @param contentProvider the content provider
	 */
	public NewFileTreeSelectionDialog(ResourceManager rscMgr, Shell parent, ILabelProvider labelProvider,
			ITreeContentProvider contentProvider) {
		super(parent, labelProvider, contentProvider);

		Assert.isNotNull(rscMgr);
		this.rscMgr = rscMgr;

		newFileValue = RscTools.empty();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		// construct element selector
		text = new TextWidget(rscMgr, composite, SWT.NONE, true, null);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				newFileValue = text.getValue();
			}
		});

		getTreeViewer().addSelectionChangedListener(event -> {
			IResource resource = (IResource) super.getFirstResult();
			if (resource instanceof IFile) {
				String resourceName = ((IFile) resource).getName();
				text.setValue(resourceName);
				newFileValue = resourceName;
			}
		});

		return composite;
	}

	@Override
	public Object getFirstResult() {
		IResource resource = (IResource) super.getFirstResult();
		if (!StringUtils.isBlank(newFileValue)) {
			if (resource instanceof IContainer) {
				return ((IContainer) resource).getFile(new Path(newFileValue));
			} else if (resource instanceof IFile) {
				return ((IFile) resource).getParent().getFile(new Path(newFileValue));
			}
		}
		return resource;
	}
}
