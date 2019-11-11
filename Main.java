package flashcards;

import java.util.*;

public class Main {

	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		boolean isExit = false;
		String action1 = "";
		String file1 = "";
		String action2 = "";
		String file2 = "";


		if (args.length == 2) {
			action1 = args[0];
			file1 = args[1];
		}

		if (args.length == 4) {
			action1 = args[0];
			file1 = args[1];
			action2 = args[2];
			file2 = args[3];
		}
		if (action1.equals("-import")) {
			Flashcard.importCards(file1);
		} else if (action2.equals("-import")) {
			Flashcard.importCards(file2);
		}

		do {
			System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
			String action = scanner.nextLine();
			Flashcard.getLog().add(action);
			switch (action.toLowerCase().trim()) {
				case "add":
					Flashcard.add();
					break;
				case "remove":
					Flashcard.remove();
					break;
				case "import":
					Flashcard.importCards();
					break;
				case "export":
					Flashcard.exportCards();
					break;
				case "ask":
					Flashcard.ask();
					break;
				case "log":
					Flashcard.log();
					break;
				case "hardest card":
					Flashcard.hardestCard();
					break;
				case "reset stats":
					Flashcard.resetStats();
					break;
				case "exit":
					isExit = true;
					if (action1.equals("-export")) {
						Flashcard.exportCards(file1);
					} else if (action2.equals("-import")) {
						Flashcard.exportCards(file2);
					}
					System.out.println("Bye bye!");
			}
		} while (!isExit);

	}


} 
