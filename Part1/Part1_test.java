public class Part1_test {

    public static void main(String[] args) {        
        Typist test_typist = new Typist('①', "TURBOFINGERS", 0.85); // Creating typist object to run tests
    
        // Test 1: Progress (value of charsAlong attribute) cannot go below 0 after slideBack()
        for (int i = 0; i < 10; i++) {
            test_typist.typeCharacter();
        }

        System.out.println("Original value of charsAlong attribute: " + test_typist.getProgress());
        System.out.println("Decrementing by 11");
        test_typist.slideBack(11);
        System.out.println("New value of charsAlong attribute: " + test_typist.getProgress());

        // Test 2: Burnout correctly counts down turn by turn and clears at zero
        test_typist.burnOut(5);
        while (test_typist.isBurntOut()) {
            System.out.println("Is Burnt Out: " + test_typist.isBurntOut());
            System.out.println("Turns left: " + test_typist.getBurnoutTurnsRemaining());
            test_typist.recoverFromBurnout();
        }

        System.out.println("Is Burnt Out: " + test_typist.isBurntOut());

        // Test 3: resetToStart() clears both progress and burnout state
        for (int i = 0; i < 10; i++) {
            test_typist.typeCharacter();
        }

        test_typist.burnOut(10);

        System.out.println("Progress: " + test_typist.getProgress());
        System.out.println("Burnout: " + test_typist.isBurntOut() + ", number of turns left: " + test_typist.getBurnoutTurnsRemaining());

        test_typist.resetToStart();

        System.out.println("Progress: " + test_typist.getProgress());
        System.out.println("Burnout: " + test_typist.isBurntOut() + ", number of turns left: " + test_typist.getBurnoutTurnsRemaining());


        // Test 4: accuracy cannot be set outside 0.0-1.0 range
        System.out.println("Typist accuracy (original): " + test_typist.getAccuracy());
        test_typist.setAccuracy(-1.5);
        System.out.println("Typist accuracy (after setting to -1.5): " + test_typist.getAccuracy());
        test_typist.setAccuracy(1.5);
        System.out.println("Typist accuracy (after setting to 1.5): " + test_typist.getAccuracy());


        // Test 5: Normal forward movement via typeCharacter()
        System.out.println("Current progress: " + test_typist.getProgress());
        test_typist.typeCharacter();
        System.out.println("New progress: " + test_typist.getProgress());
    }

}


// ◦ That resetToStart() clears both progress and burnout state 
// ◦ That accuracy cannot be set outside the 0.0–1.0 range 
// ◦ Normal forward movement via typeCharacter()