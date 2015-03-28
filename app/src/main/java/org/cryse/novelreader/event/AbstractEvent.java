package org.cryse.novelreader.event;

public abstract class AbstractEvent {
    private boolean isHandled = false;

    public boolean isHandled() {
        return isHandled;
    }

    public void setHandled(boolean isHandled) {
        this.isHandled = isHandled;
    }
}
