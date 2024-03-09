package app;

import app.audio.Collections.Album;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.user.Artist;
import app.user.Host;
import app.user.User;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


/**
 * The type Admin.
 */
public final class Admin {
    @Getter
    private static List<User> users = new ArrayList<>();
    @Getter
    private static List<Artist> artists = new ArrayList<>();
    @Getter
    private static List<Host> hosts = new ArrayList<>();
    @Getter
    private static List<Song> songs = new ArrayList<>();
    private static List<Podcast> podcasts = new ArrayList<>();
    private static int timestamp = 0;
    private static final int LIMIT = 5;
    public static String playing;
    public static boolean repeatStatus;

    private Admin() {
    }

    /**
     * Sets users.
     *
     * @param userInputList the user input list
     */
    public static void setUsers(final List<UserInput> userInputList) {
        users = new ArrayList<>();
        for (UserInput userInput : userInputList) {
            users.add(new User(userInput.getUsername(),
                    userInput.getAge(), userInput.getCity(), "normal"));
        }
    }

    /**
     * Sets songs.
     *
     * @param songInputList the song input list
     */
    public static void setSongs(final List<SongInput> songInputList) {
        songs = new ArrayList<>();
        for (SongInput songInput : songInputList) {
            songs.add(new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist()));
        }
    }


    /**
     * Sets podcasts.
     *
     * @param podcastInputList the podcast input list
     */
    public static void setPodcasts(final List<PodcastInput> podcastInputList) {
        podcasts = new ArrayList<>();
        for (PodcastInput podcastInput : podcastInputList) {
            List<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episodeInput : podcastInput.getEpisodes()) {
                episodes.add(new Episode(episodeInput.getName(),
                                         episodeInput.getDuration(),
                                         episodeInput.getDescription()));
            }
            podcasts.add(new Podcast(podcastInput.getName(), podcastInput.getOwner(), episodes));
        }
    }

    /**
     * Gets podcasts.
     *
     * @return the podcasts
     */
    public static List<Podcast> getPodcasts() {
        return new ArrayList<>(podcasts);
    }

    /**
     * Gets playlists.
     *
     * @return the playlists
     */
    public static List<Playlist> getPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        for (User user : users) {
            playlists.addAll(user.getPlaylists());
        }
        return playlists;
    }

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     */
    public static User getUser(final String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Update timestamp.
     *
     * @param newTimestamp the new timestamp
     */
    public static void updateTimestamp(final int newTimestamp) {
        int elapsed = newTimestamp - timestamp;
        timestamp = newTimestamp;
        if (elapsed == 0) {
            return;
        }

        for (User user : users) {
            user.simulateTime(elapsed);
        }
    }

    /**
     * Gets top 5 songs.
     *
     * @return the top 5 songs
     */
    public static List<String> getTop5Songs() {
        List<Song> sortedSongs = new ArrayList<>(songs);
        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        List<String> topSongs = new ArrayList<>();
        int count = 0;
        for (Song song : sortedSongs) {
            if (count >= LIMIT) {
                break;
            }
            topSongs.add(song.getName());
            count++;
        }
        return topSongs;
    }

    /**
     * Gets top 5 playlists.
     *
     * @return the top 5 playlists
     */
    public static List<String> getTop5Playlists() {
        List<Playlist> sortedPlaylists = new ArrayList<>(getPlaylists());
        sortedPlaylists.sort(Comparator.comparingInt(Playlist::getFollowers)
                .reversed()
                .thenComparing(Playlist::getTimestamp, Comparator.naturalOrder()));
        List<String> topPlaylists = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : sortedPlaylists) {
            if (count >= LIMIT) {
                break;
            }
            topPlaylists.add(playlist.getName());
            count++;
        }
        return topPlaylists;
    }

    /**
     * Gets the top5 albums.
     *
     * @return the top5 albums.
     */
    public static List<String> getTop5Albums() {
        List<Album> sortedAlbums = new ArrayList<>(Admin.getAlbums());
        sortedAlbums.sort(Comparator.comparingInt(
                Album::getTotalLikes).reversed().thenComparing(Album::getName));

        List<String> topAlbums = new ArrayList<>();
        int count = 0;
        for (Album album : sortedAlbums) {
            if (count >= LIMIT) {
                break;
            }
            topAlbums.add(album.getName());
            count++;
        }

        return topAlbums;
    }

    /**
     * Gets top5 artists
     *
     * @return the top5 artists.
     */
    public static List<String> getTop5Artists() {
        List<Artist> sortedArtists = new ArrayList<>(Admin.getArtists());
        sortedArtists.sort(Comparator.comparingInt(artist -> artist.getTotalLikes() * -1));

        List<String> topArtists= new ArrayList<>();

        int count = 0;
        for (Artist artist : sortedArtists) {
            if (count >= LIMIT) {
                break;
            }
            topArtists.add(artist.getUsername());
            count++;
        }

        return topArtists;
    }


    /**
     * Reset.
     */
    public static void reset() {
        users = new ArrayList<>();
        songs = new ArrayList<>();
        podcasts = new ArrayList<>();
        artists = new ArrayList<>();
        hosts = new ArrayList<>();
        timestamp = 0;
        repeatStatus = false;
    }

    /**
     * Adds a user.
     *
     * @param userInput the user input
     * @param type      the type of user to be added
     * @return A message indicating the success or failure of the user addition.
     */
    public static String addUser(final UserInput userInput, final String type) {
        if (Admin.getUser(userInput.getUsername()) != null) {
            return "The username " + userInput.getUsername() + " is already taken.";
        }

        User user = new User(userInput.getUsername(), userInput.getAge(),
                userInput.getCity(), type);
        users.add(user);
        if (type.equals("artist")) {
            Artist artist = new Artist(userInput.getUsername(),
                    userInput.getAge(), userInput.getCity(), type);
            artists.add(artist);
        }
        if (type.equals("host")) {
            Host host = new Host(userInput.getUsername(), userInput.getAge(),
                    userInput.getCity(), type);
            hosts.add(host);
        }
        return "The username " + userInput.getUsername() + " has been added successfully.";
    }

    /**
     * Deletes a user.
     *
     * @param username The username of the user to be deleted.
     * @return A message indicating the success or failure of the user deletion.
     */
    public static String deleteUser(final String username) {
        User user = getUser(username);
        if (user != null) {
            users.remove(user);
            if ("artist".equals(user.getType())) {
                Artist artist = getArtist(username);
                if (artist != null) {
                    List<Album> artistAlbums = artist.getAlbums();
                    artistAlbums.forEach(album -> {
                        List<Song> albumSongs = album.getSongs();
                        List<Song> songsToRemove = new ArrayList<>(albumSongs);
                        songs.removeAll(albumSongs);
                        for (User u : users) {
                            u.getlikedSongs().removeAll(songsToRemove);
                        }
                    });
                    artistAlbums.clear();
                    artists.remove(artist);
                }
            }
            for (User newUser : users) {
                Iterator<Playlist> iterator = newUser.getFollowedPlaylists().iterator();
                while (iterator.hasNext()) {
                    Playlist playlist = iterator.next();
                    for (Playlist playlist1 : user.getPlaylists()) {
                        if (playlist1.getName().equals(playlist.getName())) {
                            playlist1.decreaseFollowers();
                            iterator.remove();
                        }
                    }
                }
            }
            for (Playlist playlist: getPlaylists()) {
                for (Playlist followedPlaylist : user.getFollowedPlaylists()) {
                    if (followedPlaylist.getName().equals(playlist.getName())) {
                        playlist.decreaseFollowers();
                    }
                }
            }

            for (User newUser: users) {
                for (Song song : newUser.getlikedSongs()) {
                        for (Song song1: user.getlikedSongs()) {
                            if (song1.equals(song)) {
                                newUser.getlikedSongs().remove(song);
                            }
                        }
                }
            }
            user.setPageOwner(null);
            return user.getUsername() + " was successfully deleted.";
        } else {
            return "User not found.";
        }
    }

    /**
     * Adds a podcast to the global list of podcasts.
     *
     * @param podcast The podcast to be added.
     */
    public static void addPodcast(final Podcast podcast) {
        podcasts.add(podcast);
    }

    /**
     * Removes a podcast from the global list of podcasts.
     *
     * @param podcast The podcast to be removed.
     */
    public static void removePodcast(final Podcast podcast) {
        podcasts.remove(podcast);
    }

    /**
     * Gets an artist based on the provided artist name.
     *
     * @param artistName The username of the artist.
     * @return The artist.
     */
    public static Artist getArtist(final String artistName) {
        for (Artist artist: artists) {
            if (artist.getUsername().equals(artistName)) {
                return artist;
            }
        }
        return null;
    }

    /**
     * Gets a host based on the provided host name.
     *
     * @param hostName The username of the host.
     * @return The host.
     */
    public static Host getHost(final String hostName) {
        for (Host host :hosts) {
            if (host.getUsername().equals(hostName)) {
                return host;
            }
        }
        return null;
    }

    /**
     * Gets the albums.
     *
     * @return The albums.
     */
    public static List<Album> getAlbums() {
        List<Album> albums = new ArrayList<>();
        for (Artist artist:artists) {
            albums.addAll(artist.getAlbums());
        }
        return albums;
    }

    /**
     * Gets all users.
     *
     * @return The users.
     */
    public static List<String> getAllUsers() {
        List<String> allUsers = new ArrayList<>();

        for (User user : users) {
            if ("user".equals(user.getType()) || "normal".equals(user.getType())) {
                allUsers.add(user.getUsername());
            }
        }

        for (User user : users) {
            if ("artist".equals(user.getType())) {
                allUsers.add(user.getUsername());
            }
        }
        for (User user : users) {
            if ("host".equals(user.getType()) || user.getType().isEmpty()) {
                allUsers.add(user.getUsername());
            }
        }
        return allUsers;
    }

}
