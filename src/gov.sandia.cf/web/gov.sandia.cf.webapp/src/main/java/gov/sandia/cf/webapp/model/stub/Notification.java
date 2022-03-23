package gov.sandia.cf.webapp.model.stub;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Notification {
    private String type;
    private String value;
}