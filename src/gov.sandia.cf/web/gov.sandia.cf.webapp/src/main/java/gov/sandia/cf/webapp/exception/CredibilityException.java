package gov.sandia.cf.webapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class CredibilityException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String message;
}