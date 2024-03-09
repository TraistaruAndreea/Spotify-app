package app.audio.Collections;

import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * The Album
 */
@Getter
public class Album extends AudioCollection {
    private final List<Song> songs;
    private final int releaseYear;
    private final String description;

    /**
     * Initiate a new Album
     *
     * @param name         The name of the album.
     * @param owner        The owner or artist of the album.
     * @param releaseYear  The release year of the album.
     * @param description  A description of the album.
     */
    public Album(final String name, final String owner,
                 final int releaseYear, final String description) {
        super(name, owner);
        this.releaseYear = releaseYear;
        this.songs = new ArrayList<>();
        this.description = description;
    }

    /**
     * Adds a song to the album.
     *
     * @param song The song to be added to the album.
     * @throws IllegalArgumentException If the song does not belong to this album.
     */
    public void addSong(final Song song) {
        if (song.getAlbum().equalsIgnoreCase(this.getName())
                && song.getArtist().equalsIgnoreCase(this.getOwner())) {
            songs.add(song);
        } else {
            throw new IllegalArgumentException("Song does not belong to this album");
        }
    }

    /**
     * Gets the names of all songs.
     *
     * @return A list of song names.
     */
    public List<String> getSongNames() {
        List<String> songNames = new ArrayList<>();
        for (Song song : songs) {
            songNames.add(song.getName());
        }
        return songNames;
    }

    /**
     * Gets the total number of tracks
     *
     * @return The total number of tracks
     */
    @Override
    public int getNumberOfTracks() {
        return songs.size();
    }

    /**
     * Gets an audio file (song) from the album based on its index.
     *
     * @param index The index of the track
     * @return The audio file (song) at the specified index.
     */
    @Override
    public AudioFile getTrackByIndex(final int index) {
        return songs.get(index);
    }

    /**
     * Gets the total number of likes for all songs
     *
     * @return The total number of likes for all songs
     */
    public int getTotalLikes() {
        int totalLikes = 0;
        for (Song song : songs) {
            totalLikes += song.getLikes();
        }
        return totalLikes;
    }


    public static int getListens(Album album) {
        int totalListens = 0;
        for (Song song: album.getSongs()) {
            totalListens += song.getListen();
        }
        return totalListens;
    }
}
