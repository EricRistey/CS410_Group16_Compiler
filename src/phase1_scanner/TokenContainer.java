package phase1_scanner;

public class TokenContainer {
    public int states[];
    public String states_string[];
    public int length;

    public TokenContainer(int[] states, String[] states_string, int length) {
        this.states = states;
        this.states_string = states_string;
        this.length = length;

    }
}
