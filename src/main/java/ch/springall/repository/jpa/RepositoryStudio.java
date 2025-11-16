package ch.springall.repository.jpa;

import ch.springall.entity.Studio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryStudio extends JpaRepository<Studio, Long> {

    Studio findByStudioName(String name);


}
