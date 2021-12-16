package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.*;
import ch.fuzzy.movie_suggester.util.ObjUtil;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;

import java.util.function.Consumer;

/**
 * The Bot which a user needs to create a {@link MovieFilter}
 * @author rbu
 */
public class MovieFilterBotPresenter extends VLayout{

    protected final MovieFilter filter; //NOTE: rbu 16.11.2021, make static to not lose it after refresh/navigation if wanted
    private final int NUMBER_OF_QUESTIONS = 10; //TODO: rbu 15.12.2021 Maybe just Navigate directly in lastAnswer instead of keeping Track like this
    private int currentQuestion;
    private VLayout currentInput;
    private Button send;
    protected boolean isEditMovie;

    private final boolean isInvestment;

    public MovieFilterBotPresenter(SettingsRepository repo) {this(repo, false);}
    protected MovieFilterBotPresenter(SettingsRepository repo, boolean isEditMovie) {
        super();
        this.filter = new MovieFilter();
        this.isEditMovie = isEditMovie;
        isInvestment = !ObjUtil.assertUniqueNotNull(repo.findAll()).isConcentrationFit();
        startQuestionnaire();
    }

    private void startQuestionnaire() {
        currentQuestion = 0;
        createQuestion(getNextQuestion());
        add(currentInput = createInput());
    }

    private void createAnswer(String answer) {
        HLayout layout = new HLayout(this);
        VLayout a = new VLayout(this);
        a.getElement().getStyle().set("align-items", "end");
        a.addTitle("User");
        a.addText(answer);
        layout.add(a);
        layout.addImage("./frontend/pictures/user.png");
        layout.setHeight("70px");
        layout.setWidth("50%");
        add(layout);
    }

    private void createQuestion(String question) {
        HLayout layout = new HLayout(this);
        layout.addImage("./frontend/pictures/robot.png");
        VLayout q = new VLayout(this);
        q.addTitle("MovieBot");
        q.addText(question);
        layout.add(q);
        layout.setHeight("70px");
        layout.setWidth("50%");
        add(layout);
    }

    private VLayout createInput(){
        VLayout resLayout = new VLayout(this);
        HLayout layout = new HLayout(resLayout);
        resLayout.getElement().getStyle().set("align-self", "center");
        resLayout.setWidth("75%");
        addInput(layout);
        send = new Button("Send");
        send.setEnabled(getLastAnswer(false) != null);
        layout.add(send);
        send.addClickListener(e -> sendAnswer(false));
        Button showResults = new Button("Show Results");
        showResults.setVisible(!isEditMovie);
        showResults.addClickListener(e -> {
            if(currentQuestion == 0){ filter.setNumberWatchers(0); } //If the question hasn't come up, yet it should be null
            gotoResult();
        });
        layout.add(showResults);
        Button dontCare = new Button("I Don't care");
        dontCare.setVisible(!isEditMovie);
        dontCare.addClickListener(e -> sendAnswer(true));
        layout.add(dontCare);
        HLayout secondLayout = new HLayout(resLayout);
        addWeight(secondLayout);
        resLayout.add(layout, secondLayout);
        add(resLayout);
        return resLayout;
    }

    private void sendAnswer(boolean dontCare) {
        if(currentQuestion==NUMBER_OF_QUESTIONS){
            gotoResult();
            return;
        }
        remove(currentInput);
        createAnswer(getLastAnswer(dontCare));
        currentQuestion++;
        currentQuestion = skip(currentQuestion, 2, filter.getNumberWatchers() != null && filter.getNumberWatchers() == 1);
        currentQuestion = skip(currentQuestion, 7, isInvestment);
        createQuestion(getNextQuestion());
        currentInput = createInput();
        setSizeFull();
    }

    /**
     * Some Questions are skipped when they don't make sense or aren't considered in the result
     */
    private int skip(int currentQuestion, int toSkip, boolean condition){
        if(condition && currentQuestion == toSkip){ return ++currentQuestion; }
        return currentQuestion;
    }

    protected void gotoResult() {
        ComponentUtil.setData(UI.getCurrent(), MovieFilter.class, filter);
        UI.getCurrent().navigate(MovieResultPresenter.class);
    }

