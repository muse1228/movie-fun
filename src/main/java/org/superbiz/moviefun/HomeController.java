package org.superbiz.moviefun;

import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;

    private final PlatformTransactionManager moviesPlatformTransactionManager;
    private final PlatformTransactionManager albumsPlatformTransactionManager;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures,
                          @Qualifier("moviesPlatformTransactionManager") PlatformTransactionManager moviesPlatformTransactionManager,
                          @Qualifier("albumsPlatformTransactionManager") PlatformTransactionManager albumsPlatformTransactionManager) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.moviesPlatformTransactionManager = moviesPlatformTransactionManager;
        this.albumsPlatformTransactionManager = albumsPlatformTransactionManager;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        TransactionStatus moviesTransactionStatus = moviesPlatformTransactionManager.getTransaction(new DefaultTransactionDefinition());

        try{
            for (Movie movie : movieFixtures.load()) {
                moviesBean.addMovie(movie);
            }
            moviesPlatformTransactionManager.commit(moviesTransactionStatus);
        }catch(Exception ex){
            moviesPlatformTransactionManager.rollback(moviesTransactionStatus);
        }


        TransactionStatus albumsTransactionStatus = albumsPlatformTransactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            for (Album album : albumFixtures.load()) {
                albumsBean.addAlbum(album);
            }
            albumsPlatformTransactionManager.commit(albumsTransactionStatus);
        }catch(Exception ex){
            albumsPlatformTransactionManager.rollback(albumsTransactionStatus);
        }

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
