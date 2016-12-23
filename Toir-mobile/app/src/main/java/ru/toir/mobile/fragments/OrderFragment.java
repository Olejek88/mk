package ru.toir.mobile.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.db.adapters.OperationAdapter;
import ru.toir.mobile.db.adapters.OperationVerdictAdapter;
import ru.toir.mobile.db.adapters.OrderAdapter;
import ru.toir.mobile.db.adapters.OrderStatusAdapter;
import ru.toir.mobile.db.adapters.OrderVerdictAdapter;
import ru.toir.mobile.db.adapters.TaskAdapter;
import ru.toir.mobile.db.adapters.TaskStageAdapter;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OperationVerdict;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.OrderVerdict;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.TaskStageStatus;
import ru.toir.mobile.db.realm.TaskStages;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.rest.IServiceProvider;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.TaskServiceProvider;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;

public class OrderFragment extends Fragment implements View.OnClickListener {
    private Orders selectedOrder;
    private Tasks selectedTask;
    private TaskStages selectedStage;
    private Operation selectedOperation;

    private OrderAdapter orderAdapter;
    private TaskAdapter taskAdapter;
    private TaskStageAdapter taskStageAdapter;
    private OperationAdapter operationAdapter;
    private Button submit;

    private String TAG = "OrderFragment";
    private Realm realmDB;
    private View rootView;
    private int Level = 0;
    private String currentOrderUuid = "";
    private String currentTaskUuid = "";
    private String currentTaskStageUuid = "";

    private int     currentOperationId = 0;
    private long    startTime = 0;
    private boolean firstLaunch = true;
    ListView mainListView;
    private ListViewClickListener mainListViewClickListener = new ListViewClickListener();
    private ListViewLongClickListener mainListViewLongClickListener = new ListViewLongClickListener();
    //private ReferenceSpinnerListener filterSpinnerListener = new ReferenceSpinnerListener();

