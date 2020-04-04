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
    }

    /*
     * Feel free to add methods/fields to improve the readability of your code
     */
}

/*
 * Feel free to add local classes to improve the readability of your code
 */
