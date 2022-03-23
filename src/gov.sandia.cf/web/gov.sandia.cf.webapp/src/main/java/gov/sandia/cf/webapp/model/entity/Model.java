/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
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

/**
 * The Model entity class linked to table MODEL
 * 
 * @author Didier Verstraete
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "MODEL")
public class Model {

	/**
	 * The id field linked to ID column
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	/**
	 * The name field linked to APPLICATION column
	 */
	@Column(name = "APPLICATION")
	private String application;

	/**
	 * The name field linked to CONTACT column
	 */
	@Column(name = "CONTACT")
	private String contact;

	/**
	 * The name field linked to VERSION_ORIGIN
	 */
	@Column(name = "VERSION_ORIGIN")
//	@NotBlank(message = RscConst.EX_MODEL_VERSIONORIGIN_BLANK)
	private String versionOrigin;

	/**
	 * The name field linked to VERSION
	 */
	@Column(name = "VERSION")
//	@NotBlank(message = RscConst.EX_MODEL_VERSION_BLANK)
	private String version;

	/**
	 * The confFileList field linked to model column
	 */
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "model")
//	private List<ConfigurationFile> confFileList;

}