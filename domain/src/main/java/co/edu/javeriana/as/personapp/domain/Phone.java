package co.edu.javeriana.as.personapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
	@NonNull
	private String number;
	@NonNull
	private String company;
	@NonNull
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Person owner;
}
