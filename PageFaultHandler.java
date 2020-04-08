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
import osp.Hardware.*;
import osp.Threads.*;
import osp.Tasks.*;
import osp.FileSys.FileSys;
import osp.FileSys.OpenFile;
import osp.IFLModules.*;
import osp.Interrupts.*;
import osp.Utilities.*;
import osp.IFLModules.*;

/**
 * The page fault handler is responsible for handling a page fault. If a swap in
 * or swap out operation is required, the page fault handler must request the
 * operation.
 * 
 * @OSPProject Memory
 */
public class PageFaultHandler extends IflPageFaultHandler {
    /**
     * This method handles a page fault.
     * 
     * It must check and return if the page is valid,
     * 
     * It must check if the page is already being brought in by some other thread,
     * i.e., if the page's has already pagefaulted (for instance, using
     * getValidatingThread()). If that is the case, the thread must be suspended on
     * that page.
     * 
     * If none of the above is true, a new frame must be chosen and reserved until
     * the swap in of the requested page into this frame is complete.
     * 
     * Note that you have to make sure that the validating thread of a page is set
     * correctly. To this end, you must set the page's validating thread using
     * setValidatingThread() when a pagefault happens and you must set it back to
     * null when the pagefault is over.
     * 
     * If a swap-out is necessary (because the chosen frame is dirty), the victim
     * page must be dissasociated from the frame and marked invalid. After the
     * swap-in, the frame must be marked clean. The swap-ins and swap-outs must are
     * preformed using regular calls read() and write().
     * 
     * The student implementation should define additional methods, e.g, a method to
     * search for an available frame.
     * 
     * Note: multiple threads might be waiting for completion of the page fault. The
     * thread that initiated the pagefault would be waiting on the IORBs that are
     * tasked to bring the page in (and to free the frame during the swapout).
     * However, while pagefault is in progress, other threads might request the same
     * page. Those threads won't cause another pagefault, of course, but they would
     * enqueue themselves on the page (a page is also an Event!), waiting for the
     * completion of the original pagefault. It is thus important to call
     * notifyThreads() on the page at the end -- regardless of whether the pagefault
     * succeeded in bringing the page in or not.
     * 
     * @param thread        the thread that requested a page fault
     * @param referenceType whether it is memory read or write
     * @param page          the memory page
     * 
     * @return SUCCESS if everything is fine; FAILURE if the thread dies while
     *         waiting for swap in or swap out or if the page is already in memory
     *         and no page fault was necessary (well, this shouldn't happen,
     *         but...). In addition, if there is no frame that can be allocated to
     *         satisfy the page fault, then it should return NotEnoughMemory
     * 
     * @OSPProject Memory
     */
    public static int do_handlePageFault(ThreadCB thread, int referenceType, PageTableEntry page) {
        /**
         * PageFault shouldn't of occured since the page is valid.
         */
        if (page.isValid()) {
            return FAILURE;
        }

        /**
         * Finds a new frame following guidelines in which order to select a frame from.
         */
        FrameTableEntry frame = choose_new_frame();

        /**
         * Unable to handle the page fault - unable to get a valid frame.
         */
        if (frame == null) {
            return FAILURE;
        }

        // TODO:
        SystemEvent event = new SystemEvent("PageFault");
        thread.suspend(event);

        if (thread.getStatus() == ThreadKill) {
            return FAILURE;
        }

        /**
         * Reserve the frame & note that we are in process of validating.
         */
        frame.setReserved(thread.getTask());
        page.setValidatingThread(thread);

        if (frame.getPage() != null) {
            if (frame.isDirty()) {
                /**
                 * Swap the frame out since it's initally dirty.
                 */
                swap_page_out(frame, thread);

                /**
                 * If the thread status is kill, must notify & then dispatch.
                 * 
                 * TODO: necessary??
                 */
                if (thread.getStatus() == ThreadKill) {
                    page.notifyThreads();
                    event.notifyThreads();
                    ThreadCB.dispatch();

                    return FAILURE;
                }
            }

            /**
             * Unlock the page.
             */
            if (frame.getLockCount() > 0 && frame.getPage().isValid() == false) {
                while (frame.getLockCount() > 0) {
                    frame.getPage().do_unlock();
                }
            }

            /**
             * Set unreferenced since now cleaned & new.
             */
            frame.setReferenced(false);

            /**
             * Cleanup the page of the frame & finally release it.
             */
            frame.getPage().setValid(false);
            frame.getPage().setFrame(null);
            frame.setPage(null);
        }

        /**
         * Set the page's frame & swap page in.
         */
        page.setFrame(frame);
        swap_page_in(page, thread);

        /**
         * If the thread status is kill, must notify & then dispatch.
         * 
         * TODO: necessary??
         */
        if (thread.getStatus() == ThreadKill) {
            page.notifyThreads();
            page.setValidatingThread(null);
            event.notifyThreads();
            ThreadCB.dispatch();

            return FAILURE;
        }

        /**
         * Set page as valid & unset validating.
         */
        frame.setPage(page);
        page.setValid(true);
        frame.setUnreserved(thread.getTask());

        page.setValidatingThread(null);
        page.notifyThreads();
        event.notifyThreads();
        ThreadCB.dispatch();

        return SUCCESS;
    }

