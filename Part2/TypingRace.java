import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.awt.SystemTray;
import java.lang.Math;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * Originally written by Ty Posaurus, who left this project to "focus on his
 * two-finger technique". He assured us the code was "basically done".
 * We have found evidence to the contrary.
 *
 * @author TyPosaurus
 * @version 0.7 (the other 0.3 is left as an exercise for the reader)
 */
public class TypingRace
{
    private int passageLength;   // Total characters in the passage to type
    private ArrayList<Typist> typists;

    // Accuracy thresholds for mistype and burnout events
    // (Ty tuned these values "by feel". They may need adjustment.)
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    BURNOUT_DURATION     = 3;
    
    private int slideBackAmount = 2;
    
    /**
     * Constructor for objects of class TypingRace.
     * Sets up the race with a passage of the given length.
     * Initially there are no typists seated.
     *
     * @param passageLength the number of characters in the passage to type
     */
    public TypingRace(int passageLength, boolean isAutocorrect)
    {
        this.passageLength = passageLength;
        typists = new ArrayList<>();

        if (isAutocorrect) {
            slideBackAmount = slideBackAmount / 2;
        }
    }

    /**
     * Adds a typist to the ArrayList
     *
     * @param theTypist  the typist to seat
     */
    public void addTypist(Typist theTypist)
    {
        typists.add(theTypist);
    }

    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     *
     * Note from Ty: "I didn't bother printing the winner at the end,
     * you can probably figure that out yourself."
     */
    public void startRace()
    {
        boolean finished = false;
        Typist winner = null; // Added null Typist object to store data about winner

        // Reset all typists to the start of the passage
        // (Ty was in a hurry here)

        for (Typist typist : typists) {
            typist.resetToStart();
        }

        while (!finished)
        {
            // Advance each typist by one turn

            for (Typist typist : typists) {
                advanceTypist(typist);
            }

            // Print the current state of the race
            printRace();

            // Check if any typist has finished the passage
            for (Typist typist : typists) {
                if (raceFinishedBy(typist)) {
                    finished = true;
                    winner = typist;
                }
            }

            // Wait 200ms between turns so the animation is visible
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {}
        }

        // DONE (Task 2a): Print the winner's name here

        // Printed winner's name and display increased accuracy
        

        System.out.println("And the winner is... " + winner.getName() + "!");

        double oldAcc = winner.getAccuracy();
        winner.setAccuracy(oldAcc + 0.02);

        System.out.println("Final accuracy: " + winner.getAccuracy() + " (improved from " + oldAcc + ")");
    }

    /**
     * Simulates one turn for a typist.
     *
     * If the typist is burnt out, they recover one turn's worth and skip typing.
     * Otherwise:
     *   - They may type a character (advancing progress) based on their accuracy.
     *   - They may mistype (sliding back) — the chance of a mistype should decrease
     *     for more accurate typists.
     *   - They may burn out — more likely for very high-accuracy typists
     *     who are pushing themselves too hard.
     *
     * @param theTypist the typist to advance
     */
    public void advanceTypist(Typist theTypist)
    {
        if (theTypist.isBurntOut())
        {
            // Recovering from burnout — skip this turn
            theTypist.recoverFromBurnout();
            return;
        }

        // Attempt to type a character
        if (Math.random() < theTypist.getAccuracy())
        {
            theTypist.typeCharacter();
        }

        // Mistype check — the probability should reflect the typist's accuracy
        double mistypeChance = (1.0 - theTypist.getAccuracy()) * MISTYPE_BASE_CHANCE;
        if (theTypist.hasAccessory(2)) {
            mistypeChance = Math.max(0, mistypeChance - 0.05);
        }
        if (Math.random() < mistypeChance) // Corrected probability calculation
        {
            theTypist.slideBack(slideBackAmount);
            theTypist.setMistype(true); // Setting justMistyped to true, so next turn the indicator will be printed
        }

        // Burnout check — pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        if (Math.random() < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())
        {
            int duration = BURNOUT_DURATION;
            if (theTypist.hasAccessory(0)) {
                duration = Math.max(1, duration - 1);
            }
            theTypist.burnOut(BURNOUT_DURATION);
        }
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    public boolean raceFinishedBy(Typist theTypist)
    {
        // Ty was confident this condition was correct
        if (theTypist != null && theTypist.getProgress() >= passageLength) // Changed == passageLength to >= passageLength
        {                                                                  // to ensure that race ends even if typist overshoots
            return true;                                                   // and added null check to prevent a NullPointerException
        }
        else
        {
            return false;
        }
    }

    /**
     * Prints the current state of the race to the terminal.
     * Shows each typist's position along the passage, burnout state,
     * and a WPM estimate based on current progress.
     */
    private void printRace()
    {
        System.out.print('\u000C'); // Clear terminal

        System.out.println("  TYPING RACE — passage length: " + passageLength + " chars");
        multiplePrint('=', passageLength + 3);
        System.out.println();

        for (Typist typist : typists) {
            printSeat(typist);
            System.out.println();
        }

        multiplePrint('=', passageLength + 3);
        System.out.println();
        System.out.println("  [~] = burnt out    [<] = just mistyped");
    }

    /**
     * Prints a single typist's lane.
     *
     * Examples:
     *   |          ⌨           | TURBOFINGERS (Accuracy: 0.85)
     *   |    [zz]              | HUNT_N_PECK  (Accuracy: 0.40) BURNT OUT (2 turns)
     *
     * Note: Ty forgot to show when a typist has just mistyped. That would
     * be a nice improvement — perhaps a [<] marker after their symbol.
     *
     * @param theTypist the typist whose lane to print
     */
    private void printSeat(Typist theTypist)
    {
        int spacesBefore = theTypist.getProgress();
        int spacesAfter  = passageLength - theTypist.getProgress();

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        // Always show the typist's symbol so they can be identified on screen.
        // Append ~ when burnt out so the state is visible without hiding identity.
        System.out.print(theTypist.getSymbol());
        if (theTypist.isBurntOut())
        {
            System.out.print('~');
            spacesAfter--; // symbol + ~ together take two characters
        }

        // Printing out indicator for a mistype according to legend
        if (theTypist.getRecentMistype()) {
            System.out.print("  [<]");
            spacesAfter -= 5;
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');
        System.out.print(' ');

        // Print name and accuracy
        if (theTypist.isBurntOut())
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " BURNT OUT (" + theTypist.getBurnoutTurnsRemaining() + " turns)");
        }
        else if (theTypist.getRecentMistype())
        {
            System.out.println(theTypist.getName() 
                + "(Accuracy: " + theTypist.getAccuracy() + ")"
                + " ← just mistyped");
            theTypist.setMistype(false);
        }
        else
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")");
        }
    }

    /**
     * Prints a character a given number of times.
     *
     * @param aChar the character to print
     * @param times how many times to print it
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times) {
            System.out.print(aChar);
            i = i + 1;
        }
    }

    public int getPassageLength() {
        return passageLength;
    }

    public Typist getTypist(int seatNumber) {
        if (seatNumber >= 0 && seatNumber < typists.size()) {
            return typists.get(seatNumber);
        }
        return null;
    }

    public int getSlideAmount() {
        return slideBackAmount;
    }

    public static void startRaceGUI() {
        TypingRaceGUI gui = new TypingRaceGUI();
        gui.launch();
    }

    public static void main(String[] args) {
        startRaceGUI();
    }
}
