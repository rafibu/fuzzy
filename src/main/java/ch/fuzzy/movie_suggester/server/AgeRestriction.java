package ch.fuzzy.movie_suggester.server;

import ch.fuzzy.movie_suggester.util.ObjUtil;

public enum AgeRestriction implements IFilterElement{
    NONE("None"),
    SIX("6+", NONE),
    TWELVE("12+", NONE, SIX),
    SIXTEEN("16+", NONE, TWELVE, SIX),
    EIGHTEEN("18+", NONE, SIXTEEN, TWELVE, SIX);

    private final String name;
    private final AgeRestriction[] included; //are implicitly included in this restriction

    AgeRestriction(String name, AgeRestriction... includes){
        this.name = name;
        included = includes;
    }

    /**
     * Does not restrict itself or if restriction is null
     */
    public boolean restricts(AgeRestriction restriction){
        return ObjUtil.isContained(restriction, this, null) || !ObjUtil.isContained(restriction, included);
    }

    @Override public String getName() {return name;}
}
