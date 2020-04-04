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

import java.util.*;
import osp.IFLModules.*;
import osp.Threads.*;
import osp.Tasks.*;
import osp.Utilities.*;
import osp.Hardware.*;
import osp.Interrupts.*;

/**
 * The MMU class contains the student code that performs the work of handling a
 * memory reference. It is responsible for calling the interrupt handler if a
 * page fault is required.
 * 
 * @OSPProject Memory
 */
public class MMU extends IflMMU {
    /**
     * This method is called once before the simulation starts. Can be used to
     * initialize the frame table and other static variables.
     * 
     * @OSPProject Memory
     */
    public static void init() {
        // your code goes here

    }

    /**
     * This method handles memory references. The method must calculate, which
     * memory page contains the memoryAddress, determine, whether the page is valid,
     * start page fault by making an interrupt if the page is invalid, finally, if
     * the page is still valid, i.e., not swapped out by another thread while this
     * thread was suspended, set its frame as referenced and then set it as dirty if
     * necessary. (After pagefault, the thread will be placed on the ready queue,
     * and it is possible that some other thread will take away the frame.)
     * 
     * @param memoryAddress A virtual memory address
     * @param referenceType The type of memory reference to perform
     * @param thread        that does the memory access (e.g., MemoryRead or
     *                      MemoryWrite).
     * @return The referenced page.
     * 
     * @OSPProject Memory
     */
    static public PageTableEntry do_refer(int memoryAddress, int referenceType, ThreadCB thread) {
        // your code goes here

        /**
         * Get the page number of a memory address (get its index num from memory
         * address).
         */
        int num = memoryAddress / (int) Math.pow(2.0, getVirtualAddressBits() - getPageAddressBits());

        /**
         * Get the PageTableEntry from it's calculated index above.
         */
        PageTableEntry pEntry = getPTBR().pages[num];

        /**
         * If the page isn't validating, attempt to stop/interrupt processes
         */
        if (pEntry.getValidatingThread() == null && pEntry.isValid() == false) {
            InterruptVector.setPage(pEntry);
            InterruptVector.setThread(thread);
            InterruptVector.setInterruptType(referenceType);

            // do a cpu interrupt w/PageFault since thread not valid and no validating
            // thread.
            CPU.interrupt(PageFault);
        } else if (pEntry.isValid() == false) {
            /**
             * Have a validating thread so we should attempt to suspend the thread. Since it
             * might be already going in/out of memory.
             */
            thread.suspend(pEntry);
        }

        /**
         * If the thread status is kill, then just return current page table.
         */
        if (thread.getStatus() == GlobalVariables.ThreadKill) {
            return pEntry;
        }

        /**
         * Since the page is referenced, set that it has been referenced. And, set dirty
         * bit if it's a write type.
         */
        pEntry.getFrame().setReferenced(true);
        if (referenceType == GlobalVariables.MemoryWrite) {
            pEntry.getFrame().setDirty(true);
        } else if (referenceType == GlobalVariables.MemoryRead) {
            // don't need to set the bit in this case.
            // pEntry.getFrame().setDirty
        }

        /**
         * Finally return the page table after finished setting bits.
         */
        return pEntry;
    }

    /**
     * Called by OSP after printing an error message. The student can insert code
     * here to print various tables and data structures in their state just after
     * the error happened. The body can be left empty, if this feature is not used.
     * 
     * @OSPProject Memory
     */
    public static void atError() {
        /**
         * No need to do anything when an error occurs.
         */
        return;
    }

    /**
     * Called by OSP after printing a warning message. The student can insert code
     * here to print various tables and data structures in their state just after
     * the warning happened. The body can be left empty, if this feature is not
     * used.
     * 
     * @OSPProject Memory
     */
    public static void atWarning() {
        /**
         * No need to do anything when a warning occurs.
         */
        return;
    }

    /*
     * Feel free to add methods/fields to improve the readability of your code
     */

}

/*
 * Feel free to add local classes to improve the readability of your code
 */
