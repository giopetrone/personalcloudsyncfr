/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.unito.server;

import JaCoP.core.IntVar;
import JaCoP.core.Store;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMin;
import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSelect;
import JaCoP.search.SmallestMax;
import JaCoP.search.SmallestMin;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class TaskStoreSave {

    private Store store; // constraint store
    private ArrayList<IntVar> FDVars;
    private ArrayList<ServerTask> taskList;

    /** creates a new TaskStoreSave (no tasks yet, no constraints)
     *
     */
    public TaskStoreSave() {
        store = new Store();
        FDVars = new ArrayList<IntVar>();
        taskList = new ArrayList<ServerTask>();
    }

    /** Clones a TaskStoreSave, replicating all its tasks, but
     * DOES NOT clone the constraints imposed on the store
     * (they have to be imposed from scratch).
     * constraints have to be reimposed one by one.
     *
     * @param no parameters
     */
    public TaskStoreSave clone() {
        TaskStoreSave clo = new TaskStoreSave();
        for (int i=0; i<taskList.size(); i++) {
            ServerTask t = taskList.get(i);
            clo.addTask(t.clone(clo.getStore(), clo.getFDVars()));
        }
        return clo;
    }

    /** returns the list (arrayList) of Finite Domain Variables
     * defined in the store
     *
     * @return list of FD variables
     */
    public ArrayList<IntVar> getFDVars() {
        return FDVars;
    }

    /** returns the Store embedded in the TaskStoreSave
     *
     * @return Store
     */
    public Store getStore() {
        return store;
    }

    /** adds a new task to the store
     *
     * @param name task name
     * @param st1 initial start time
     * @param st2 final start time
     * @param e1 initial end time
     * @param e2 final end time
     * @param dur duration of the task
     * @return
     */
    public ServerTask addTask(String name, int st1, int st2,
                          int e1, int e2, int dur) {
        ServerTask t = new ServerTask(name,st1,st2,e1,e2,dur,this.store,this.FDVars);
        taskList.add(t);
        return t;
    }

    /** adds a task as an object
     *
     * @param task the task to be added
     */
    public void addTask(ServerTask task)  {
        taskList.add(task);
    }

    /** searchs for a task in the store by name and returns it
     *
     * @param name name of task to be retrieved
     * @return ServerTask object
     */
    public ServerTask getTask(String name) {
        for (int i=0; i<taskList.size(); i++)
            if (name.equalsIgnoreCase(taskList.get(i).getName()))
                return taskList.get(i);
        return null;
    }

    /** returns the list of tasks defined in the store
     *
     * @return arrayList of ServerTask
     */
    public ArrayList<ServerTask> getTasks() {
        return taskList;
    }

    /** prints out the content of all the tasks defined in the store
     *
     */
    public void printTasks() {
        for (int i=0; i<taskList.size(); i++)
            System.out.println(taskList.get(i).toString());
    }

    /** imposes an order constraint between two tasks
     *
     * @param t1  name of task that must occur before
     * @param t2  name of task that must follow
     * @return true if the two tasks exist, false otherwise
     */
    public boolean imposeBefore(String t1, String t2) {
        ServerTask task1 = getTask(t1);
        ServerTask task2 = getTask(t2);
        if (t1==null || t2==null)
            return false;
        else return task1.imposeBefore(task2);
    }

    /** imposes an order constraint between two tasks
     *
     * @param t1  name of task that must follow
     * @param t2  name of task that must precede
     * @return true if the two tasks exist, false otherwise
     */
    public boolean imposeAfter(String t1, String t2) {
        ServerTask task1 = getTask(t1);
        ServerTask task2 = getTask(t2);
        if (t1==null || t2==null)
            return false;
        else return task1.imposeAfter(task2);
    }

    /** imposes a non overlap constraint between two tasks
     *
     * @param t1  name of task
     * @param t2  name of task
     * @return true if the two tasks exist, false otherwise
     */
    public boolean imposeNonOverlap(String t1Name, String t2Name) {
        ServerTask task1 = getTask(t1Name);
        ServerTask task2 = getTask(t2Name);
        if (task1==null || task2==null)
            return false;
        else return task1.imposeNonOverlap(task2);
    }

    /** imposes a non overlap constraint between a list of tasks
     *
     * @param taskNames  list of task names
     * @return true if the tasks exist, false otherwise
     */
    public boolean imposeNonOverlap(ArrayList<String> taskNames) {
        boolean result = false;
        for (int i=0; i<taskNames.size(); i++) {
            ServerTask task1 = getTask(taskNames.get(i));
            if (task1!=null) {
                for (int k=i+1; k<taskNames.size(); k++) {
                    ServerTask task2 = getTask(taskNames.get(k));
                    //System.out.println("non overlap " + task1.getName() + ", " + task2.getName());
                    if (task2!=null)
                        result = task1.imposeNonOverlap(task2);
                }
            }
        }
        return result;
    }

    /* checks consistency of constraints defined in store */
    public boolean checkConsistency() {
        return store.consistency();
    }

    /** generates a schedule consistent with the imposed constraints.
     *  Selects first the Finite State Variables having the smallest
     *  minimum value in their domain (--> assigns tasks as soon
     *  as possible, given their starting time intervals (and respecting
     *  the end constraints).
     *
     * @return true if a solution was found, false otherwise
     */
     public boolean genSchedule() {
        IntVar[] vars = new IntVar[FDVars.size()];
        for (int i=0; i<FDVars.size(); i++) {
            vars[i] = FDVars.get(i);
        }
        // search for a solution and print results
        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select =
                new SimpleSelect<IntVar>(vars,
                        new SmallestMin<IntVar>(),
                        new IndomainMin<IntVar>());
        boolean result = search.labeling(store, select);
        return result;
    }

    /** generates schedule by applying the specified selection
     *  mechanism for the selection of FD vars to be considered
     *  in the exploration of the search space.
     *  If selection mode is "start" it selects variables whose
     *  domain has minimum values first (the earlier, the better).
     *  If selection mode is "end" it selects variables whose
     *  deadline comes first (try to avoid setting tasks
     *  at the very last minute)
     *
     * @param searchMode FD variables selection method (start/end first)
     * @return true if solution exists, false otherwise
     */
    public boolean genSchedule(String searchMode) {
        boolean result = false;
        IntVar[] vars = new IntVar[FDVars.size()];
        for (int i=0; i<FDVars.size(); i++) {
            vars[i] = FDVars.get(i);
        }
        // search for a solution and print results
        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        if(searchMode!=null) {
            if (searchMode.equalsIgnoreCase("start")) {
                SelectChoicePoint<IntVar> select =
                    new SimpleSelect<IntVar>(vars,
                            new SmallestMin<IntVar>(),
                            new IndomainMin<IntVar>());
                result = search.labeling(store, select);
            }
            else if (searchMode.equalsIgnoreCase("end")) {
                SelectChoicePoint<IntVar> select =
                    new SimpleSelect<IntVar>(vars,
                            new SmallestMax<IntVar>(),
                            new IndomainMin<IntVar>());
                result = search.labeling(store, select);
            }
        }
        return result;
    }

}// end class

