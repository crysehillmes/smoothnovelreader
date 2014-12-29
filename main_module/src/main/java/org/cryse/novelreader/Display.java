package org.cryse.novelreader;

import android.os.Bundle;

import org.cryse.novelreader.model.ColorScheme;

public interface Display {

    public static final String FRAGMENT_TAG_RATE_MOVIE = "rate_movie";
    public static final String FRAGMENT_TAG_CHECKIN_MOVIE = "checkin_movie";
    public static final String FRAGMENT_TAG_TRAKT_CREDENTIALS_WRONG = "trakt_credentials_wrong";

    public static final String PARAM_ID = "_id";

    public void showLibrary();

    public void showTrending();

    public void showDiscover();

    public void showWatchlist();

    public void showLogin();

    public void startMovieDetailActivity(String movieId, Bundle bundle);

    public void showMovieDetailFragment(String movieId);

    public void startMovieImagesActivity(String movieId);

    public void showMovieImagesFragment(String movieId);

    public void showSearchFragment();

    public void showSearchMoviesFragment();

    public void showSearchPeopleFragment();

    public void showAboutFragment();

    public void showLicencesFragment();

    public void showRateMovieFragment(String movieId);

    public void closeDrawerLayout();

    public boolean hasMainFragment();

    public void startAddAccountActivity();

    public void startAboutActivity();

    public void setActionBarTitle(CharSequence title);

    public void setActionBarSubtitle(CharSequence title);

    public boolean popEntireFragmentBackStack();

    public void showUpNavigation(boolean show);

    public void finishActivity();

    public void showSettings();

    public void showRelatedMovies(String movieId);

    public void showCastList(String movieId);

    public void showCrewList(String movieId);

    public void showCheckin(String movieId);

    public void showCancelCheckin();

    public void startPersonDetailActivity(String id, Bundle bundle);

    public void showPersonDetail(String id);

    public void showPersonCastCredits(String id);

    public void showPersonCrewCredits(String id);

    public void showCredentialsChanged();

    public void playYoutubeVideo(String id);

    public void setColorScheme(ColorScheme colorScheme);

}
