/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.guidance;

import org.eclipse.swt.graphics.RGB;

/**
 * Level Color descriptor for the PIRT Guidance View
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTLevelColorDescriptor {

	/**
	 * The level description
	 */
	private String description;

	/**
	 * Color corresponding to the level
	 */
	private RGB color;

	PIRTLevelColorDescriptor(String description, RGB color) {
		this.description = description;
		this.color = color;
	}

	@SuppressWarnings("javadoc")
	public String getDescription() {
		return description;
	}

	@SuppressWarnings("javadoc")
	public void setDescription(String description) {
		this.description = description;
	}

	@SuppressWarnings("javadoc")
	public RGB getColor() {
		return color;
	}

	@SuppressWarnings("javadoc")
	public void setColor(RGB color) {
		this.color = color;
	}

}