    private ProgressDialog processDialog;
    private RfidDialog rfidDialog;
    // фильтр для получения сообщений при получении нарядов с сервера
    private IntentFilter mFilterGetTask = new IntentFilter(
            TaskServiceProvider.Actions.ACTION_GET_TASK);
    // TODO решить нужны ли фильтры на все возможные варианты отправки
    // состояния/результатов
    // фильтр для получения сообщений при получении нарядов с сервера
    private IntentFilter mFilterSendTask = new IntentFilter(
            TaskServiceProvider.Actions.ACTION_TASK_SEND_RESULT);
    private BroadcastReceiver mReceiverGetTask = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int provider = intent.getIntExtra(
                    ProcessorService.Extras.PROVIDER_EXTRA, 0);
            Log.d(TAG, "" + provider);
            if (provider == ProcessorService.Providers.TASK_PROVIDER) {
                int method = intent.getIntExtra(
                        ProcessorService.Extras.METHOD_EXTRA, 0);
                Log.d(TAG, "" + method);
                if (method == TaskServiceProvider.Methods.GET_TASK) {
                    boolean result = intent.getBooleanExtra(
                            ProcessorService.Extras.RESULT_EXTRA, false);
                    Bundle bundle = intent
                            .getBundleExtra(ProcessorService.Extras.RESULT_BUNDLE);
                    Log.d(TAG, "boolean result" + result);

                    if (result) {
                        /*
                         * нужно видимо что-то дёрнуть чтоб уведомить о том что
						 * наряд(ы) получены вероятно нужно сделать попытку
						 * отправить на сервер информацию о полученых нарядах
						 * (которые изменили свой статус на "В работе")
						 */

                        // ообщаем количество полученных нарядов
                        int count = bundle
                                .getInt(TaskServiceProvider.Methods.RESULT_GET_TASK_COUNT);
                        if (count > 0) {
                            Toast.makeText(getActivity(),
                                    "Количество нарядов " + count,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Нарядов нет.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // сообщаем описание неудачи
                        String message = bundle
                                .getString(IServiceProvider.MESSAGE);
                        Toast.makeText(getActivity(),
                                "Ошибка при получении нарядов." + message,
                                Toast.LENGTH_LONG).show();
                    }

                    // закрываем диалог получения наряда
                    processDialog.dismiss();
                    getActivity().unregisterReceiver(mReceiverGetTask);
                    initView();
                }
            }

        }
    };
    private BroadcastReceiver mReceiverSendTaskResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int provider = intent.getIntExtra(
                    ProcessorService.Extras.PROVIDER_EXTRA, 0);
            Log.d(TAG, "" + provider);
            if (provider == ProcessorService.Providers.TASK_PROVIDER) {
                int method = intent.getIntExtra(
                        ProcessorService.Extras.METHOD_EXTRA, 0);
                Log.d(TAG, "" + method);
                if (method == TaskServiceProvider.Methods.TASK_SEND_RESULT) {
                    boolean result = intent.getBooleanExtra(
                            ProcessorService.Extras.RESULT_EXTRA, false);
                    Log.d(TAG, "" + result);
                    if (result) {
                        Toast.makeText(getActivity(), "Результаты отправлены.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(),
                                "Ошибка при отправке результатов.",
                                Toast.LENGTH_LONG).show();
                    }

                    // закрываем диалог получения наряда
                    processDialog.dismiss();
                    getActivity().unregisterReceiver(mReceiverSendTaskResult);
                }
            }
        }
    };

    public static OrderFragment newInstance() {
        return (new OrderFragment());
    }

    /*
 * (non-Javadoc)
 *
 * @see
 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
 * android.view.ViewGroup, android.os.Bundle)
 */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.orders_layout, container,
                false);

        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Наряды");
        submit = (Button)rootView.findViewById(R.id.finishButton);
        submit.setOnClickListener(this);
        submit.setVisibility(View.GONE);

        realmDB = Realm.getDefaultInstance();

        mainListView = (ListView) rootView
                .findViewById(R.id.list_view);

        setHasOptionsMenu(true);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.d(TAG, "OrderFragment !!! back pressed!!!");
                    if (Level == 1) {
                        initView();
                    }
                    return true;
                }
                return false;
            }
        });

        // так как обработчики пока одни на всё, ставим их один раз
        mainListView.setOnItemClickListener(mainListViewClickListener);
        mainListView.setOnItemLongClickListener(mainListViewLongClickListener);
        //mainListView.setOnItemLongClickListener(mainListViewLongClickListener);

        mainListView.setLongClickable(true);

        initView();
        return rootView;
    }

    private void initView() {

        Level = 0;
        fillListViewOrders(null, null);
    }

    // Orders----------------------------------------------------------------------------------------
    private void fillListViewOrders(String orderStatus, String orderByField) {
        User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
        if (user == null) {
            Toast.makeText(getActivity(), "Нет такого пользователя!",
                    Toast.LENGTH_SHORT).show();
        } else {
            RealmResults<Orders> orders;
            orders = realmDB.where(Orders.class).findAll();
            orderAdapter = new OrderAdapter(getContext(), orders);
            mainListView.setAdapter(orderAdapter);
        }
        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.GONE);
        }
    }

    // Tasks----------------------------------------------------------------------------------------
    private void fillListViewTasks(Orders order) {
        RealmResults<Tasks> tasks;
        RealmQuery<Tasks> q = realmDB.where(Tasks.class);
        boolean first = true;
        for (Tasks task : order.getTasks()) {
            long id = task.get_id();
            if (first) {
                q = q.equalTo("_id", id);
                first = false;
            } else {
                q = q.or().equalTo("_id", id);
            }
        }

        tasks = q.findAll();

        taskAdapter = new TaskAdapter(getContext(), tasks);
        mainListView.setAdapter(taskAdapter);
        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(order.getTitle());
        }
        submit.setVisibility(View.GONE);
    }

    // TaskStages----------------------------------------------------------------------------------------
    private void fillListViewTaskStage(Tasks task) {
        RealmResults<TaskStages> stages;
        RealmQuery<TaskStages> q = realmDB.where(TaskStages.class);
        boolean first = true;
        for (TaskStages stage : task.getTaskStages()) {
            long id = stage.get_id();
            if (first) {
                q = q.equalTo("_id", id);
                first = false;
            } else {
                q = q.or().equalTo("_id", id);
            }
        }

        stages = q.findAll();

        taskStageAdapter = new TaskStageAdapter(getContext(), stages);
        mainListView.setAdapter(taskStageAdapter);
        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(task.getTaskTemplate().getTitle());
        }
        submit.setVisibility(View.GONE);
    }

    // Operations----------------------------------------------------------------------------------------
    private void fillListViewOperations(TaskStages stage) {
        RealmResults<Operation> operations;
        RealmQuery<Operation> q = realmDB.where(Operation.class);
        boolean first = true;
        for (Operation operation : stage.getOperations()) {
            long id = operation.get_id();
            if (first) {
                q = q.equalTo("_id", id);
                first = false;
            } else {
                q = q.or().equalTo("_id", id);
            }
        }

        operations = q.findAll();

        operationAdapter = new OperationAdapter(getContext(), operations);
        mainListView.setAdapter(operationAdapter);
        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(stage.getTaskStageTemplate().getTitle());
        }
        //mainListView.setOnItemClickListener(mainListViewClickListener);
    }

    // Start Operations----------------------------------------------------------------------------------------
    void startOperations() {
        int totalOperationCount;
        // запрещаем все операции кроме первой
        if (operationAdapter != null) {
            totalOperationCount = operationAdapter.getCount();
            operationAdapter.setItemEnable(0, true);
            for (int i = 0; i < totalOperationCount; i++) {
                 if (i > 0)
                     operationAdapter.setItemEnable(i, false);
                 //checkBox = (CheckBox)operationAdapter.getView(i,null,mainListView);
                 //checkBox.setOnClickListener(new onCheckBoxClickListener());
               }
           }
        // время начала работы (приступаем к первой операции и нехрен тормозить)
        startTime = System.currentTimeMillis();
        taskTimer.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // добавляем элемент меню для получения новых нарядов
        MenuItem getTaskNew = menu.add("Получить новые наряды");
        getTaskNew
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.d(TAG, "Получаем новые наряды.");
//                        TaskServiceHelper tsh = new TaskServiceHelper(
//                                getActivity().getApplicationContext(),
//                                TaskServiceProvider.Actions.ACTION_GET_TASK);
//
//                        getActivity().registerReceiver(mReceiverGetTask,
//                                mFilterGetTask);
//
//                        tsh.GetTaskNew();

                        AuthorizedUser user = AuthorizedUser.getInstance();
                        Call<List<Orders>> call = ToirAPIFactory.getOrderService()
                                .order("bearer " + user.getToken(), user.getUuid(), "");
                        call.enqueue(new Callback<List<Orders>>() {
                            @Override
                            public void onResponse(Response<List<Orders>> response, Retrofit retrofit) {
                                // ообщаем количество полученных нарядов
                                List<Orders> orders = response.body();
                                int count = orders.size();
                                if (count > 0) {
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    realm.copyToRealmOrUpdate(orders);
                                    realm.commitTransaction();
                                    Toast.makeText(getActivity(),
                                            "Количество нарядов " + count,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Нарядов нет.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                processDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                // сообщаем описание неудачи
                                Toast.makeText(getActivity(),
                                        "Ошибка при получении нарядов.",
                                        Toast.LENGTH_LONG).show();
                                processDialog.dismiss();
                            }
                        });

                        // показываем диалог получения наряда
                        processDialog = new ProgressDialog(getActivity());
                        processDialog.setMessage("Получаем наряды");
                        processDialog.setIndeterminate(true);
                        processDialog
                                .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        processDialog.setCancelable(false);
                        processDialog.setButton(
                                DialogInterface.BUTTON_NEGATIVE, "Отмена",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
//                                        getActivity().unregisterReceiver(mReceiverGetTask);
                                        Toast.makeText(getActivity(),
                                                "Получение нарядов отменено",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        processDialog.show();
                        return true;
                    }
                });

        // добавляем элемент меню для получения "архивных" нарядов
        MenuItem getTaskDone = menu.add("Получить сделанные наряды");
        getTaskDone
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.d(TAG, "Получаем сделанные наряды.");
//                        TaskServiceHelper tsh = new TaskServiceHelper(
//                                getActivity().getApplicationContext(),
//                                TaskServiceProvider.Actions.ACTION_GET_TASK);
//
//                        getActivity().registerReceiver(mReceiverGetTask,
//                                mFilterGetTask);
//
//                        tsh.GetTaskDone();

                        // показываем диалог получения наряда
                        processDialog = new ProgressDialog(getActivity());
                        processDialog.setMessage("Получаем наряды");
                        processDialog.setIndeterminate(true);
                        processDialog
                                .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        processDialog.setCancelable(false);
                        processDialog.setButton(
                                DialogInterface.BUTTON_NEGATIVE, "Отмена",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
//                                        getActivity().unregisterReceiver(mReceiverGetTask);
                                        Toast.makeText(getActivity(),
                                                "Получение нарядов отменено",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        processDialog.show();
                        return true;
                    }
                });

        // добавляем элемент меню для отправки результатов выполнения нарядов
        MenuItem sendTaskResultMenu = menu.add("Отправить результаты");
        sendTaskResultMenu
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

//                        TaskDBAdapter adapter = new TaskDBAdapter(
//                                new ToirDatabaseContext(getActivity()));
//                        List<Task> tasks;
//                        String currentUserUuid = AuthorizedUser.getInstance()
//                                .getUuid();
//
//                        // проверяем наличие не законченных нарядов
//                        tasks = adapter.getOrdersByUser(currentUserUuid,
//                                TaskStatusDBAdapter.Status.IN_WORK, "");
//                        if (tasks.size() > 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                getContext());

                        dialog.setTitle("Внимание!");
                        dialog.setMessage("Есть "/* + tasks.size()*/
                                + " наряда в процессе выполнения.\n"
                                + "Отправить выполненные наряды?");
                        dialog.setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {

                                        sendCompleteTask();
                                        dialog.dismiss();
                                    }
                                });
                        dialog.setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {

                                        dialog.dismiss();
                                    }
                                });
                        dialog.show();
