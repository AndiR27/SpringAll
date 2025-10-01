package ch.springall.repository.jpa;

import ch.springall.entity.Director;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("jpaDirector")
public interface RepositoryDirector extends JpaRepository<Director, Long> {

    Director findByFirstNameAndLastName(String FirstName, String LastName);



}
