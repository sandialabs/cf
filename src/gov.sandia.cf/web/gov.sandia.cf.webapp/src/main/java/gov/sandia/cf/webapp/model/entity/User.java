package gov.sandia.cf.webapp.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Do not use @Data with Entity to avoid default toString, Hashcode and Equals methods
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "USER")
public class User {

	/**
	 * The id field linked to ID column
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	/**
	 * The userID field linked to USERID column
	 */
	@Column(name = "USERID")
	// @NotBlank(message = RscConst.EX_USER_USERID_BLANK)
	private String userID;

	/**
	 * The name field linked to NAME column
	 */
	@Column(name = "NAME")
	// @NotBlank(message = RscConst.EX_USER_NAME_BLANK)
	private String name;

	/**
	 * The firstName field linked to FIRSTNAME column
	 */
	@Column(name = "FIRSTNAME")
	// @NotBlank(message = RscConst.EX_USER_FIRSTNAME_BLANK)
	private String firstName;

	/**
	 * The rolePCMM field linked to ROLE_PCMM_ID column
	 */
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "CURRENT_ROLE_ID")
	// private Role rolePCMM;
}
