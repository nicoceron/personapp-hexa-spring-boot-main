package co.edu.javeriana.as.personapp.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Study {
	@NonNull
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Person person;
	@NonNull
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Profession profession;
	private LocalDate graduationDate;
	private String universityName;
}
