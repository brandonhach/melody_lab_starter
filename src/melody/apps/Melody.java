package melody.apps;

// TODO: Complete this class

import melody.audio.Note;
import java.util.*;
import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import melody.audio.Pitch;

//ArrayBlockingQueue
//A Queue that additionally supports operations that wait for the queue to become non-empty when retrieving an element, and wait for space to become available in the queue when storing an element.
//https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ArrayBlockingQueue.html
public class Melody {
    Queue<Note> song;
    String songTitle;
    String songComposers;
    int numOfLines;
    double totalDuration;
    final static int MIN_OCTAVE = 1;
    final static int MAX_OCTAVE = 10;
    final static int MAX_REPEATS = 2;
    final static int ARRAY_BLOCKING_QUEUE_INIT_CAPACITY = 100;

    /**
     * Initializes your melody to store the passed in Queue of Notes.
     * 
     * @param file
     */
    public Melody(Queue<Note> song,
            String title, String artists, int lineNum) {
        // TODO: write this constructor
        this.song = song;
        this.totalDuration = 0;
        this.songTitle = title;
        this.numOfLines = lineNum;
        this.songComposers = artists;
    }

    /**
     * Changes the tempo of each note to be tempo percent of
     * what it formerly was. Passing a tempo of 1.0 will make
     * the tempo stay the same. tempo of 2.0 will make each
     * note twice as long. tempo of 0.5 will make each note
     * half as long. Keep in mind that when the tempo changes
     * the length of the song also changes.
     * 
     * @param ratio
     * 
     *              STEPS
     *              1) Check whether song is null
     *              2) Iterate through and multiple each duration by the ratio
     *              3) Multiply totalDuration by ratio
     */
    public void changeTempo(double ratio) {
        if (this.song == null)
            return;
        int numOfNotes = this.song.size();
        Note note;
        for (int i = 0; i < numOfNotes; i++) {
            note = this.song.poll();
            note.setDuration(ratio * note.getDuration());
            this.song.add(note);
        }
        this.totalDuration *= ratio;
    }

    /**
     * Adds all notes from the given other song to the end of
     * this song. For example, if this song is A,F,G,B and the
     * other song is F, C, D, your method should change this
     * song to be A, F, G, B, F, C, D. The other song should
     * be unchanged after the call. Remember that objects can
     * access the private fields of other objects of the same
     * type.
     * 
     * @param other
     * 
     *              STEPS
     *              1) Check if other equals null
     *              2) Check if song is null
     *              if true, create a new song of type Note
     *              3) Create a loop to add 'other' elements to the end of this.song
     *              4) Increment totalDuration
     */
    public void append(Melody other) {
        // Todo: write this method
        if (other == null)
            return;
        else if (this.song == null) {
            this.song = new ArrayBlockingQueue<Note>(ARRAY_BLOCKING_QUEUE_INIT_CAPACITY);
        } else {
            for (int i = 0; i < other.song.size(); i++) {
                this.song.add(other.song.poll());
                other.song.remove();
            }

            this.totalDuration = other.totalDuration + totalDuration;
        }
    }

    /**
     * In this method you should return the total duration (length) of the song, in
     * seconds. In general this is equal to the
     * sum of the durations of the song's notes, but if some sections of the song
     * are repeated, those parts count twice
     * toward the total. For example, a song whose notes' durations add up to 6
     * seconds that has a 1.5-second repeated
     * section and a 1-second repeated section has a total duration of (6.0 + 1.5 +
     * 1.0) = 8.5 seconds.
     * 
     * @return
     *         STEPS
     *         1) If song is null, return 0
     *         2) If totalDuration is 0
     *         Create loop to iterate through all notes
     *         3) Poll the current node and add to tmpQueue
     *         4) If note is repeated, add to repeatQueue
     *         Increase repeatCount
     *         5) If repeatCount equals max repeats, reset repeatCount, isRepeated,
     *         and tmpQueue
     *         6) Return totalDuration
     */
    public double getTotalDuration() {
        if (this.song == null)
            return 0;
        if (this.totalDuration == 0) {
            boolean isRepeated = false;
            int repeatCount = 0;
            int numOfNotes = this.song.size();
            Queue<Note> repeatQueue = null;
            Note note;
            Queue<Note> tmpQueue = this.song;
            for (int i = 0; i < numOfNotes; i++) {
                note = tmpQueue.poll();
                this.totalDuration += note.getDuration();
                tmpQueue.add(note);
                if (repeatCount != 0)
                    numOfNotes++;
                if ((!isRepeated) && note.isRepeat()) {// The beginning of a repeat section
                    repeatQueue = new ArrayBlockingQueue<Note>(ARRAY_BLOCKING_QUEUE_INIT_CAPACITY);
                    isRepeated = !isRepeated;
                    repeatQueue.add(note);
                } else if (isRepeated && note.isRepeat()) {// The end of a repeat section
                    repeatCount++;
                    if (repeatCount >= Melody.MAX_REPEATS) {
                        tmpQueue = this.song;
                        isRepeated = false;
                        repeatCount = 0;
                    }
                } else if (isRepeated && (!note.isRepeat()))// The middle of a repeat section
                    repeatQueue.add(note);
            }
        }
        return this.totalDuration;
    }

