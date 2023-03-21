package melody.audio;

// CSE 143, Homework 3: Melody
// Instructor-provided code.  You should not modify this file!

// An Accidental represents a musical accidental; sharp, flat or natural.

public enum Accidental {	
    SHARP, NATURAL, FLAT;
    public static Accidental getValueOf(String s) {
        s = s.intern();
        if (s == "sharp") {
            return Accidental.SHARP;
        }
        if (s == "flat") {
            return Accidental.FLAT;
        }
        if (s == "natural") {
            return Accidental.NATURAL;
        }
        return null;
    }
}