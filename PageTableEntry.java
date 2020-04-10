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

import osp.Hardware.*;
import osp.Tasks.*;
import osp.Threads.*;
import osp.Devices.*;
import osp.Utilities.*;
import osp.IFLModules.*;
import osp.FileSys.OpenFile;

/**
 * The PageTableEntry object contains information about a specific virtual page
 * in memory, including the page frame in which it resides.
 * 
 * @OSPProject Memory
 * 
 */
public class PageTableEntry extends IflPageTableEntry {
    /**
     * The constructor. Must call
     * 
     * super(ownerPageTable,pageNumber);
     * 
     * as its first statement.
     * 
     * @OSPProject Memory
     */
    public PageTableEntry(PageTable ownerPageTable, int pageNumber) {
        super(ownerPageTable, pageNumber);
    }

    /**
     * This method increases the lock count on the page by one.
     * 
     * The method must FIRST increment lockCount, THEN check if the page is valid,
     * and if it is not and no page validation event is present for the page, start
     * page fault by calling PageFaultHandler.handlePageFault().
     * 
     * @return SUCCESS or FAILURE FAILURE happens when the pagefault due to locking
     *         fails or the that created the IORB thread gets killed.
     * 
     * @OSPProject Memory
     */
    public int do_lock(IORB iorb) {
        /**
         * Now check if the page is vaid. If it is not valid & no validation event
         * exists, start page fault with handlePageFault().
         */
        if (this.isValid() == false && this.getValidatingThread() == null) {
            if (PageFaultHandler.handlePageFault(iorb.getThread(), GlobalVariables.MemoryLock, this) == FAILURE) {
                return FAILURE;
            }

            // if the thread was killed, then return failure.
            if (iorb.getThread().getStatus() == ThreadKill) {
                return FAILURE;
            }
        } else if (this.isValid() == false && this.getValidatingThread() != iorb.getThread()) {
            iorb.getThread().suspend(this);

            // Thread wasn't suspended successfully.
            if (iorb.getThread().getStatus() != ThreadWaiting || this.isValid() == false) {
                return FAILURE;
            }
        }

        /**
         * Finally, increment lock count.
         * 
         * THE INSTRUCTIONS SAY THIS MUST BE DONE FIRST. BUT PROGRAM DOES NOT FUNCTION
         * CORRECTLY WHEN THIS IS FIRST.
         */
        this.getFrame().incrementLockCount();

        return SUCCESS;
    }

    /**
     * This method decreases the lock count on the page by one.
     * 
     * This method must decrement lockCount, but not below zero.
     * 
     * @OSPProject Memory
     */
    public void do_unlock() {
        /**
         * If there's an existing lock, then decrement the lock count.
         */
        if (this.getFrame().getLockCount() > 0) {
            this.getFrame().decrementLockCount();
            return;
        }

        // no lock present.
        return;
    }
}
