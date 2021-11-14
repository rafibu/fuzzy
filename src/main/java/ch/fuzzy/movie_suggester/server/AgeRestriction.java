package ch.fuzzy.movie_suggester.server;

import ch.fuzzy.movie_suggester.util.ObjUtil;

public enum AgeRestriction implements IFilterElement{
    SIX("6+"),
    TWELVE("12+", SIX),
    SIXTEEN("16+", TWELVE, SIX),
    EIGHTEEN("18+", SIXTEEN, TWELVE, SIX);

    private final String name;
    private final AgeRestriction[] included; //are implicitly included in this restriction

    AgeRestriction(String name, AgeRestriction... includes){
        this.name = name;
        included = includes;
    }

    public boolean restricts(AgeRestriction restriction){
        return this == restriction || ObjUtil.isContained(restriction, included);
    }

    @Override public String getName() {return name;}
}
