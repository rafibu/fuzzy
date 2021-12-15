package ch.fuzzy.movie_suggester;

import ch.fuzzy.movie_suggester.server.SettingsRepository;
import ch.fuzzy.movie_suggester.ui.MovieEditorPresenter;
import ch.fuzzy.movie_suggester.ui.MovieFilterBotPresenter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * First view a user sees
 * @author rbu
 */
@Route
@CssImport("./themes/page_style.css")
public class MainView extends VerticalLayout {
	public MainView(SettingsRepository settingsRepo) {
		Button movieEditBtn = new Button("Edit Movies");
		movieEditBtn.addClickListener(e -> UI.getCurrent().navigate(MovieEditorPresenter.class));
		add(movieEditBtn);
		add(new MovieFilterBotPresenter(settingsRepo));
	}
}