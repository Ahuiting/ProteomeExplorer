package ProteomeExplorer.undoredo;

public class SimpleCommand implements Command {
    private final String name;
    private final Runnable undoRunnable;
    private final Runnable redoRunnable;

    public SimpleCommand(String name, Runnable undoRunnable, Runnable redoRunnable) {
        this.name = name;
        this.undoRunnable = undoRunnable;
        this.redoRunnable = redoRunnable;
    }

    public void undo() {
        undoRunnable.run();
    }

    public void redo() {
        redoRunnable.run();
    }

    public String name() {
        return name;
    }

    public boolean canUndo() {
        return undoRunnable != null;
    }

    public boolean canRedo() {
        return redoRunnable != null;
    }
}
