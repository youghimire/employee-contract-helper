package ghimire.ujjwal.agent.contract.dto;

import ghimire.ujjwal.agent.postProcess.ValidateInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryISO {

    public CountryISO(String iso) {
        this.iso2 = iso.toUpperCase();
        this.name = ValidateInformation.getCountryName(iso2);
    }
    String name;
    String iso2;
}
