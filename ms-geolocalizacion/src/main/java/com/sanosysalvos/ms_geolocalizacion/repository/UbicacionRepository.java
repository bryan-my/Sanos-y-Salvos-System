package com.sanosysalvos.ms_geolocalizacion.repository;

import com.sanosysalvos.ms_geolocalizacion.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {
    List<Ubicacion> findByIdMascota(Long idMascota);

    List<Ubicacion> findAllByOrderByFechaRegistroDesc();

    Optional<Ubicacion> findFirstByIdMascotaOrderByFechaRegistroDesc(Long idMascota);
}
