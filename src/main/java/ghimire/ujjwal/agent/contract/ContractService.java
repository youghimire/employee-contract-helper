package ghimire.ujjwal.agent.contract;

import ghimire.ujjwal.agent.postProcess.EmploymentInformation;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;

public interface ContractService {

    String processInitialRequest(GeneralInformation generalInformation, String token);

    String processFinalRequest(EmploymentInformation employmentInformation, GeneralInformation generalInformation, String appToken, String contractId);
}
