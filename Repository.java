import java.util.*;
import java.text.SimpleDateFormat;
//David Guzman Valente
// TA: Niyati Trivedi
// 2/7/2024
// P1: Mini-Git
// Repository
// this class represents a repository that stores the amount of commits created by the user
public class Repository {

    /**
     * TODO: Implement your code here.
     */
    private String name;
    private Commit head;

    // Behavior: constructs a repository with the name given by the user
    // Exceptions: throws an IllegalArgumentException if the name is null or empty
    // Return: N/A
    // Parameters: accepts the given name inputted by the user
    public Repository(String name) {
        if(name == null || name.equals("")) {
            throw new IllegalArgumentException();
        } 

        this.name = name;
        head = null;
    }

    // Behavior: returns the id of this repositories head. If the head is null
    // the method returns null
    // Exceptions: N/A
    // Return: returns the id of this repositories head if it has any commits. If not
    // it returns null
    // Parameters: N/A 
    public String getRepoHead() {
        if(head == null) {
            return null;
        }
        return head.id;
    }

    // Behavior: gets the size of this repository and returns it to the user
    // Exceptions:N/A
    // Return: returns a number representing the size of the repository
    // Parameters: N/A
    public int getRepoSize() {
        Commit curr = head;
        int size = 0;
        while(curr != null) {
            size++;
            curr = curr.past;
        }

        return size;
    }

    // Behavior: Returns to the user a string of the repositories name and it's current head.
    // If the repositories head is null it states that it has no commits
    // Exceptions: N/A
    // Return: returns a string with the repositories name and current head if their are commits.
    // If not it will return the name and state that it has no commits.
    // Parameters: N/A
    public String toString() {
        if(head == null) {
            return name + " - " + "No commits";
        }
        return name + " - " + "Current head: " + head.toString();
    }

    // Behavior: looks at the repository and determines whether it contains the id and returns
    // true if it contains the id. If not it returns false
    // Exceptions: N/A
    // Return: returns true if the id given by the user is found, and false if not
    // Parameters: accepts a target id so that for the method to look for
    public boolean contains(String targetId) {
        Commit curr = head;
        while(curr != null) {
            if(curr.id.equals(targetId)) {
                return true;
            }
            curr = curr.past;
        }
        return false;
    }

    // Behavior:returns a string representation of the most recent commits
    // according to the users input. If the user inputs a number greater than the repository's
    // size, the string will contain all commits. If the input is a number within the commits size
    // it returns that amount of commits beginning with the most recent and going down the list 
    // until it reaches the amount. If the repository is empty it will return an empty line.
    // Exceptions: throws an IllegalArgumentException if the users input is 
    // less than or equal to zero
    // Return: returns a string representation of the most recent commits beginning with the most
    // recent and going down the list until the users amount is reached. If the user inputs a 
    // number greater than the repository's size it returns all commits.
    // Parameters: accepts a number representing the amount of commits the user would like to
    // see
    public String getHistory(int n) {
        if(n <= 0) {
            throw new IllegalArgumentException();
        }

        String history = "";
        Commit curr = head;
        int count = 0;
        while(curr != null || count != n) {
            history += curr.toString();
            if(curr.past != null) {
                history += "\n";
            }
            count++;
            curr = curr.past;
        }

        return history;
    }

    // Behavior: adds another commit to this repository with the users given message
    // Exceptions: N/A
    // Return: returns a string of the new heads id
    // Parameters: accepts a message that will be associated with the new commit created
    public String commit(String message) {

        this.head = new Commit(message, head);
        return head.id;
    }

    // Behavior: Looks for the commit with the id specified by the user and 
    // removes it from the repository. Returns true if the commit was found and removed
    // and false if the commit was never located and nothing was removed.
    // Exceptions:N/A
    // Return: returns true if the commit with the targeted id has been removed
    // and false if the commit was not located within the repository
    // Parameters: accepts a string representing the id of the commit to be removed
    public boolean drop(String targetId) {
        if(head != null) {
            if(head.id.equals(targetId)) {
                head = head.past;
                return true;
            }
            Commit curr = head;
            while(curr != null && curr.past != null) {
                Commit temp = curr.past;
                if(temp.id.equals(targetId)) {
                    curr.past = curr.past.past; 
                    return true;
                }
                curr = curr.past;
                temp = curr.past;
            }
        }
        return false;
    }

    // Behavior: takes another repository and adds all of its commits to this repository in until
    // the other commit is empty. The other repositories commits are combined with this
    // repositories commits in chronological order based on the time they were created, with the
    // most recent at the front and the oldest at the end. If this repository is empty it takes 
    // all of the others commits until the inputted repository is empty. If the other repository
    // is empty their is no change.
    // Exceptions: N/A
    // Return: N/A
    // Parameters: accepts another repository so that it's commits can be added to this repository
    public void synchronize(Repository other) {
        if(this.head == null) {
            this.head = other.head;
            other.head = null;
        } else if(other.getRepoSize() != 0)  {
            if (other.head.timeStamp > head.timeStamp) {
                Commit temp = other.head.past;
                other.head.past = this.head;
                this.head = other.head;
                other.head = temp;
            }
            Commit before = null;
            Commit curr = this.head;
            while(other.head != null) {
                //if our current timeStamp occured earlier than the others timeStamp
                if(curr.timeStamp < other.head.timeStamp) {
                    Commit past = curr;
                    curr = other.head; //we're going to set this curr to the other's head
                    other.head = other.head.past;
                    curr.past = past;
                    if(before != null) {
                        before.past = curr;
                    }
                }
                before = curr;
                curr = curr.past;
                if(curr == null) {
                    before.past = other.head;
                    other.head = null;
                }
            }
        }
    }
    
    /**
     * DO NOT MODIFY
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this 
     * class openly mention the fields of the class. This is fine 
     * because the fields of the Commit class are public. In general, 
     * be careful about revealing implementation details!
     */
    public class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
        * Resets the IDs of the commit nodes such that they reset to 0.
        * Primarily for testing purposes.
        */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}