    //TODO: 17.11.2021, create Enum encapsulating these four properties getLastAnswer, getNextQuestion, addInput, getWeight
    private String getLastAnswer(boolean dontCare) {
        if(dontCare){ return "I don't care"; }
        switch (currentQuestion){
            case 0: return ObjUtil.toString(filter.getGenres());
            case 1: return ObjUtil.toString(filter.getNumberWatchers());
            case 2: return ObjUtil.toString(filter.getRelationship());
            case 3: return ObjUtil.toString(filter.getLanguage());
            case 4: return ObjUtil.toString(filter.getPlatforms());
            case 5: return ObjUtil.toString(filter.getAgeRestriction());
            case 6: return ObjUtil.toString(filter.getEmotionality()) + "%";
            case 7: return ObjUtil.toString(filter.getInvested()) + "%";
            case 8: return ObjUtil.toString(filter.getPositiveKeywords());
            case 9: return ObjUtil.toString(filter.getNegativeKeywords());
            case 10: return ObjUtil.toString(filter.getScreen());
            default: throw  new IllegalStateException("No Answer defined for question " + currentQuestion);
        }
    }

    private String getNextQuestion() {
        switch (currentQuestion){
            case 0: return "What Genre would you like to watch?";
            case 1: return "How many people will be watching?";
            case 2: return "Who are you watching the Movie with?";
            case 3: return "What Language should the movie be in?";
            case 4: return "On which platforms are you able to watch the movie?";
            case 5: return "What's the highest Age Restriction which should be present?";
            case 6: return "How emotional should the movie be?";
            case 7: return "How invested do you want to be in the movie?";
            case 8: return "Are there any keywords the movie should contain?";
            case 9: return "Are there any keywords you'd like to avoid?";
            case 10: return "On what Screen are you watching?";
            default: throw  new IllegalStateException("No Question defined for question " + currentQuestion);
        }
    }

    private void addInput(HLayout layout) {
        switch (currentQuestion){
            case 0: layout.addMultiSelectComboBox(filter::setGenres, Genre.GenreType.values()); return;
            case 1: layout.addIntegerField("", filter::setNumberWatchers, 1, true, 1, 9999, 1); return;
            case 2: layout.addSelect("", filter::setRelationship, Relationship.values()); return;
            case 3: layout.addSelect("", filter::setLanguage, Language.values()); return;
            case 4: layout.addMultiSelectComboBox(filter::setPlatforms, Platform.values()); return;
            case 5: layout.addSelect("", filter::setAgeRestriction, AgeRestriction.values()); return;
            case 6: layout.addSlider(filter::setEmotionality); return;
            case 7: layout.addSlider(filter::setInvested); return;
            case 8: layout.addMultiSelectComboBox(filter::setPositiveKeywords, Keyword.KeywordValue.values()); return;
            case 9: layout.addMultiSelectComboBox(filter::setNegativeKeywords, Keyword.KeywordValue.values()); return;
            case 10: layout.addSelect("", filter::setScreen, Screen.getFilterValues()); return;
            default: throw new IllegalStateException("No Inputmethod defined for question " + currentQuestion);
        }
    }

    private void addWeight(HLayout layout){
        if(isEditMovie){ return;}
        switch (currentQuestion){
            case 0: addWeightChooser(layout, filter::setGenreWeight); return;
            case 1: addWeightChooser(layout, filter::setNumberWatchersWeight); return;
            case 2: addWeightChooser(layout, filter::setRelationshipWeight); return;
            case 3:
            case 4: //For Hard rules there are no weights
            case 5: return;
            case 6: addWeightChooser(layout, filter::setEmotionalityWeight); return;
            case 7: addWeightChooser(layout, filter::setInvestedWeight); return;
            case 8: addWeightChooser(layout, filter::setPositiveKeywordsWeight); return;
            case 9: addWeightChooser(layout, filter::setNegativeKeywordsWeight); return;
            case 10: addWeightChooser(layout, filter::setScreenWeight); return;
            default: throw new IllegalStateException("No Weight defined for question " + currentQuestion);
        }
    }

    private void addWeightChooser(HLayout layout, Consumer<MovieFilter.Weight> setter) {
        layout.addText("Importance of this Question: ");
        layout.addRadioButtons("", setter, MovieFilter.Weight.values());
    }

    @Override
    public void fireStateChanged() {
        super.fireStateChanged();
        send.setEnabled(getLastAnswer(false) != null);
    }
}