    /**
     * In this method you should play your melody so that it can
     * be heard on the computer's speakers. Essentially this
     * consists of calling the play method on each Note in your
     * array. The notes should be played from the beginning
     * of the list to the end, unless there are notes that
     * are marked as being part of a repeated section. If a
     * series of notes represents a repeated section, that sequence
     * is played twice. For example, in the diagram below, suppose
     * the notes at indexes 3, 5, 9, and 12 all indicate that they
     * are start/end points of repeated sections (their isRepeat
     * method returns true). In this case, the correct sequence of
     * note indexes to play is 0, 1, 2, 3, 4, 5, 3, 4, 5, 6, 7, 8,
     * 9,10, 11, 12, 9, 10, 11, 12, 13. Note that notes at indexes
     * 3-5 and 9-12 are played twice in our example.
     * This method should not modify the state of your array.
     * Also, it should be possible to call play multiple times
     * and get the same result each time.
     * 
     * STEPS
     * -Steps are labeled within method
     */
    public void play() {
        // TODO: write this method
        if (this.song == null)
            return;
        boolean isRepeated = false;
        int repeatCount = 0;
        int numOfNotes = this.song.size();
        Queue<Note> repeatQueue = null;
        Note note;
        Queue<Note> tmpQueue = this.song;
        for (int i = 0; i < numOfNotes; i++) {
            note = tmpQueue.poll();
            note.play();
            tmpQueue.add(note);
            if (repeatCount != 0)
                numOfNotes++;
            if ((!isRepeated) && note.isRepeat()) { // The beginning of a repeat section
                repeatQueue = new ArrayBlockingQueue<Note>(ARRAY_BLOCKING_QUEUE_INIT_CAPACITY);
                isRepeated = !isRepeated;
                // Missing....
                // Update repeatQueue and add note
                repeatQueue.add(note);

            } else if (isRepeated && note.isRepeat()) {// The end of a repeat section
                repeatCount++;
                if (repeatCount >= Melody.MAX_REPEATS) {
                    isRepeated = false;
                    repeatCount = 0;
                    // Missing...
                    // Update tmpQueue
                    tmpQueue = this.song;

                }
            } else if (isRepeated && (!note.isRepeat())) {// The middle of a repeat section
                // Missing....
                // Update repeatQueue
                repeatQueue.add(note);
                tmpQueue = repeatQueue;
            }
        }
        return;
    }

    /**
     * In this method you should modify the state of the notes in your internal
     * array so that they are all exactly 1 octave
     * lower in pitch than their current state. For example, a C note in octave 4
     * would become a C note in octave 3.
     * Rests are not affected by this method, and the notes' state is otherwise
     * unchanged other than the octaves.
     * There is one special case to watch out for. Octave 1 is the lowest possible
     * octave allowed by our system. If any
     * note(s) in your song are already down at octave 1, then the entire octaveDown
     * call should do nothing. In such a
     * case, no notes (even ones above octave 1) should be changed; the call should
     * have no effect.
     * You should return true if this method lowered the octave, and false if you
     * hit the above special case.
     * 
     * @return
     * 
     *         STEPS
     *         1) If song is null, return false
     *         2) Iterate through song and assign note to the song.poll
     *         If pitch cannot decrease, isInvalid is true
     *         3) Create a loop to decrease each note
     */
    public boolean octaveDown() {
        if (this.song == null)
            return false;
        Note note;
        boolean isInvalid = false;
        int numOfNotes = this.song.size();
        for (int i = 0; i < numOfNotes; i++) {
            note = this.song.poll();
            if (note.getPitch() != Pitch.R && note.getOctave() == Melody.MIN_OCTAVE)
                isInvalid = true;
            this.song.add(note);
        }
        if (isInvalid)
            return false;

        for (int i = 0; i < numOfNotes; i++) {
            note = this.song.poll();
            if (note.getPitch() != Pitch.R)
                note.setOctave(note.getOctave() - 1);
            this.song.add(note);
        }
        return true;
    }

