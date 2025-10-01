package ch.springall;

import org.junit.jupiter.api.Test;
import org.mapstruct.Mapping;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringAllApplicationTests {
    @Mock
    ch.springall.repository.jpa.RepositoryDirector repositoryDirector;

    @Test
    void contextLoads() {
    }

}
