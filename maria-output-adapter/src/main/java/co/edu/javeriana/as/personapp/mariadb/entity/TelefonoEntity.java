package co.edu.javeriana.as.personapp.mariadb.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "telefono", catalog = "persona_db", schema = "")
public class TelefonoEntity {

    @Id
    @Basic(optional = false)
    @Column(name = "num", nullable = false, length = 20)
    private String num;

    @Column(name = "oper", length = 50)
    private String operador;

    @JoinColumn(name = "duenio", referencedColumnName = "cc")
    @ManyToOne(optional = false)
    private PersonaEntity duenio;
} 