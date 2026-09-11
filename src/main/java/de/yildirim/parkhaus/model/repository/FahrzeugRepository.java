package de.yildirim.parkhaus.model.repository;

import de.yildirim.parkhaus.model.entity.Fahrzeug;
import org.springframework.data.repository.CrudRepository;

public interface FahrzeugRepository extends CrudRepository<Fahrzeug, String>
{
}
