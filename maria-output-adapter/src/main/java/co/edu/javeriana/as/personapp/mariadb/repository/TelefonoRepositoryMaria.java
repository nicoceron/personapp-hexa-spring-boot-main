package co.edu.javeriana.as.personapp.mariadb.repository;

import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelefonoRepositoryMaria extends JpaRepository<TelefonoEntity, String> {
    List<TelefonoEntity> findByDuenio_Cc(Integer duenioCc);
} 