package ch.fuzzy.movie_suggester;

import ch.fuzzy.movie_suggester.ui.MovieEditorPresenter;
import ch.fuzzy.movie_suggester.ui.MovieFilterPresenter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
@CssImport("./themes/page_style.css")
public class MainView extends VerticalLayout {
	public MainView() {
		Button movieEditBtn = new Button("Edit Movies");
		movieEditBtn.addClickListener(e -> UI.getCurrent().navigate(MovieEditorPresenter.class));
		add(movieEditBtn);
		add(new MovieFilterPresenter());
	}
}