/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface Migration Task used to identify and load the migration tasks.
 * 
 * @author Didier Verstraete
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface MigrationTask {
	
	/**
	 * Name.
	 *
	 * @return the string
	 */
	public String name();

	/**
	 * Id.
	 *
	 * @return the int
	 */
	public int id();
}
