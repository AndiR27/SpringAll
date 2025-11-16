package ch.springall.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "studio")
public class Studio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "studio_name", unique = true, nullable = false)
    private String studioName;

    @Column(name = "studioFoundedYear")
    private int studioFoundedYear;

    @OneToMany
    @JoinColumn(name = "studio_id", nullable = false)
    private List<Director> directorList;

    public Studio() {}
    public Studio(String studioName, int studioFoundedYear, List<Director> directorList) {
        this.studioName = studioName;
        this.studioFoundedYear = studioFoundedYear;
        this.directorList = directorList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudioName() {
        return studioName;
    }

    public void setStudioName(String studioName) {
        this.studioName = studioName;
    }

    public int getStudioFoundedYear() {
        return studioFoundedYear;
    }

    public void setStudioFoundedYear(int studioFoundedYear) {
        this.studioFoundedYear = studioFoundedYear;
    }

    public List<Director> getDirectorList() {
        return directorList;
    }

    public void setDirectorList(List<Director> directorList) {
        this.directorList = directorList;
    }
}
