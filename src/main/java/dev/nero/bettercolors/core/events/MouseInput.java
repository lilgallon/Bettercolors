package dev.nero.bettercolors.core.events;

public class MouseInput {

    private final int button;
    private final int action;

    public MouseInput(int button, int action) {
        this.button = button;
        this.action = action;
    }

    public int getButton() {
        return button;
    }

    public int getAction() {
        return action;
    }
}