    /**
     * In this method you should modify the state of the notes in
     * your internal array so that they are all exactly 1 octave
     * higher in pitch than their current state. For example, a C
     * note in octave 4 would become a C note in octave 5.
     * Rests are not affected by this method, and the notes' state
     * is otherwise unchanged other than the octaves. There is one
     * special case to watch out for. Octave 10 is the highest
     * possible octave allowed by our system. If any note(s) in
     * your song are already up at octave 10, then the entire
     * octaveUp call should do nothing. In such a case, no notes
     * (even ones below octave 10) should be changed; the call
     * should have no effect.
     * You should return true if this method raised the octave,
     * and false if you hit the above special case
     * 
     * @return
     * 
     *         STEPS
     *         1) If song is null, return false
     *         2) Iterate through song and assign note to the song.poll
     *         If pitch cannot increase, isInvalid is true
     *         3) Create a loop to increase each note
     */
    public boolean octaveUp() {
        // This is left for the individual project (it is not required to finish in the
        // lab)
        if (this.song == null)
            return false;
        Note note;
        boolean isInvalid = false;
        int numOfNotes = this.song.size();
        for (int i = 0; i < numOfNotes; i++) {
            note = this.song.poll();
            if (note.getPitch() != Pitch.R && note.getOctave() == Melody.MAX_OCTAVE)
                isInvalid = true;
            this.song.add(note);
        }
        if (isInvalid)
            return false;

        for (int i = 0; i < numOfNotes; i++) {
            note = this.song.poll();
            if (note.getPitch() != Pitch.R)
                note.setOctave(note.getOctave() + 1);
            this.song.add(note);
        }
        return true;
    }

    /**
     * Reverses the order of notes in the song, so that future
     * calls to the play methods will play the notes in the
     * opposite of the order they were in before reverse was
     * called. For example, a song containing notes A, F, G,
     * then B would become B, G, F, A. You may use one
     * temporary Stack or one temporary Queue to help you
     * solve this problem.
     * 
     * STEPS
     * 1) Check if song is null
     * 2) Use loop to iterate through song
     * push current note into temporary songStack
     * 3) Iterate through songStack and add notes back to this.song
     */
    public void reverse() {
        // TODO: write this method
        if (this.song == null)
            return;

        Stack<Note> songStack = new Stack();
        while (this.song.size() != 0) {
            songStack.push(this.song.poll());
        }
        while (songStack.size() != 0) {
            this.song.add(songStack.pop());
        }

    }

    /**
     * In this method you should return the title of the song,
     * as was found in the first line of the song's input file.
     * 
     * @return
     */
    public String getTitle() {
        if (this.song == null)
            return null;
        return this.songTitle;
    }

    /**
     * In this method you should return the artist of the song,
     * as was found in the second line of the song's input file.
     * 
     * @return
     */
    public String getArtist() {
        if (this.song == null)
            return null;
        return this.songComposers;
    }

    /**
     * You are not required to write a toString method in your
     * Melody class, but if you do, it will be called by our Main
     * program when any operations are performed. For example, after
     * loading a song from a file, or reversing the song, or
     * changing duration or octaves, the Main program prints out
     * the toString representation of your Melody on the console.
     * If you do write a toString, you can return any string you
     * want. This may be useful for debugging. Recall that
     * Arrays.toString returns a string representation of an array
     * 
     * @return
     */
    public String toString() {
        // TODO: write this method
        if (this.song == null)
            return "";
        return Arrays.toString(this.song.toArray());
    }
}
