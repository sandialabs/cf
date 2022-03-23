package gov.sandia.cf.webapp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import gov.sandia.cf.webapp.exception.LockException;
import gov.sandia.cf.webapp.mapper.EntityLockInfoMapper;
import gov.sandia.cf.webapp.model.stub.EntityLock;
import gov.sandia.cf.webapp.model.stub.EntityLockInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LockService implements ILockService {

	@Autowired
	private EntityLockInfoMapper entityLockInfoMapper;

//	@PersistenceContext
//	private EntityManager entityManager;

	private Map<Class<?>, Map<Long, EntityLock>> lockMap;

	public LockService() {
		lockMap = new ConcurrentHashMap<>();
	}

	@Override
	public String lock(Class<?> entityClass, Long id, String information) {

		if (entityClass == null) {
			throw new LockException("The entity can not be locked if null");
		}
		if (id == null) {
			throw new LockException("The entity can not be locked if its id is null");
		}

		log.debug("Locking entity");

		UUID token = UUID.randomUUID();
		String tokenKey = token.toString();
		if (!lockMap.containsKey(entityClass)) {
			lockMap.put(entityClass, new ConcurrentHashMap<>());
		} 
		
		if (!lockMap.get(entityClass).containsKey(id)) {
			lockMap.get(entityClass).put(id, new EntityLock(tokenKey, entityClass, id, information));
		} else {
			throw new LockException("The entity is already locked.");
		}

		// To enable entity lock:
		// entityManager.lock(entity, LockModeType.PESSIMISTIC_READ, properties);

		return tokenKey;
	}

	@Override
	public String lock(Class<?> entityClass, Long id) {
		return lock(entityClass, id, "");
	}

	@Override
	public void unlock(String token, Class<?> entityClass, Long id) {

		if (token == null) {
			throw new LockException("The entity can not be unlocked with a null token");
		}

		if (entityClass == null) {
			throw new LockException("The entity can not be unlocked for a null entity");
		}

		if (id == null) {
			throw new LockException("The entity can not be unlocked for an entity with id null");
		}

		if (lockMap.containsKey(entityClass) && lockMap.get(entityClass).containsKey(id)
				&& token.equals(lockMap.get(entityClass).get(id).getToken())) {
			log.debug("Unlocking entity with token: " + token);
			lockMap.get(entityClass).remove(id);
		} else {
			throw new LockException("Bad token/entity-id identifier. The entity is still locked");
		}
	}

	@Override
	public String lock(EntityLock lock) {

		if (lock == null) {
			throw new LockException("The entity can not be locked with a null parameter");
		}

		return lock(lock.getEntityClass(), lock.getId(), lock.getInformation());
	}

	@Override
	public void unlock(EntityLock lock) {

		if (lock == null) {
			throw new LockException("The entity can not be unlocked with a null parameter");
		}

		unlock(lock.getToken(), lock.getEntityClass(), lock.getId());
	}

	@Override
	public boolean isLocked(Class<?> entityClass, Long id) {
		return lockMap.containsKey(entityClass) && lockMap.get(entityClass).containsKey(id)
				&& lockMap.get(entityClass).get(id) != null;
	}

	@Override
	public boolean isWritable(String token, Class<?> entityClass, Long id) {
		return lockMap.containsKey(entityClass) && lockMap.get(entityClass).containsKey(id) && token != null
				&& token.equals(lockMap.get(entityClass).get(id).getToken());
	}

	@Override
	public EntityLockInfo getLockInfo(Class<?> entityClass, Long id) {
		return lockMap.containsKey(entityClass) && lockMap.get(entityClass).containsKey(id)
				? entityLockInfoMapper.toInfo(lockMap.get(entityClass).get(id))
				: null;
	}

	@Scheduled(fixedDelay = 60000)
	public void unlockMechanism() {
		LocalDateTime currentDate = LocalDateTime.now();
		for (Entry<Class<?>, Map<Long, EntityLock>> entryMapLock : lockMap.entrySet()) {
			if (entryMapLock != null && entryMapLock.getValue() != null) {
				Map<Long, EntityLock> mapLock = entryMapLock.getValue();
				List<Long> toDelete = new ArrayList<>();
				for (Entry<Long, EntityLock> lockEntry : mapLock.entrySet()) {
					if (lockEntry != null && lockEntry.getValue() != null
							&& currentDate.isAfter(lockEntry.getValue().getDateExpiration())) {
						toDelete.add(lockEntry.getKey());
					}
				}
				toDelete.forEach(mapLock::remove);
			}
		}
	}

}
