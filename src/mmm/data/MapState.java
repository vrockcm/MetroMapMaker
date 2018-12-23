package mmm.data;

/**
 * This enum has the various possible states of the logo maker app
 * during the editing process which helps us determine which controls
 * are usable or not and what specific user actions should affect.
 * 
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public enum MapState {
    SELECTING_NODE,
    ADDING_STATION,
    REMOVING_STATION,
    REMOVING_ELEMENT,
    DRAGGING_NOTHING,
    DRAGGING_NODE;
}
