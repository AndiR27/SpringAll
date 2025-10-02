package ch.springall.service;

import ch.springall.dtos.MovieRecord;
import ch.springall.entity.Movie;
import ch.springall.mapper.MapperMovie;
import ch.springall.repository.jpa.RepositoryMovie;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceMovie {

    //repo
    private final RepositoryMovie repositoryMovie;
    private final MapperMovie mapperMovie;

    public ServiceMovie(RepositoryMovie repositoryMovie, MapperMovie mapperMovie) {
        this.repositoryMovie = repositoryMovie;
        this.mapperMovie = mapperMovie;
    }

    //add a movie
    public MovieRecord addMovie(MovieRecord movie){
        Movie m = this.mapperMovie.fromRecordToEntity(movie);
        this.repositoryMovie.save(m);
        return mapperMovie.toRecord(m);
    }

    //Find a movie
    public Optional<MovieRecord> findMovie(Long movieId){
        Optional<Movie> m = this.repositoryMovie.findById(movieId);
        if(m.isPresent()){
            return Optional.of(mapperMovie.toRecord(m.get()));
        }
        return Optional.empty();
    }
}
