package ch.fuzzy.movie_suggester.server;

import ch.fuzzy.movie_suggester.util.ObjUtil;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String description;

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name="movie_platform")
	@Column(name="platform")
	private Set<Platform> platforms = new HashSet<>();

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name="movie_languages")
	@Column(name="language")
	private Set<Language> languages = new HashSet<>();

	@Column(name = "FAMILY_FIT")
	private Integer familyFit;
	@Column(name = "FRIENDS_FIT")
	private Integer friendsFit;
	@Column(name = "ROMANTIC_FIT")
	private Integer romanticFit;

	@Column(name = "LOW_CONCENTRATION_FIT")
	private Integer lowConcentrationFit;
	@Column(name = "MODERATE_CONCENTRATION_FIT")
	private Integer moderateConcentrationFit;
	@Column(name = "HARD_CONCENTRATION_FIT")
	private Integer hardConcentrationFit;

	@Enumerated(EnumType.STRING)
	@Column(name = "AGE_RESTRICTION")
	private AgeRestriction ageRestriction;

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval = true, mappedBy = "movie",fetch = FetchType.EAGER)
	private Set<Keyword> keywords = new HashSet<>();

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval = true, mappedBy = "movie",fetch = FetchType.EAGER)
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

	public Set<Platform> getPlatforms() {return platforms;}
	public void setPlatforms(Set<Platform> platforms) {this.platforms = platforms;}

	public Set<Language> getLanguages() {return languages;}
	public void setLanguages(Set<Language> languages) {this.languages = languages;}

	public AgeRestriction getAgeRestriction() {return ageRestriction;}
	public void setAgeRestriction(AgeRestriction ageRestriction) {this.ageRestriction = ageRestriction;}

	public Set<Keyword> getKeywords() {return keywords;}
	public Keyword createKeyword() { Keyword k = new Keyword(this, null); keywords.add(k); return k;}
	public void removeKeyword(Keyword k) { keywords.remove(k);}

	public Integer getLowConcentrationFit() {return lowConcentrationFit;}
	public void setLowConcentrationFit(Integer lowConcentrationFit) {
		if(lowConcentrationFit != null && lowConcentrationFit > 0 && lowConcentrationFit < 100){
			this.lowConcentrationFit = lowConcentrationFit;
		} else this.lowConcentrationFit = null;
	}

	public Integer getModerateConcentrationFit() {return moderateConcentrationFit;}
	public void setModerateConcentrationFit(Integer moderateConcentrationFit) {
		if(moderateConcentrationFit != null && moderateConcentrationFit > 0 && moderateConcentrationFit < 100){
			this.moderateConcentrationFit = moderateConcentrationFit;
		} else this.moderateConcentrationFit = null;
	}

	public Integer getHardConcentrationFit() {return hardConcentrationFit;}
	public void setHardConcentrationFit(Integer hardConcentrationFit) {
		if(hardConcentrationFit != null && hardConcentrationFit > 0 && hardConcentrationFit < 100){
			this.hardConcentrationFit = hardConcentrationFit;
		} else this.hardConcentrationFit = null;
	}

	public Set<Genre> getGenres() {return genres;}
	public void setGenres(Set<Genre> genres) {this.genres = genres;}
	public Genre createGenre() { Genre g = new Genre(this, null); genres.add(g); return g; }
	public void removeGenre(Genre genre){ genres.remove(genre); }

	public void setKeywords(Set<Keyword> keywords) {this.keywords = keywords;}

	@Override public String toString() { return String.format("Movie[id=%d, title='%s']", id, title); }

	public static String showGenres(Movie movie) {
		StringBuilder sb = new StringBuilder();
		movie.genres.stream().forEach(g -> sb.append(g.getName()).append(", "));
		return sb.length() > 0 ? sb.substring(0, sb.length()-2): "";
	}

    public int calculateGenreFit(Genre.GenreType genre) {
		for(Genre g: genres){
			if(g.getType() == genre){
				return g.getFit();
			}
		}
		return 0;
    }

	public int getConcentrationFit(MovieFinder.Concentration concentration) {
		switch (concentration){
			case LOW: return getLowConcentrationFit() != null ? getLowConcentrationFit() : 0;
			case MODERATE: return getModerateConcentrationFit() != null ? getModerateConcentrationFit() : 0;
			case HARD: return getHardConcentrationFit() != null ? getHardConcentrationFit() : 0;
			default: throw new IllegalArgumentException("No Fit found for Concentration " + concentration);
		}
	}

	public boolean hasGenre(Genre.GenreType g) {
		return ObjUtil.isContained(g, getGenres().stream().map(Genre::getType).toArray(Genre.GenreType[]::new));
	}

	public boolean hasKeyword(Keyword.KeywordValue k) {
		return ObjUtil.isContained(k, getKeywords().stream().map(Keyword::getKeyword).toArray(Keyword.KeywordValue[]::new));
	}
}
