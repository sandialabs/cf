package gov.sandia.cf.webapp.model.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Do not use @Data with Entity to avoid default toString, Hashcode and Equals methods
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "INTENDED_PURPOSE")
public class IntendedPurpose {

	/**
	 * The id field linked to ID column
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	/**
	 * description: the description
	 */
	@Column(name = "DESCRIPTION", length = 100000)
	private String description;

	/**
	 * reference: the intended purpose reference
	 */
	@Column(name = "REFERENCE_LINK", length = 100000)
	private String reference;

	/**
	 * The project field linked to PROJECT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	private Model model;

	/**
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	private User userCreation;

	/**
	 * The userUpdate field linked to USER_UPDATE_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_UPDATE_ID")
	private User userUpdate;

	/**
	 * The dateCreation field linked to DATE_CREATION column
	 */
	@Column(name = "DATE_CREATION")
	private LocalDateTime dateCreation;
	/**
	 * The dateUpdate field linked to DATE_UPDATE column
	 */
	@Column(name = "DATE_UPDATE")
	private LocalDateTime dateUpdate;

}