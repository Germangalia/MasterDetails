package com.ggalia84.masterdetails;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import com.ggalia84.masterdetails.Adapters.ListAdapter;
import com.ggalia84.masterdetails.Models.TodoItem;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    private static final String TODO_LIST = "todo_list_master_detail";
    private static final String SHARED_PREFERENCES_TODOS = "SP_TODO_LIST_MD";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public static boolean mTwoPane;
    public static TodoArrayList items;
    public static final Map<String, TodoItem> ITEM_MAP = new HashMap<String, TodoItem>();
    private View positiveAction;
    private String taskName;
    private String taskDescription;
    private int priority;
    private Gson gson;
    private ListAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private SharedPreferences todosSharedPreference;
    private String todoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                loadTask();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        loadTask();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fabrm = (FloatingActionButton) findViewById(R.id.fabremove);
        FloatingActionButton fabadd = (FloatingActionButton) findViewById(R.id.fabadd);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void loadTask() {
        swipeContainer.setRefreshing(false);

        // Restore preferences
        todosSharedPreference = getSharedPreferences(SHARED_PREFERENCES_TODOS, 0);
        todoList = todosSharedPreference.getString(TODO_LIST, null);

        if (todoList == null) {
            String initial_json = "[{\"id\":\"Example task ID\", name:\"Example task\", \"done\": false, \"priority\": 2, \"description\":\"Example task description\"}]";
            SharedPreferences.Editor editor = todosSharedPreference.edit();
            editor.putString(TODO_LIST, initial_json);
            editor.commit();
            todoList = todosSharedPreference.getString(TODO_LIST, null);
        }
        updateJson();
    }

    public void updateJson(){
        Type arrayTodoList = new TypeToken<TodoArrayList>() {}.getType();
        this.gson = new Gson();
        TodoArrayList temp = gson.fromJson(todoList, arrayTodoList);

        if (temp != null) {
            items = temp;

        } else {
            //Error TODO
        }

        adapter = new ListAdapter(items, this);

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        swipeContainer.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //When stop app save tasks
        if (items == null) {
            return;
        }

        String tasksToSave = gson.toJson(items);

        SharedPreferences todosSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_TODOS, 0);
        SharedPreferences.Editor editor = todosSharedPreferences.edit();
        editor.putString(TODO_LIST, tasksToSave);
        editor.apply();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(adapter);
    }

    private static void addItem(TodoItem item) {
        items.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public void showAddForm(View view) {

        taskName = " ";
        taskDescription = " ";
        priority = 2;

        final EditText taskNameText;
        final EditText taskDescriptionText;

        MaterialDialog dialog = new MaterialDialog.Builder(this).
                title("Add new Task").
                customView(R.layout.form_add_task, true).
                negativeText("Cancel").
                positiveText("Add").
                negativeColor(Color.parseColor("#ff3333")).
                positiveColor(Color.parseColor("#2196F3")).
                onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {

                        // Task priority
                        RadioGroup taskPriority = (RadioGroup) dialog.findViewById(R.id.task_priority);

                        switch (taskPriority.getCheckedRadioButtonId()) {
                            case R.id.task_priority_urgent:
                                priority = 1;
                                break;
                            case R.id.task_priority_important_not_urgent:
                                priority = 2;
                                break;
                            case R.id.task_priority_not_urgent:
                                priority = 3;
                                break;
                        }
                        final TodoItem todoItem = new TodoItem(taskName + taskDescription, taskName, false, priority, taskDescription);
                        addItem(todoItem);
                        adapter.notifyDataSetChanged();
                    }
                }).


                build();

        dialog.show();

        taskNameText = (EditText) dialog.getCustomView().findViewById(R.id.task_tittle);
        taskDescriptionText = (EditText) dialog.getCustomView().findViewById(R.id.task_description);

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);

        taskNameText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskName = s.toString();
                positiveAction.setEnabled(taskName.trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        taskDescriptionText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskDescription = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void removeTask(View view) {

        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).done) {
                items.remove(i);
            }
        }

        adapter.notifyDataSetChanged();
    }
}