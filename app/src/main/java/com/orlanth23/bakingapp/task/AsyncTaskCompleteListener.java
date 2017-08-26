package com.orlanth23.bakingapp.task;

/**
 * Created by orlanth23 on 24/08/2017.
 *
 * Thanks to :
 * http://www.jameselsey.co.uk/blogs/techblog/extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/
 */

public interface AsyncTaskCompleteListener<T>
{
    /**
     * Invoked when the AsyncTask has completed its execution.
     * @param result The resulting object from the AsyncTask.
     */
    void onTaskComplete(T result);
}
