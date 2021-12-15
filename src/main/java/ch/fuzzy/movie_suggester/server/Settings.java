package ch.fuzzy.movie_suggester.server;

import javax.persistence.*;

/**
 * Global settings of the application
 * @author rbu
 */
@Entity
public class Settings {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * describes the Distance Function used for the Fit calculations
     */
    @Column(name = "distancefunction")
    @Enumerated(EnumType.STRING)
    private Distance distanceFunction;

    /**
     * This flag indicates whether we want the relationship and number of watchers to be calculated together (in the concentration Fit) or separate
     */
    private boolean isConcentrationFit;

    public Distance getDistanceFunction() {return distanceFunction;}
    public void setDistanceFunction(Distance distance) {this.distanceFunction = distance;}

    public boolean isConcentrationFit() {return isConcentrationFit;}
    public void setConcentrationFit(boolean concentrationFit) {isConcentrationFit = concentrationFit;}

    public Long getId() {return id;}
    protected void setId(Long id) {this.id = id;}

    /**
     * Distance Functions
     * This enum allows us to change them dynamically for testing out which one feels the most natural
     */
    public enum Distance implements IFilterElement{
        L2("L2"),
        L1("L1"),
        L2_ONLY_ON_FITS("L2 only on Fits"),
        L2_COMPLETE("L2 on Fits and Final Calculation");

        private final String name;

        Distance(String name){
            this.name = name;
        }

        @Override public String getName() {return name;}
    }
}
