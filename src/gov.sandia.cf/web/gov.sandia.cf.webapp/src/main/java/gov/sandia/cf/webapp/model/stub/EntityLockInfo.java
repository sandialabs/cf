package gov.sandia.cf.webapp.model.stub;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EntityLockInfo {
	private Class<?> entityClass;
	private Long id;
	private String information;

	public EntityLockInfo(Class<?> entityClass, Long id) {
		this(entityClass, id, "");
	}

	public EntityLockInfo(Class<?> entityClass, Long id, String information) {
		this.entityClass = entityClass;
		this.id = id;
		this.information = information;
	}

}