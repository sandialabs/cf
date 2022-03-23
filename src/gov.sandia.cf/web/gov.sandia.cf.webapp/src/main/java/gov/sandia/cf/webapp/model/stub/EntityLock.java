package gov.sandia.cf.webapp.model.stub;

import java.time.LocalDateTime;

import gov.sandia.cf.webapp.model.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EntityLock {
	private String token;
	private LocalDateTime dateExpiration;
	private User user;
	private Class<?> entityClass;
	private Long id;
	private String information;

	public static final Long EXPIRATION_DELAY_SECONDS = 7200L;

	public EntityLock(String token, Class<?> entityClass, Long id) {
		this(token, entityClass, id, "");
	}

	public EntityLock(String token, Class<?> entityClass, Long id, String information) {
		this.token = token;
		this.entityClass = entityClass;
		this.id = id;
		this.dateExpiration = LocalDateTime.now().plusSeconds(EXPIRATION_DELAY_SECONDS);
		this.information = information;
	}

}