    /**
     * Will find and return a clean page according to the following:
     * 
     * 1. Finds any clean frame with useCount == 0, and returns it.
     * 
     * If none found, then:
     * 
     * 2. Finds any dirty frame with useCount == 0, and returns it.
     * 
     * If none found, then:
     * 
     * 3. Finds any frame and returns it.
     * 
     * @return FrameTableEntry
     */
    private static FrameTableEntry choose_new_frame() {
        FrameTableEntry frame;

        /**
         * Following #1, find clean frame with use count == 0.
         */
        for (int i = 0; i < MMU.getFrameTableSize(); ++i) {
            if (MMU.getFrame(i) != null && MMU.getFrame(i).getPage() == null && MMU.getFrame(i).isReserved() == false
                    && MMU.getFrame(i).getLockCount() == 0 && MMU.getFrame(i).getUseCounts() == 0
                    && MMU.getFrame(i).isDirty() == false) {
                return MMU.getFrame(i);
            }
        }

        /**
         * Following #2, find any frame with use count == 0 (even if dirty).
         */
        for (int i = 0; i < MMU.getFrameTableSize(); ++i) {
            if (MMU.getFrame(i) != null && MMU.getFrame(i).getPage() == null && MMU.getFrame(i).isReserved() == false
                    && MMU.getFrame(i).getLockCount() == 0 && MMU.getFrame(i).getUseCounts() == 0) {
                return MMU.getFrame(i);
            }
        }

        /**
         * Following #2, find any frame with use count == 0 (even if dirty).
         */
        for (int i = MMU.getFrameTableSize() - 1; i >= 0; --i) {
            if (MMU.getFrame(i) != null) {
                return MMU.getFrame(i);
            }
        }

        /**
         * Finally, if unable to find any frame at all - return null.
         */
        return null;
    }

    /**
     * Swap out - & thus making it clean.
     */
    private static void swap_page_out(FrameTableEntry frame, ThreadCB threadCB) {
        /**
         * Get the frame's page and task.
         */
        PageTableEntry page = frame.getPage();
        TaskCB task = frame.getPage().getTask();
        if (page == null || task == null) {
            return;
        }

        /**
         * Get the swapfile, and write out.
         */
        OpenFile swap = task.getSwapFile();
        if (swap == null) {
            return;
        } else {
            swap.write(page.getID(), page, threadCB);
        }

        /**
         * Sets the frame to clean
         */
        frame.setDirty(false);

        return;
    }

    /**
     * Swap in - thus removing specified page.
     */
    private static void swap_page_in(PageTableEntry page, ThreadCB threadCB) {
        /**
         * Get the page's task.
         */
        TaskCB task = page.getTask();
        if (task == null) {
            return;
        }

        /**
         * Get the swapfile, and read in - thus removing it.
         */
        OpenFile swap = task.getSwapFile();
        if (swap == null) {
            return;
        } else {
            swap.read(page.getID(), page, threadCB);
        }

        return;
    }
}
