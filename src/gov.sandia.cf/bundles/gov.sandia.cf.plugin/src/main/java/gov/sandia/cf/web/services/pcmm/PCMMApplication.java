/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.pcmm;

import java.util.List;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;

/**
 * Manage PCMM Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMApplication extends AApplication implements IPCMMApplication {
	/**
	 * The constructor
	 */
	public PCMMApplication() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public PCMMApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public PCMMSpecification loadPCMMConfiguration(Model model) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMPhase> getPCMMPhases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMOption> getPCMMOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMOption addPCMMOption(PCMMOption option) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMOption updatePCMMOption(PCMMOption option) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePCMMOption(PCMMOption option) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllPCMMOptions(List<PCMMOption> options) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<PCMMElement> getElementList(Model model) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMElement getElementById(Integer id) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMElement addElement(PCMMElement element) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMElement updateElement(PCMMElement element) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteElement(PCMMElement element) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public PCMMLevel getLevelById(Integer id) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMLevel addLevel(PCMMLevel level) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMLevel updateLevel(PCMMLevel level) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteLevel(PCMMLevel level) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public PCMMLevelDescriptor getLevelDescriptorById(Integer id) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMLevelDescriptor addLevelDescriptor(PCMMLevelDescriptor levelDescriptor) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMLevelDescriptor updateLevelDescriptor(PCMMLevelDescriptor levelDescriptor) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteLevelDescriptor(PCMMLevelDescriptor levelDescriptor) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public PCMMSubelement getSubelementById(Integer id) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMSubelement addSubelement(PCMMSubelement subelement) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMSubelement updateSubelement(PCMMSubelement subelement) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteSubelement(PCMMSubelement subelement) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Role> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role getRoleById(Integer id) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role addRole(Role role) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role updateRole(Role role) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRole(Role role) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public PCMMLevelColor addLevelColor(PCMMLevelColor levelColor) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMLevelColor updateLevelColor(PCMMLevelColor levelColor) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteLevelColor(PCMMLevelColor levelColor) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public Tag tagCurrent(Tag newTag) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tag> getTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag updateTag(Tag tag) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteTag(Tag tag) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public int computeCurrentProgress(Model model, PCMMSpecification configuration) throws CredibilityException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int computeCurrentProgressByElement(PCMMElement element, Tag selectedTag, PCMMSpecification configuration)
			throws CredibilityException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int computeEvidenceProgress(PCMMElement element, Tag selectedTag, PCMMMode mode) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int computeAssessProgress(PCMMElement element, Tag selectedTag, PCMMMode mode) throws CredibilityException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int computeMaxProgress(PCMMSpecification configuration) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int computeEvidenceMaxProgress(PCMMElement element, PCMMMode mode) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int computeAssessMaxProgress(PCMMElement element, PCMMMode mode) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PCMMElement getElementFromKey(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMSubelement getSubelementFromKey(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean sameConfiguration(PCMMSpecification spec1, PCMMSpecification spec2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPCMMEnabled(Model model) throws CredibilityException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refreshElement(PCMMElement element) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshSubelement(PCMMSubelement subelement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshAssessable(IAssessable element) {
		// TODO Auto-generated method stub
		
	}
}
