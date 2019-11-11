package flashcards;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Flashcard {

	private String cardName;

	private String definition;

	private Integer mistakes;

	private static List<Flashcard> flashcards = new ArrayList<>();

	private static List<String> log = new ArrayList<>();

	private static Map<String, String> cardToDefinition = new LinkedHashMap<>();

	private static Map<String, String> definitionToCard = new LinkedHashMap<>();

	private static Map<String, Integer> stats = new HashMap<>();

	private Flashcard(String cardName, String definition) {

		this.cardName = cardName;
		this.definition = definition;
	}

	private Flashcard(String cardName, String definition, Integer mistakes) {

		this.cardName = cardName;
		this.definition = definition;
		this.mistakes = mistakes;
	}

	private String getCardName() {

		return cardName;
	}

	public void setCardName(String cardName) {

		this.cardName = cardName;
	}

	private String getDefinition() {

		return definition;
	}

	private void setDefinition(String definition) {

		this.definition = definition;
	}

	private Integer getMistakes() {

		return mistakes;
	}

	public void setMistakes(Integer mistakes) {

		this.mistakes = mistakes;
	}

	public List<Flashcard> getFlashcards() {

		return flashcards;
	}

	public void setFlashcards(List<Flashcard> flashcards) {

		Flashcard.flashcards = flashcards;
	}

	static List<String> getLog() {

		return log;
	}

	public static void setLog(List<String> log) {

		Flashcard.log = log;
	}

	static void add() {

		String card;
		String definition;


		System.out.print("The card:\n");
		card = Main.scanner.nextLine();
		log.add(card);
		if (cardToDefinition.containsKey(card)) {
			System.out.print("Card exists. Replace definition ");
			System.out.print("The definition of the card :\n");
			definition = Main.scanner.nextLine();
			log.add(definition);

			System.out.printf("Definition for card: \"%s\" replaced for: \"%s\". \n", card,
					definition);
		} else {
			System.out.print("The definition of the card :\n");
			definition = Main.scanner.nextLine();
			log.add(definition);
			cardToDefinition.put(card, definition);
			definitionToCard.put(definition, card);
			flashcards.add(new Flashcard(card, definition));
			System.out.printf("The pair (\"%s\":\"%s\") is added. \n", card, definition);
		}
	}

	static void remove() {

		System.out.print("The card:\n");
		String card = Main.scanner.nextLine();
		log.add(card);

		if (cardToDefinition.containsKey(card)) {
			String definition = cardToDefinition.get(card);
			cardToDefinition.remove(card);
			definitionToCard.remove(definition);

			flashcards.remove(new Flashcard(card, definition));
			System.out.println("The " + card + " successfully removed");
		} else {
			System.out.printf("Can't remove \"%s\": there is no such card.\n", card);
		}
	}

	static void importCards() {

		System.out.println("File name: ");
		String file = Main.scanner.nextLine();
		log.add(file);

		importCards(file);
	}

	static void importCards(String file) {
		String card;
		String definition;
		String mistakeString;
		Integer mistake = null;
		int count = 0;

		try (Scanner scanner = new Scanner(new File(file))) {
			while (scanner.hasNextLine()) {
				card = scanner.nextLine();
				definition = scanner.nextLine();
				mistakeString = scanner.nextLine();
				if (!mistakeString.equals("null")) {
					mistake = Integer.valueOf(mistakeString);
				}

				cardToDefinition.put(card, definition);
				definitionToCard.put(definition, card);
				flashcards.add(new Flashcard(card, definition, mistake));
				stats.put(card, mistake);
				count++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("No file found: " + file);
		}

		System.out.printf("%s cards have been loaded.\n", count);
	}

	static void exportCards() {

		System.out.println("File name: ");
		String file = Main.scanner.nextLine();
		log.add(file);

		exportCards(file);
	}

	static void exportCards(String file) {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file));
		     PrintWriter print = new PrintWriter(writer)) {
			for (Flashcard flashcard : flashcards) {
				print.printf("%s\n%s\n%s\n", flashcard.getCardName(), flashcard.getDefinition(), flashcard.getMistakes());
			}
		} catch (IOException e) {
			System.out.print("0 cards have been saved.\n");
			return;
		}
		System.out.printf("%d cards have been saved.\n", cardToDefinition.size());
	}

	static void ask() {

		Random random = new Random(1000);
		int number;
		Flashcard flashcard;
		System.out.println("How many times to ask?");
		int n = Integer.parseInt(Main.scanner.nextLine());
		log.add(String.valueOf(n));
		if (n <= 0) {
			return;
		}

		for (int i = 0; i < n; i++) {
			number = random.nextInt(flashcards.size());
			flashcard = flashcards.get(number);
			System.out.printf("Print the definition of \"%s\":\n", flashcard.getCardName());
			String guess = Main.scanner.nextLine();
			log.add(guess);
			if (cardToDefinition.get(flashcard.getCardName()).equals(guess)) {
				System.out.print("Correct answer. ");
			} else if (definitionToCard.containsKey(guess)) {

				System.out.printf(
						"Wrong answer (the correct one is \"%s\", you've just written a definition of \"%s\" card). ",
						cardToDefinition.get(flashcard.getCardName()), definitionToCard.get(guess));
				stats.merge(flashcard.getCardName(), 1, Integer::sum);
			} else {


				System.out
						.printf("Wrong answer (the correct one is \"%s\").", cardToDefinition.get(flashcard.getCardName()));
				stats.merge(flashcard.getCardName(), 1, Integer::sum);
			}
		}
		System.out.println();
	}

	static void log() {

		System.out.println("File name:");
		String fileName = Main.scanner.nextLine();
		log.add(fileName);
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));
		     PrintWriter print = new PrintWriter(writer)) {
			for (String entry : log) {
				print.printf("%s\n", entry);
			}
			log.clear();
		} catch (IOException e) {
			System.out.println("The log has not been saved.");
			return;
		}
		System.out.println("The log has been saved..");

	}

	static void hardestCard() {

		Iterator<Map.Entry<String, Integer>> iterator = stats.entrySet().iterator();
		Map.Entry<String, Integer> maxErrors = iterator.hasNext() ? iterator.next() : null;
		if (maxErrors == null) {
			System.out.print("No stats\n");
			return;
		}
		while (iterator.hasNext()) {
			Map.Entry<String, Integer> cur = iterator.next();
			if (cur.getValue() > maxErrors.getValue()) {
				maxErrors = cur;
			}
		}
		System.out.printf("The hardest card is \"%s\". You have %d errors answering it.\n",
				maxErrors.getKey(), maxErrors.getValue());

	}

	static void resetStats() {

		stats.clear();
		System.out.print("Stats reset\n");
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Flashcard flashcard = (Flashcard) o;
		return Objects.equals(cardName, flashcard.cardName);
	}

	@Override
	public int hashCode() {

		return Objects.hash(cardName);
	}
}
