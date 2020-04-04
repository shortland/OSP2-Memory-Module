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
 * The PageTable class represents the page table for a given task.
 * A PageTable consists of an array of PageTableEntry objects.  This
 * page table is of the non-inverted type.
 * 
 * @OSPProject Memory
 */
import java.lang.Math;
import osp.Tasks.*;
import osp.Utilities.*;
import osp.IFLModules.*;
import osp.Hardware.*;

public class PageTable extends IflPageTable {
    /**
     * The page table constructor. Must call
     * 
     * super(ownerTask)
     * 
     * as its first statement.
     * 
     * @OSPProject Memory
     */
    public PageTable(TaskCB ownerTask) {
        // your code goes here
        super(ownerTask);

        /**
         * Initialize the array of page table entry objects.
         */
        int num = (int) Math.pow(2, MMU.getPageAddressBits());
        pages = new PageTableEntry[num];
        for (int i = 0; i < num; ++i) {
            pages[i] = new PageTableEntry(this, i);
        }
    }

    /**
     * Frees up main memory occupied by the task. Then unreserves the freed pages,
     * if necessary.
     * 
     * @OSPProject Memory
     */
    public void do_deallocateMemory() {
        // your code goes here
        /**
         * Get task of this page.
         */
        TaskCB currentTask = this.getTask();

        /**
         * Iterate through frame table and clean up each of the frames.
         */
        FrameTableEntry currentFrame;
        PageTableEntry currentPage;
        for (int i = 0; i < MMU.getFrameTableSize(); ++i) {
            /**
             * Get current frame & page.
             */
            currentFrame = MMU.getFrame(i);
            currentPage = MMU.getFrame(i).getPage();

            /**
             * If the current page isn't null, and task is this one, then clean it up.
             */
            // TODO:
            // might need:
            // currentPage != null &&
            if (currentPage.getTask() == currentTask) {
                currentFrame.setPage(null);
                currentFrame.setDirty(false);
                currentFrame.setReferenced(false);

                /**
                 * If it's reserved, then unreserve the task.
                 */
                if (currentFrame.getReserved() == currentTask) {
                    currentFrame.setUnreserved(currentTask);
                }
            }
        }
    }

    /*
     * Feel free to add methods/fields to improve the readability of your code
     */

}

/*
 * Feel free to add local classes to improve the readability of your code
 */
