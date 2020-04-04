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

    }

    /**
     * Frees up main memory occupied by the task. Then unreserves the freed pages,
     * if necessary.
     * 
     * @OSPProject Memory
     */
    public void do_deallocateMemory() {
        // your code goes here

    }

    /*
     * Feel free to add methods/fields to improve the readability of your code
     */

}

/*
 * Feel free to add local classes to improve the readability of your code
 */
