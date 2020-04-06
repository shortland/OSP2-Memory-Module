/**
 * Ilan Kleiman
 * 110942711
 * 
 * I pledge my honor that all parts of this project were done by me individually, 
 * without collaboration with anyone, and without consulting any external sources 
 * that provide full or partial solutions to a similar project.
 * I understand that breaking this pledge will result in an "F" for the entire course.
 */

package osp.Memory;

/**
 * The FrameTableEntry class contains information about a specific page
 * frame of memory.
 * 
 * @OSPProject Memory
 */
import osp.Tasks.*;
import osp.Interrupts.*;
import osp.Utilities.*;
import osp.IFLModules.IflFrameTableEntry;

public class FrameTableEntry extends IflFrameTableEntry {
    /**
     * @see #useCounts - The amount of times the frame has been used.
     */
    private int useCounts;

    /**
     * The frame constructor. Must have
     * 
     * super(frameID)
     * 
     * as its first statement.
     * 
     * @OSPProject Memory
     */
    public FrameTableEntry(int frameID) {
        /**
         * Call super for the parent obj.
         */
        super(frameID);

        /**
         * Initialize a new frame with 1 use counts.
         */
        this.useCounts = 1;
    }

    /**
     * Get the # of use counts for this frame
     */
    public int getUseCounts() {
        return this.useCounts;
    }

    /**
     * Set the # of use counts for this frame
     */
    public void setUseCounts(int useCounts) {
        this.useCounts = useCounts;
    }
}
