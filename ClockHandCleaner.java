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
import osp.FileSys.OpenFile;

public class ClockHandCleaner implements DaemonInterface {
    @Override
    public void unleash(ThreadCB threadCB) {
        /**
         * Sweep through the eligible frames and decrement their use counts.
         */
        this.sweep_eligible_frames(threadCB);

        return;
    }

    /**
     * Sweeps through frames, and decrements the use count of them by 1.
     */
    private void sweep_eligible_frames(ThreadCB threadCB) {
        PageTableEntry page;
        FrameTableEntry frame;

        /**
         * Iterate through the frames in the frame table & swap out ones that have
         * useCount set to 0. & decrement useCounts of frames with useCount > 0.
         */
        for (int i = 0; i < MMU.getFrameTableSize(); ++i) {
            frame = MMU.getFrame(i);
            page = MMU.getFrame(i).getPage();

            /**
             * If a frame is eligible, decrement its useCount by 1, & cannot be less than 0.
             */
            if (frame.getPage() != null && frame.isDirty() == true && frame.isReserved() == false
                    && frame.getLockCount() == 0) {
                if (frame.getUseCounts() <= 0) {
                    // swap the frame out - thus making it clean.
                    this.swap_out_page(frame, threadCB);
                } else {
                    frame.setUseCounts(frame.getUseCounts() - 1);
                }
            }
        }

        return;
    }

    /**
     * Swap a frame out - thus making it clean.
     */
    private void swap_out_page(FrameTableEntry frame, ThreadCB threadCB) {
        /**
         * Get the frame's page and task.
         */
        PageTableEntry page = frame.getPage();
        TaskCB task = frame.getPage().getTask();

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
}
