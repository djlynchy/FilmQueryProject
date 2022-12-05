package com.skilldistillery.filmquery.app;

import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	DatabaseAccessor db = new DatabaseAccessorObject();

	public static void main(String[] args) {
		FilmQueryApp app = new FilmQueryApp();
//		app.test();
		app.launch();
	}

//	private void test() {
//		Film film = db.findFilmById(1);
//		System.out.println(film);
//	}

	private void launch() {
		Scanner input = new Scanner(System.in);

		startUserInterface(input);

		input.close();
	}
	private void startUserInterface(Scanner input) {
		boolean keepGoing = true;
		while (keepGoing) {
			displayMainMenu();

			int choice = input.nextInt();
			switch (choice) {
			case 1:
				chooseFilmId(input);
				break;

			case 2:
				chooseFilmByKeywords(input);
				break;
			case 3:
				System.out.println("That was FUN!");
				keepGoing = false;
				break;

			}

		}

	}

	private void displayMainMenu() {
		System.out.println();
		System.out.println("##~##~##~##~##~##~##~##~##~##~##~##");
		System.out.println("#~#         Film Query App      #~#");
		System.out.println("##     Choose between (1 - 3)    ##");
		System.out.println("##     1: Look up film by id     ##");
		System.out.println("##   2: Look up film by keyword  ##");
		System.out.println("##~#         3: Quit           #~##");
		System.out.println("##~##~##~##~##~##~##~##~##~##~##~##");
		System.out.print("	    Please choose: ");
	}

	private void chooseFilmId(Scanner input) {
		System.out.print("Enter the Film Id: ");
		try {
			int filmId = input.nextInt();
			Film film = db.findFilmById(filmId);
			if (film != null) {
				System.out.println(film);

			} else {
				System.err.println("Does not exist");
			}
		} catch (Exception e) {
			System.err.println("Invalid choice");
			input.next();
			startUserInterface(input);
		}

	}

	private void chooseFilmByKeywords(Scanner input) {
		System.out.print("Enter your keywords: ");
		try {
			String keywords = input.next();
			List<Film> filmList = db.findFilmByKeywords(keywords);
			if (filmList.size() > 0) {
				for (Film film : filmList) {
					System.out.println(film);

				}
			} else {
				System.err.println("Does not exist");
			}
		} catch (Exception e) {
			System.err.println("Invalid choice");
			input.next();
			startUserInterface(input);
		}

	}

}