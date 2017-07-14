/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.exceptions;

/**
 *
 * @author Justin Swanson
 */
public class MissingMaster extends Exception {

    /**
     *
     */
    public MissingMaster() {
    }

    /**
     *
     * @param msg
     */
    public MissingMaster(String msg) {
        super(msg);
    }
}