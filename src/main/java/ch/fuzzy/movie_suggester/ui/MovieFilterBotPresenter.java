package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.*;
import ch.fuzzy.movie_suggester.util.ObjUtil;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;

public class MovieFilterBotPresenter extends VLayout{

    private final MovieFilter filter;
    private final int NUMBER_OF_QUESTIONS = 9;
    private int currentQuestion;
    private HLayout currentInput;

    public MovieFilterBotPresenter() {
        super();
        this.filter = new MovieFilter();
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

    private HLayout createInput(){
        HLayout layout = new HLayout(this);
        layout.getElement().getStyle().set("align-self", "center");
        layout.setWidth("75%");
        addInput(layout);
        Button send = new Button("Send");
        layout.add(send);
        send.addClickListener(e -> sendAnswer());
        Button showResults = new Button("Show Results");
        showResults.addClickListener(e -> gotoResult());
        layout.add(showResults);
        add(layout);
        return layout;
    }

    private void sendAnswer() {
        if(currentQuestion==NUMBER_OF_QUESTIONS){
            gotoResult();
            return;
        }
        remove(currentInput);
        createAnswer(getLastAnswer());
        currentQuestion++;
        createQuestion(getNextQuestion());
        currentInput = createInput();
    }

    private void gotoResult() {
        ComponentUtil.setData(UI.getCurrent(), MovieFilter.class, filter);
        UI.getCurrent().navigate(MovieResultPresenter.class);
    }

    private String getLastAnswer() {
        switch (currentQuestion){
            case 0: return ObjUtil.toString(filter.getGenres());
            case 1: return ObjUtil.toString(filter.getRelationship());
            case 2: return ObjUtil.toString(filter.getNumberWatchers());
            case 3: return ObjUtil.toString(filter.getLanguage());
            case 4: return ObjUtil.toString(filter.getPlatforms());
            case 5: return ObjUtil.toString(filter.getAgeRestriction());
            case 6: return ObjUtil.toString(filter.getEmotionality());
            case 7: return ObjUtil.toString(filter.getInvested());
            case 8: return ObjUtil.toString(filter.getPositiveKeywords());
            case 9: return ObjUtil.toString(filter.getNegativeKeywords());
            default: throw  new IllegalStateException("No Answer defined for question " + currentQuestion);
        }
    }

    private String getNextQuestion() {
        switch (currentQuestion){
            case 0: return "What Genre would you like to watch?";
            case 1: return "Who are you watching the Movie with?";
            case 2: return "How many people will be watching?";
            case 3: return "What Language should the movie be in?";
            case 4: return "On which platforms are you able to watch the movie?";
            case 5: return "Are there any Age Restrictions to consider?";
            case 6: return "How emotional should the movie be?";
            case 7: return "How invested do you want to be in the movie?";
            case 8: return "Are there any keywords the movie should contain?";
            case 9: return "Are there any keywords you'd like to avoid?";
            default: throw  new IllegalStateException("No Question defined for question " + currentQuestion);
        }
    }

    private void addInput(HLayout layout) {
        switch (currentQuestion){
            case 0: layout.addMultiSelect(filter::setGenres, Genre.GenreType.values()); return;
            case 1: layout.addSelect("", filter::setRelationship, Relationship.values()); return;
            case 2: layout.addIntegerField("", filter::setNumberWatchers, 1, true, 1, 9999, 1); return;
            case 3: layout.addSelect("", filter::setLanguage, Language.values()); return;
            case 4: layout.addMultiSelect(filter::setPlatforms, Platform.values()); return;
            case 5: layout.addSelect("", filter::setAgeRestriction, AgeRestriction.values()); return;
            case 6: layout.addSlider(filter::setEmotionality); return;
            case 7: layout.addSlider(filter::setInvested); return;
            case 8: layout.addMultiSelect(filter::setPositiveKeywords, Keyword.KeywordValue.values()); return;
            case 9: layout.addMultiSelect(filter::setNegativeKeywords, Keyword.KeywordValue.values()); return;
            default: throw new IllegalStateException("No Inputmethod defined for question " + currentQuestion);
        }
    }

}
