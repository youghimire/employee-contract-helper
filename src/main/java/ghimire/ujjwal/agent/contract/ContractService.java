package ghimire.ujjwal.agent.contract;

import ghimire.ujjwal.agent.postProcess.EmploymentInformation;
import ghimire.ujjwal.agent.postProcess.GeneralInformation;
import ghimire.ujjwal.agent.session.Session;

public interface ContractService {

    Session processInitialRequest(GeneralInformation generalInformation, String token, Session session);

    Session processFinalRequest(EmploymentInformation employmentInformation, GeneralInformation generalInformation, String appToken, Session session);
}
