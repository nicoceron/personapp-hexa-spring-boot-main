package co.edu.javeriana.as.personapp.mongo.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "telefono")
public class TelefonoDocument {

    @Id
    private String num; // Phone number as ID

    private String oper;

    private Integer duenio;
}
