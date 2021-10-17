package ch.fuzzy.movie_suggester.server;

import com.vaadin.flow.internal.StringUtil;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String description;
	@ElementCollection
	private Set<String> keywords = new HashSet<>();
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinTable(
			name = "Movie_Genre",
			joinColumns = { @JoinColumn(name = "movie_id") },
			inverseJoinColumns = { @JoinColumn(name = "genre_id") }
	)
	private Set<Genre> genres = new HashSet<>();

	protected Movie() {}

	public Movie(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {return title;}
	public void setTitle(String title) {this.title = title;}

	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}

	public Set<String> getKeywords() {return keywords;}

	public Set<Genre> getGenres() {return genres;}
	public void addGenre(Genre.GenreType genre){
		genres.add(new Genre(genre)); //TODO: Load existing Genres and create link instead of creating new one each time
	}
	public void removeGenre(Genre.GenreType genre){ genres.remove(genres.stream().filter(g -> g.getType() == genre).findFirst().get()); }

	@Override
	public String toString() {
		return String.format("Movie[id=%d, title='%s']", id,
				title);
	}

	public static String showGenres(Movie movie) {
		StringBuilder sb = new StringBuilder();
		movie.genres.stream().forEach(g -> sb.append(g.getName()).append(", "));
		return sb.toString().substring(0, sb.length()-2);
	}
}
