package ghimire.ujjwal.agent.contract.dto;

import ghimire.ujjwal.agent.postProcess.PostProcessGeneralInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryISO {

    public CountryISO(String iso) {
        this.iso2 = iso.toUpperCase();
        this.name = PostProcessGeneralInformation.getCountryName(iso2);
    }
    String name;
    String iso2;
}
