/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class LockException.
 * 
 * @author Didier Verstraete
 */
@Getter
@Setter
@AllArgsConstructor
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class LockException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String message;
}