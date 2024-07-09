package ghimire.ujjwal.agent.contract;

import ghimire.ujjwal.agent.postProcess.EmploymentInformation;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import ghimire.ujjwal.agent.session.Session;

public interface ContractService {

    String processInitialRequest(GeneralInformation generalInformation, String token);

    String processFinalRequest(EmploymentInformation employmentInformation, GeneralInformation generalInformation, String appToken, Session session);
}
