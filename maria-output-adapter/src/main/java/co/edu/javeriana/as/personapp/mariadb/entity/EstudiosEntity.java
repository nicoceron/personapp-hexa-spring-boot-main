package co.edu.javeriana.as.personapp.mariadb.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author aasanchez
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="estudios", catalog = "persona_db", schema = "")
@NamedQueries({ @NamedQuery(name = "EstudiosEntity.findAll", query = "SELECT e FROM EstudiosEntity e"),
		@NamedQuery(name = "EstudiosEntity.findByIdProf", query = "SELECT e FROM EstudiosEntity e WHERE e.estudiosPK.idProf = :idProf"),
		@NamedQuery(name = "EstudiosEntity.findByCcPer", query = "SELECT e FROM EstudiosEntity e WHERE e.estudiosPK.ccPer = :ccPer"),
		@NamedQuery(name = "EstudiosEntity.findByFecha", query = "SELECT e FROM EstudiosEntity e WHERE e.fecha = :fecha"),
		@NamedQuery(name = "EstudiosEntity.findByUniver", query = "SELECT e FROM EstudiosEntity e WHERE e.univer = :univer") })
public class EstudiosEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@EmbeddedId
	protected EstudiosEntityPK estudiosPK;
	@Column(name = "fecha")
	@Temporal(TemporalType.DATE)
	private Date fecha;
	@Column(name = "univer", length = 50)
	private String univer;
	@JoinColumn(name = "cc_per", referencedColumnName = "cc", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private PersonaEntity persona;
	@JoinColumn(name = "id_prof", referencedColumnName = "id", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private ProfesionEntity profesion;

	public EstudiosEntity(EstudiosEntityPK estudiosPK) {
		this.estudiosPK = estudiosPK;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (estudiosPK != null ? estudiosPK.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof EstudiosEntity)) {
			return false;
		}
		EstudiosEntity other = (EstudiosEntity) object;
		if ((this.estudiosPK == null && other.estudiosPK != null)
				|| (this.estudiosPK != null && !this.estudiosPK.equals(other.estudiosPK))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "EstudiosEntity [estudiosPK=" + estudiosPK + ", fecha=" + fecha + ", univer=" + univer + "]";
	}

}
