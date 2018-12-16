package gui.extra;

/**
 * A mutable class that holds several toggleable boolean flags that the
 * main game can use to decide what "additional" features that should be active
 */
public class InformationToggle {
    private boolean moveHighlight;
    private boolean lastMoveHighlight;
    private boolean boardStatus;
    private boolean playSound;
    private boolean moveAnimation;

    public InformationToggle(boolean moveHighlight, boolean lastMoveHighlight,
                             boolean boardStatus, boolean playSound, boolean moveAnimation) {
        this.moveHighlight = moveHighlight;
        this.lastMoveHighlight = lastMoveHighlight;
        this.boardStatus = boardStatus;
        this.playSound = playSound;
        this.moveAnimation = moveAnimation;
    }

    void toggleHighlight() {
        moveHighlight = !moveHighlight;
    }

    void toggleLastMoveHighlight() {
        lastMoveHighlight = !lastMoveHighlight;
    }

    void toggleBoardStatus() {
        boardStatus = !boardStatus;
    }

    void toggleSound() {
        playSound = !playSound;
    }

    public void toggleMoveAnimation() {
        moveAnimation = !moveAnimation;
    }

    public boolean isMoveHighlightEnabled() {
        return moveHighlight;
    }

    public boolean isLastMoveHighlightEnabled() {
        return lastMoveHighlight;
    }

    public boolean isBoardStatusEnabled() {
        return boardStatus;
    }

    public boolean isPlaySoundEnabled() {
        return playSound;
    }

    public boolean isMoveAnimationEnabled() {
        return moveAnimation;
    }
}
