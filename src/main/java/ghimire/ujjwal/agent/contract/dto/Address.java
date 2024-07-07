package ghimire.ujjwal.agent.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String addressLine1 = "865 Market Street San Francisco Centre";
    private String city = "San Francisco";
    private CountryISO country = new CountryISO("United States", "US");
    private StateISO state = new StateISO("California", "CA", "California");
    private String zipcode = "94103";
}
