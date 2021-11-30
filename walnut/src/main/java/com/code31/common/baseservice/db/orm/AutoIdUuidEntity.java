package com.code31.common.baseservice.db.orm;

import javax.annotation.Nonnegative;
import javax.persistence.*;


@Entity
public abstract class AutoIdUuidEntity implements IEntity<Long> {
	private Long id;
	private String uuid;

	@Id
	@Column(name = "id", columnDefinition = "NOT NULL AUTO_INCREMENT")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(@Nonnegative Long id) {
		// Preconditions.checkArgument(id > 0, "id");
		this.id = id;
	}

	@Column(name = "uuid", length = 64, nullable = false, unique = true, columnDefinition = "NOT NULL")
	public final String getUuid() {
		return uuid;
	}

	public final void setUuid(@Nonnegative String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String[] getSubKeys() {
		return null;
	}

	@Override
	public String getMainKey() {
		return this.getClass().getSimpleName() + "_uuid_" + "_%s";
	}
}
