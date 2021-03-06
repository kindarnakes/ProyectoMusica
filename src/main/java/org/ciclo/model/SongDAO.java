package org.ciclo.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongDAO extends Song {


    /**
     * Constructor
     */
    public SongDAO() {
        super();
    }

    /**
     * Parametrized constructor
     *
     * @param s song to update
     */

    public SongDAO(Song s) {
        this.setId(s.getId());
        this.setDuration(s.getDuration());
        this.setName(s.getName());
        this.setGenre(null);
        this.setDisc(s.getDisc()); //Pointer to same object
        this.setList(s.getLists()); //Pointer to same object
        this.setReproductions(null);
    }

    /**
     * Constructor
     *
     * @param id of the Song
     */

    public SongDAO(Integer id) {
        this(SongDAO.listById(id));
    }

    /**
     * List all the songs
     *
     * @return All the songs
     */

    public static List<Song> listAll() {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT id, nombre, duracion, id_disco FROM cancion";
        try (
                Connection conn = org.ciclo.model.connect.Connection.getConnect();
                PreparedStatement st = conn.prepareStatement(sql);
                ResultSet rs = st.executeQuery()
        ) {
            List<Disc> discs = DiscDAO.listAll();
            while (rs != null && rs.next()) {
                Song aux = new Song();
                aux.setId(rs.getInt("id"));
                aux.setName(rs.getString("nombre"));
                aux.setDuration(rs.getInt("duracion"));
                boolean find = false;
                int index = 0;
                for (int i = 0; i < discs.size() && !find; i++) {
                    if (discs.get(i).getId() == rs.getInt("id_disco")) {
                        find = true;
                        index = i;
                    }
                }
                aux.setDisc(discs.get(index));
                songs.add(aux);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return songs;
    }

    /**
     * List the song with that id
     *
     * @param id unique for all the song
     * @return The song with that id
     */

    public static Song listById(Integer id) {
        Song song = new Song();
        String sql = "SELECT id, nombre, duracion, id_disco FROM cancion WHERE id = ?"; //cambiar
        try (
                Connection conn = org.ciclo.model.connect.Connection.getConnect();
                PreparedStatement st = conn.prepareStatement(sql)
        ) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Disc disc = DiscDAO.listById(rs.getInt("id_disco"));
                Song aux = new Song();
                aux.setId(rs.getInt("id"));
                aux.setName(rs.getString("nombre"));
                aux.setDuration(rs.getInt("duracion"));
                aux.setDisc(disc);
                song = aux;
            }

            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return song;
    }

    /**
     * List all the song with that name
     *
     * @param name the name of the song
     * @return The songs with that name
     */

    public static List<Song> listByName(String name) {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT c.id, c.nombre, c.duracion, d.id, d.nombre, d.foto, d.fecha_publicacion, a.id, a.nombre, a.foto, a.nacionalidad " +
                "FROM cancion AS c LEFT JOIN disco AS d ON c.id_disco = d.id LEFT JOIN artista AS a ON a.id=d.id_artista " +
                "WHERE c.nombre = ?";
        try (
                Connection conn = org.ciclo.model.connect.Connection.getConnect();
                PreparedStatement st = conn.prepareStatement(sql)
        ) {
            st.setString(1, name);
            ResultSet rs = st.executeQuery();
            while (rs != null && rs.next()) {
                Disc disc = new Disc();
                Artist artist = new Artist();
                Song aux = new Song();
                aux.setId(rs.getInt("c.id"));
                aux.setName(rs.getString("c.nombre"));
                aux.setDuration(rs.getInt("c.duracion"));
                disc.setId(rs.getInt("d.id"));
                disc.setName(rs.getString("d.nombre"));
                disc.setPhoto(rs.getString("d.foto"));
                disc.setReleaseDate(rs.getDate("d.fecha_publicacion").toLocalDate());
                artist.setId(rs.getInt("a.id"));
                artist.setName(rs.getString("a.nombre"));
                artist.setNationality(rs.getString("a.nacionalidad"));
                disc.setArtist(artist);
                aux.setDisc(disc);
                songs.add(aux);
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return songs;
    }

    /**
     * Update a song
     *
     * @return true if the song has been updated, false if not
     */

    public boolean update() {
        boolean update = false;
        String sql = "UPDATE cancion SET nombre = ?, duracion = ?, id_disco = ? WHERE id = ?";
        try (
                Connection conn = org.ciclo.model.connect.Connection.getConnect();
                PreparedStatement st = conn.prepareStatement(sql)
        ) {
            st.setString(1, this.getName());
            st.setInt(2, this.getDuration());
            st.setInt(3, this.getDisc().getId());
            st.setInt(4, this.getId());
            int i = st.executeUpdate();
            if (i > 1) {
                update = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


        return update;
    }

    /**
     * Save and insert a somg
     *
     * @return true if the song has been inserted, false if not
     */

    public boolean save() {
        boolean saved = false;

        String sql = "INSERT INTO cancion(nombre, duracion, id_disco) VALUES (?, ?, ?)";
        try (
                Connection conn = org.ciclo.model.connect.Connection.getConnect()
        ) {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, this.getName());
            st.setInt(2, this.getDuration());
            st.setInt(3, this.getDisc().getId());
            int i = st.executeUpdate();
            if (i > 1) {
                saved = true;
            }

            sql = "SElECT id FROM cancion WHERE nombre = ? ORDER BY id DESC LIMIT 1";
            st = conn.prepareStatement(sql);
            st.setString(1, this.getName());
            ResultSet rs = st.executeQuery();
            if (rs != null && rs.next()) {
                this.setId(rs.getInt("id"));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return saved;
    }

    /**
     * Remove a song with that id
     *
     * @param id unique for all the song
     * @return true if the song has been removed, false if not
     */

    public static boolean remove(Integer id) {
        boolean removed = false;
        String sql = "DELETE FROM cancion WHERE id = ?";
        try (
                Connection conn = org.ciclo.model.connect.Connection.getConnect();
                PreparedStatement st = conn.prepareStatement(sql)
        ) {
            st.setInt(1, id);
            int i = st.executeUpdate();
            if (i > 1) {
                removed = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return removed;
    }

    /**
     * Remove the song
     *
     * @return true if the song has been removed, false if not
     */

    public boolean remove() {
        boolean removed = false;
        String sql = "DELETE FROM cancion WHERE id = ?";
        try (
                Connection conn = org.ciclo.model.connect.Connection.getConnect();
                PreparedStatement st = conn.prepareStatement(sql)
        ) {
            st.setInt(1, this.getId());
            int i = st.executeUpdate();
            if (i > 1) {
                removed = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return removed;
    }

    /**
     * List all the song in that disc
     *
     * @param disc the disc to insert the song
     * @return true if the song has been inserted in the disc, false if not
     */

    public static List<Song> searchByDisc(Disc disc) {
        List<Song> songs = new ArrayList<>();
        Integer id_disc = disc.getId();

        String sql = "SELECT id, nombre, duracion FROM cancion WHERE id_disco = ?";
        try (
                Connection conn = org.ciclo.model.connect.Connection.getConnect();
                PreparedStatement st = conn.prepareStatement(sql)
        ) {
            st.setInt(1, id_disc);
            ResultSet rs = st.executeQuery();
            while (rs != null && rs.next()) {
                Song aux = new Song();
                aux.setId(rs.getInt("id"));
                aux.setName(rs.getString("nombre"));
                aux.setDuration(rs.getInt("duracion"));
                aux.setDisc(disc);
                songs.add(aux);
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return songs;
    }

    /**
     * List all song in a playlist
     *
     * @param playlist the playlist that contains the discs
     * @return The list of the song in a playlist
     */

    public static List<Song> ListSongByPlaylist(Playlist playlist) {
        List<Song> songs = new ArrayList<>();
        Integer id_playlist = playlist.getId();

        String sql = "SELECT id, nombre, duracion, id_disco FROM cancion "
                + "WHERE id IN (SELECT id_cancion FROM lista_cancion WHERE id_lista=?)";
        try (
                Connection conn = org.ciclo.model.connect.Connection.getConnect();
                PreparedStatement st = conn.prepareStatement(sql)
        ) {
            st.setInt(1, id_playlist);
            ResultSet rs = st.executeQuery();
            List<Disc> discs = DiscDAO.listAll();
            while (rs != null && rs.next()) {
                Song aux = new Song();
                aux.setId(rs.getInt("id"));
                aux.setName(rs.getString("nombre"));
                aux.setDuration(rs.getInt("duracion"));
                boolean find = false;
                int index = 0;
                for (int i = 0; i < discs.size() && !find; i++) {
                    if (discs.get(i).getId() == rs.getInt("id_disco")) {
                        find = true;
                        index = i;
                    }
                }
                aux.setDisc(discs.get(index));
                songs.add(aux);
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return songs;
    }


}
