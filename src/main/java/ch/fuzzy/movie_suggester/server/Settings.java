package ch.fuzzy.movie_suggester.server;

import javax.persistence.*;

@Entity
public class Settings {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "distancefunction")
    @Enumerated(EnumType.STRING)
    private Distance distanceFunction;

    public Distance getDistanceFunction() {return distanceFunction;}
    public void setDistanceFunction(Distance l2Distance) {this.distanceFunction = l2Distance;}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public enum Distance implements IFilterElement{
        L2("L2"),
        L1("L1");

        private final String name;

        Distance(String name){
            this.name = name;
        }

        @Override public String getName() {return name;}
    }
}
