package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {
		private static final String url= "jdbc:mysql://localhost:3306/sdvid?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=US/Mountain";
		private static String user = "student";
		private static String pass = "student";
		private static Connection conn;

		public DatabaseAccessorObject() {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			}catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public Film findFilmById(int filmId) {
			if (filmId <= 0) {
				return null;
			}
			Film film = null;
			String sql = "SELECT film.* FROM film WHERE film.id = ?";

			try {
				conn = DriverManager.getConnection(url, user, pass);
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setInt(1, filmId);
				ResultSet filmResult = stmt.executeQuery();

				if (filmResult.next()) {
					film = new Film();

					film.setId(filmResult.getInt("id"));
					film.setTitle(filmResult.getString("title"));
					film.setDescription(filmResult.getString("description"));
					film.setReleaseYear(filmResult.getInt("release_year"));
					film.setLanguageId(filmResult.getInt("language_id"));
					film.setRentalDuration(filmResult.getDouble("rental_duration"));
					film.setRentalRate(filmResult.getDouble("rental_rate"));
					film.setLength(filmResult.getInt("length"));
					film.setReplacementCost(filmResult.getDouble("replacement_cost"));
					film.setRating(filmResult.getString("rating"));
					film.setSpecialFeatures(filmResult.getString("special_features"));
					film.setActorList(findActorsByFilmId(filmId));
					film.setLanguage(getLanguageOfFilm(filmId));

				}
				filmResult.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return film;
		}

		@Override
		public Actor findActorById(int actorId) {
			if (actorId <= 0) {
				return null;
			}
			String sql = "SELECT id, first_name, last_name FROM actor WHERE actor.id = ?";
			Actor actor = null;

			try {
				conn = DriverManager.getConnection(url, user, pass);
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setInt(1, actorId);
				ResultSet actorResult = stmt.executeQuery();
				if (actorResult.next()) {
					actor = new Actor();

					actor.setId(actorResult.getInt("id"));
					actor.setFirstName(actorResult.getString("first_name"));
					actor.setLastName(actorResult.getString("last_name"));
				//	actor.setFilms(findFilmByActorId(actorId)); // An Actor has Films
				}
				actorResult.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return actor;
		}

		@Override
		public List<Actor> findActorsByFilmId(int filmId) {
			if (filmId <= 0) {
				return null;
			}
			List<Actor> listOfActors = new ArrayList<>();
			String sql = "select actor.id,actor.first_name,actor.last_name from film join film_actor on film.id=film_actor.film_id join actor on film_actor.actor_id = actor.id where film.id=?";
			try {
				conn = DriverManager.getConnection(url, user, pass);

				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setInt(1, filmId);
				ResultSet rs = stmt.executeQuery();

				while (rs.next()) {
					int id = rs.getInt("id");
					String firstName = rs.getString("first_name");
					String lastName = rs.getString("last_name");

					Actor actor = new Actor(id, firstName, lastName);
					listOfActors.add(actor);
				}
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return listOfActors;
		}

		@Override
		public List<Film> findFilmByActorId(int actorId) {
			if (actorId <= 0) {
				return null;
			}
			List<Film> listOfFilms = new ArrayList<>();
			String sql = " select film.title from actor join film_actor on actor.id=film_actor.actor_id join film on film_actor.film_id=film.id where actor.id=?;";
			try {
				conn = DriverManager.getConnection(url, user, pass);

				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setInt(1, actorId);
				ResultSet rs = stmt.executeQuery();

				while (rs.next()) {
					String title = rs.getString("film.title");

					Film film = new Film(title);
					listOfFilms.add(film);
				}
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return listOfFilms;
		}

		@Override
		public List<Film> findFilmByKeywords(String keyword) {

			List<Film> listOfFilms = new ArrayList<>();
			String sql = "select film.* from film where film.title like ? or film.description like ? ;";
			Film film = null;

			try {
				conn = DriverManager.getConnection(url, user, pass);
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, "% " + keyword + " %");
				stmt.setString(2, "% " + keyword + " %");
				ResultSet filmResult = stmt.executeQuery();

				while (filmResult.next()) {
					film = new Film();

					film.setId(filmResult.getInt("id"));
					film.setTitle(filmResult.getString("title"));
					film.setDescription(filmResult.getString("description"));
					film.setReleaseYear(filmResult.getInt("release_year"));
					film.setLanguageId(filmResult.getInt("language_id"));
					film.setRentalDuration(filmResult.getDouble("rental_duration"));
					film.setRentalRate(filmResult.getDouble("rental_rate"));
					film.setLength(filmResult.getInt("length"));
					film.setReplacementCost(filmResult.getDouble("replacement_cost"));
					film.setRating(filmResult.getString("rating"));
					film.setSpecialFeatures(filmResult.getString("special_features"));
					film.setActorList(findActorsByFilmId(film.getId()));
					film.setLanguage(getLanguageOfFilm(film.getId()));
					
					listOfFilms.add(film);

				}
				filmResult.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return listOfFilms;
		}

		@Override
		public String getLanguageOfFilm(int filmId) {
			if (filmId <= 0) {
				return null;
			}

			String sql = "select language.name from film join language on film.language_id=language.id where film.id = ?";
			String language = "";
			try {
				 conn = DriverManager.getConnection(url, user, pass);

				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setInt(1, filmId);
				ResultSet rs = stmt.executeQuery();

				if (rs.next()) {
					language = rs.getString("language.name");

				}
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return language;
		}
}