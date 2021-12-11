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

    /**
     * This flag indicates whether we want the relationship and number of watchers to be calculated together or alone
     */
    private boolean isConcentrationFit;

    public Distance getDistanceFunction() {return distanceFunction;}
    public void setDistanceFunction(Distance distance) {this.distanceFunction = distance;}

    public boolean isConcentrationFit() {return isConcentrationFit;}
    public void setConcentrationFit(boolean concentrationFit) {isConcentrationFit = concentrationFit;}

    public Long getId() {return id;}
    protected void setId(Long id) {this.id = id;}

    public enum Distance implements IFilterElement{
        L2("L2"),
        L1("L1"),
        L2_COMPLETE("L2 on all fits");

        private final String name;

        Distance(String name){
            this.name = name;
        }

        @Override public String getName() {return name;}
    }
}