//                        }

                        return true;
                    }
                });
    }

    /**
     * Отправка всех выполненных нарядов на сервер
     */
    private void sendCompleteTask() {

//        TaskDBAdapter adapter = new TaskDBAdapter(new ToirDatabaseContext(
//                getActivity()));
//        List<Task> tasks;
//        String currentUserUuid = AuthorizedUser.getInstance().getUuid();
//
//        tasks = adapter.getTaskByUserAndUpdated(currentUserUuid);
//
//        if (tasks == null) {
//            Toast.makeText(getActivity(), "Нет результатов для отправки.",
//                    Toast.LENGTH_SHORT);
//            return;
//        }
//
//        String[] sendTaskUuids = new String[tasks.size()];
//        int i = 0;
//        for (Task task : tasks) {
//            sendTaskUuids[i] = task.getUuid();
//            // устанавливаем дату попытки отправки
//            // (пока только для наряда)
//            task.setAttempt_send_date(Calendar.getInstance().getTime()
//                    .getTime());
//            adapter.replace(task);
//            i++;
//        }
//
//        getActivity()
//                .registerReceiver(mReceiverSendTaskResult, mFilterSendTask);
//
//        TaskServiceHelper tsh = new TaskServiceHelper(getActivity(),
//                TaskServiceProvider.Actions.ACTION_TASK_SEND_RESULT);
//        tsh.SendTaskResult(sendTaskUuids);

        // показываем диалог отправки результатов
        processDialog = new ProgressDialog(getActivity());
        processDialog.setMessage("Отправляем результаты");
        processDialog.setIndeterminate(true);
        processDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        processDialog.setCancelable(false);
        processDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        getActivity().unregisterReceiver(mReceiverGetTask);
                        Toast.makeText(getActivity(),
                                "Отправка результатов отменена",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        processDialog.show();
    }

    public class ListViewClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View selectedItemView,
                                int position, long id) {

            // находимся на "экране" нарядов
            if (Level == 0) {
                if (orderAdapter != null) {
                    selectedOrder = orderAdapter.getItem(position);
                    if (selectedOrder != null) {
                        currentOrderUuid = selectedOrder.getUuid();
                        fillListViewTasks(selectedOrder);
                        Level = 1;
                    }
                }

                return;
            }
            // Tasks
            if (Level == 1) {
                if (taskAdapter != null) {
                    selectedTask = taskAdapter.getItem(position);
                    if (selectedTask != null) {
                        Toast.makeText(getContext(), "Нужно поднести метку", Toast.LENGTH_LONG).show();
                        final String expectedTagId = selectedTask.getEquipment().getTagId();
                        Log.d(TAG, "Ожидаемаея метка: " + expectedTagId);
                        Handler handler = new Handler(new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                if (message.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
                                    String tagId = ((String) message.obj).substring(4);
                                    Log.d(TAG, "Ид метки получили: " + tagId);
                                    if (expectedTagId.equals(tagId)) {
                                        currentTaskUuid = selectedTask.getUuid();
                                        fillListViewTaskStage(selectedTask);
                                        Level = 2;
                                    } else {
                                        Toast.makeText(getContext(),
                                                "Не верное оборудование!", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                } else {
                                    Log.d(TAG, "Ошибка чтения метки!");
                                    Toast.makeText(getContext(),
                                            "Ошибка чтения метки.", Toast.LENGTH_SHORT)
                                            .show();

                                }

                                // закрываем диалог
                                rfidDialog.dismiss();
                                return true;
                            }
                        });

                        rfidDialog = new RfidDialog();
                        rfidDialog.setHandler(handler);
                        rfidDialog.readTagId();
                        rfidDialog.show(getActivity().getFragmentManager(), TAG);

                    }
                }

                return;
            }
            // TaskStage
            if (Level == 2) {
                if (taskStageAdapter != null) {
                    selectedStage = taskStageAdapter.getItem(position);
                    if (selectedStage != null) {
                        currentTaskStageUuid = selectedStage.getUuid();
                        fillListViewOperations(selectedStage);
                        submit.setVisibility(View.VISIBLE);
                        Level = 3;
                        startOperations();
                    }
                }
                return;
            }

            // Operation
            if (Level == 3) {
                if (operationAdapter != null) {
                    selectedOperation = operationAdapter.getItem(position);
                    if (selectedOperation != null) {
                        operationAdapter.setItemVisibility(position);
                        operationAdapter.notifyDataSetChanged();
                        //mainListView.invalidateViews();
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int totalOperationCount = 0, completedOperationCount = 0;
        final long currentTime = System.currentTimeMillis();
        //long totalTimeElapsed = currentTime - startTime;
        CheckBox checkBox;
        /*
        final String taskStageStatusUuid2="8ee8a4f8-5c98-4444-86ed-133923132322";
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TaskStageStatus taskStageStatusComplete;
                taskStageStatusComplete = realmDB.createObject(TaskStageStatus.class);
                taskStageStatusComplete.set_id(2);
                taskStageStatusComplete.setUuid(taskStageStatusUuid2);
                taskStageStatusComplete.setTitle("Выполнен");
            }
        });*/
        final TaskStageStatus taskStageComplete;
        taskStageComplete = realmDB.where(TaskStageStatus.class).equalTo("title","Выполнен").findFirst();

        if (operationAdapter != null)
            totalOperationCount = operationAdapter.getCount();

        for (int i = 0; i < totalOperationCount; i++) {
            checkBox = (CheckBox) getViewByPosition(i,mainListView).findViewById(R.id.operation_status);
            if (checkBox.isChecked()) {
                final Operation operation = operationAdapter.getItem(i);
                if (operation != null) {
                    completedOperationCount++;
                } else
                    Log.d(TAG, "Операция под индексом " + i + " не найдена");
            }

        }
        // все операции выполнены
        if (totalOperationCount == completedOperationCount) {
            final TaskStages taskStage = realmDB.where(TaskStages.class).equalTo("uuid", currentTaskStageUuid).findFirst();
            if (taskStage != null) {
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        taskStage.setTaskStageStatus(taskStageComplete);
                        //taskStage.setEndDate();
                        taskStage.setEndDate(new Date(currentTime));
                    }
                });
            }
        }
        else {
            // TODO показать диалог с коментарием и выбором вердикта
        }

        taskTimer.cancel();
        firstLaunch = true;
        currentOperationId = 0;

        if (selectedTask != null) {
            currentTaskStageUuid = selectedStage.getUuid();
            currentTaskUuid = selectedTask.getUuid();
            fillListViewTaskStage(selectedTask);
            submit.setVisibility(View.GONE);
            Level = 2;
        }
    }

    CountDownTimer taskTimer = new CountDownTimer(1000000000, 1000) {
        public void onTick(long millisUntilFinished) {
            TextView textTime;
            long currentTime = System.currentTimeMillis();
            if (operationAdapter != null && currentOperationId < operationAdapter.getCount()) {
                //textTime = (TextView) mainListView.getChildAt(currentOperationId).findViewById(R.id.op_time);
                textTime = (TextView) getViewByPosition(currentOperationId, mainListView).findViewById(R.id.op_time);
                textTime.setText((int)(currentTime-startTime)/1000 + "сек.");
                if (firstLaunch)
                    onStart();
            }
        }
        public void onStart() {
            int totalOperationCount;
            CheckBox checkBox;
            if (operationAdapter != null) {
                totalOperationCount = operationAdapter.getCount();
                for (int i = 0; i < totalOperationCount; i++) {
                    checkBox = (CheckBox) mainListView.getChildAt(i).findViewById(R.id.operation_status);
                    checkBox.setOnClickListener(new onCheckBoxClickListener(i));
                }
                firstLaunch = false;
            }
        }
        public void onFinish() {
        }
    };

    public class onCheckBoxClickListener implements View.OnClickListener {
        int position;

        public onCheckBoxClickListener(int pos) {
            this.position = pos;
        }

        @Override
        public void onClick(View arg) {
            if (position!=currentOperationId) return;
            if (!operationAdapter.getItemEnable(position)) return;
            TextView textTime;
            final long currentTime = System.currentTimeMillis();
            final OperationStatus operationStatusCompleted;
            final OperationVerdict operationVerdictCompleted;
            operationStatusCompleted = realmDB.where(OperationStatus.class).findFirst();
            operationVerdictCompleted = realmDB.where(OperationVerdict.class).findFirst();
            if (operationStatusCompleted == null)
                Log.d(TAG, "Статус: операция завершена отсутствует в словаре!");
            if (operationVerdictCompleted == null)
                Log.d(TAG, "Вердикт: операция завершена отсутствует в словаре!");

            if (operationAdapter != null) {
                textTime = (TextView) mainListView.getChildAt(currentOperationId).findViewById(R.id.op_time);
                textTime.setText((int)(currentTime-startTime)/1000 + "сек.");

                if (currentOperationId < operationAdapter.getCount()) {
                    operationAdapter.setItemEnable(currentOperationId+1, true);
                    operationAdapter.setItemVisibility(currentOperationId);
                    operationAdapter.setItemVisibility(currentOperationId+1);
                    startTime = System.currentTimeMillis();
                    currentOperationId++;
                }

                final Operation operation = operationAdapter.getItem(position);
                if (operation != null) {
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            operation.setEndDate(new Date(currentTime));
                            operation.setStartDate(new Date(startTime));
                            operation.setOperationStatus(operationStatusCompleted);
                            operation.setOperationVerdict(operationVerdictCompleted);
                        }
                    });
                    if (((currentTime - startTime) / 1000) <= operation.getOperationTemplate().getNormative())
                         textTime.setBackgroundColor(Color.GREEN);
                    else
                         textTime.setBackgroundColor(Color.RED);
                    // перезапоминаем таймер
                    startTime = System.currentTimeMillis();
                    operationAdapter.setItemEnable(position,false);
                }
            }
        }
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }


    public class ListViewLongClickListener implements
            AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {

            // находимся на "экране" с операциями
            if (Level == 3) {
                //String operationStatus = operation.getOperation_status_uuid();
                // менять произвольно статус операции позволяем только если
                // статус операции "Новая" или "В работе"
                Operation operation = (Operation) parent.getItemAtPosition(position);
                if (!operation.getOperationStatus().getUuid().equals(OperationAdapter.Status.NEW) ||
                        !operation.getOperationStatus().getUuid().equals(OperationAdapter.Status.IN_WORK)) {
                    // показываем диалог изменения статуса
                    closeOperationManual(operation);
                } else {
                    // операция уже выполнена, изменить статус нельзя
                    // сообщаем об этом
                    AlertDialog.Builder dialog = new AlertDialog.Builder(
                            getContext());
                    dialog.setTitle("Внимание!");
                    dialog.setMessage("Изменить статус операции нельзя!");
                    dialog.setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }

            } else if (Level == 0) {
                // находимся на экране с нарядами
                Orders order = orderAdapter.getItem(position);
                OrderStatus orderStatus;
                // проверяем статус наряда
                if (order != null) {
                    orderStatus = order.getOrderStatus();
                    if (orderStatus != null) {
                        if (orderStatus.getUuid().equals(OrderStatusAdapter.Status.COMPLETE)
                                || order.getOrderStatus().getUuid()
                                .equals(OrderStatusAdapter.Status.UN_COMPLETE)) {

                            // наряд уже закрыт, изменить статус нельзя
                            // сообщаем об этом
                            AlertDialog.Builder dialog = new AlertDialog.Builder(
                                    getContext());
                            dialog.setTitle("Внимание!");
                            dialog.setMessage("Изменить статус наряда уже нельзя!");
                            dialog.setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            dialog.show();

                        } else {
                            // наряд можно закрыть принудительно
                            closeOrderManual(order);
                        }
                    }
                    else
                        closeOrderManual(order);
                }
            }
            return true;
        }
    }

    /**
     * Диалог изменения статуса операции
     *
     * @param operation - операция для отмены
     */
    private void closeOperationManual(final Operation operation) {
        final OperationStatus operationStatusUnComplete;
        operationStatusUnComplete = realmDB.where(OperationStatus.class).findFirst();

        // диалог для отмены операции
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View myView = inflater.inflate(R.layout.operation_cancel_dialog, null);
        final Spinner operationVerdictSpinner;
        // список статусов операций в выпадающем списке для выбора
        RealmResults<OperationVerdict> operationVerdict = realmDB.where(OperationVerdict.class).findAll();
        operationVerdictSpinner = (Spinner) myView.findViewById(R.id.simple_spinner);
        OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(getContext(), operationVerdict);
        operationVerdictAdapter.notifyDataSetChanged();
        operationVerdictSpinner.setAdapter(operationVerdictAdapter);

        dialog.setView(myView);
        dialog.setTitle("Отмена операции");

        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final OperationVerdict verdict = (OperationVerdict) operationVerdictSpinner.getSelectedItem();
                // выставляем выбранный статус
                realmDB.executeTransaction(new Realm.Transaction() {
                                               @Override
                                               public void execute(Realm realm) {
                                                   operation.setOperationStatus(operationStatusUnComplete);
                                                   operation.setOperationVerdict(verdict);
                                               }
                                           });
                // TODO обновляем содержимое курсора
                // закрываем диалог
                dialog.dismiss();
            }
        };

        dialog.setPositiveButton(android.R.string.ok, okListener);
        dialog.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        //operationTypeSpinner.setOnItemSelectedListener(new ReferenceSpinnerListener());
        dialog.show();
    }

    /**
     * Диалог ручного закрытия наряда
     *
     * @param order
     */
    private void closeOrderManual(final Orders order) {
        final Spinner orderVerdictSpinner;
        // диалог для отмены операции
        final OrderStatus orderStatusUnComplete;
        final TaskStatus taskStatusUnComplete;
        final TaskStageStatus taskStageStatusUnComplete;
        final OperationStatus operationStatusUnComplete;

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View myView = inflater.inflate(R.layout.operation_cancel_dialog, null);
        // список статусов нарядов в выпадающем списке для выбора
        RealmResults<OrderVerdict> orderVerdict = realmDB.where(OrderVerdict.class).findAll();
        orderVerdictSpinner = (Spinner) myView.findViewById(R.id.simple_spinner);
        OrderVerdictAdapter orderVerdictAdapter = new OrderVerdictAdapter(getContext(), orderVerdict);
        orderVerdictAdapter.notifyDataSetChanged();
        orderVerdictSpinner.setAdapter(orderVerdictAdapter);

        // TODO заменить на реальные статусы "не выполнено"
        orderStatusUnComplete = realmDB.where(OrderStatus.class).findFirst();
        taskStageStatusUnComplete = realmDB.where(TaskStageStatus.class).findFirst();
        operationStatusUnComplete = realmDB.where(OperationStatus.class).findFirst();
        taskStatusUnComplete = realmDB.where(TaskStatus.class).equalTo("_id",1).findFirst();

        dialog.setView(myView);
        dialog.setTitle("Закрытие наряда");
        dialog.setMessage("Всем не законченным задачам будет установлен статус \"Не выполнена\""
                + "\n" + "Закрыть наряд?");

        dialog.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
						/*
						 * закрываем наряд, в зависимости от статуса выполнения
						 * операции выставляем статус наряда
						 */

                        for (final Tasks task : order.getTasks()) {
                            // TODO заменить на реальный статус "не закончена"
                            realmDB.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    task.setTaskStatus(taskStatusUnComplete);
                                }
                            });
                            for (final TaskStages taskStages : task.getTaskStages()) {
                                // TODO заменить на реальный статус "не закончена"
                                realmDB.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        taskStages.setTaskStageStatus(taskStageStatusUnComplete);
                                    }
                                });
                                for (final Operation operation : taskStages.getOperations()) {
                                        // TODO заменить на реальный статус "не закончена"
                                        realmDB.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                operation.setOperationStatus(operationStatusUnComplete);
                                            }
                                        });
                                    }
                                }
                            }
                        final OrderVerdict verdict = (OrderVerdict) orderVerdictSpinner.getSelectedItem();
                        realmDB.executeTransaction(new Realm.Transaction() {
                                                       @Override
                                                       public void execute(Realm realm) {
                                                           order.setCloseDate(new Date());
                                                           order.setUpdate(1);
                                                           order.setOrderStatus(orderStatusUnComplete);
                                                           order.setOrderVerdict(verdict);
                                                       }
                                                   });
                            //fillListViewTask(null, null);
                            dialog.dismiss();
                        }
                });

        dialog.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

}



