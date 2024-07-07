package ghimire.ujjwal.agent.contract;

import ghimire.ujjwal.agent.postProcess.GeneralInformation;

public interface ContractService {

    String processInitialRequest(GeneralInformation generalInformation, String token);
}
