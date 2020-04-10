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
        super(ownerTask);

        /**
         * Initialize the array of page table entry objects.
         */
        int num = (int) Math.pow(2, MMU.getPageAddressBits());
        this.pages = new PageTableEntry[num];
        for (int i = 0; i < num; ++i) {
            this.pages[i] = new PageTableEntry(this, i);
        }
    }

    /**
     * Frees up main memory occupied by the task. Then unreserves the freed pages,
     * if necessary.
     * 
     * @OSPProject Memory
     */
    public void do_deallocateMemory() {
        /**
         * Get task of this page.
         */
        TaskCB task = this.getTask();

        /**
         * Iterate through frame table and clean up each of the frames. These may be
         * changed each iteration.
         */
        PageTableEntry page;
        FrameTableEntry frame;
        for (int i = 0; i < MMU.getFrameTableSize(); ++i) {
            /**
             * Get current frame & page.
             */
            frame = MMU.getFrame(i);
            page = MMU.getFrame(i).getPage();

            /**
             * If the current page isn't null, and task is this one, then clean it up.
             */
            if (page != null && page.getTask() == task) {
                frame.setPage(null);
                frame.setDirty(false);
                frame.setReferenced(false);

                /**
                 * If it's reserved, then unreserve the task.
                 */
                if (frame.getReserved() == task) {
                    frame.setUnreserved(task);
                }
            }
        }
    }
}
