package io.zuppelli.userservice.model.key;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.UUID;

@PrimaryKeyClass
public class RatingKey {
    @PrimaryKeyColumn(name= "id",
            ordinal = 0,
            type = PrimaryKeyType.PARTITIONED)
    private UUID id;
    @PrimaryKeyColumn(name="movie_id",
            ordinal = 1,
            type = PrimaryKeyType.CLUSTERED)
    private String movieId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
