package co.edu.javeriana.as.personapp.mariadb.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

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
@Embeddable
public class EstudiosEntityPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
	@Column(name = "id_prof")
	private int idProf;
	@Basic(optional = false)
	@Column(name = "cc_per")
	private int ccPer;

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (int) idProf;
		hash += (int) ccPer;
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof EstudiosEntityPK)) {
			return false;
		}
		EstudiosEntityPK other = (EstudiosEntityPK) object;
		if (this.idProf != other.idProf) {
			return false;
		}
		if (this.ccPer != other.ccPer) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "EstudiosEntityPK [idProf=" + idProf + ", ccPer=" + ccPer + "]";
	}

}
