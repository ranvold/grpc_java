package com.example.ui;

import com.example.component.Column;
import com.example.component.Row;
import com.example.component.TableData;
import com.example.component.column.*;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import org.example.*;
import com.example.component.column.ColumnType;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class DBMS {

    private static DBMS instance;
    private static String databaseName = "Відкрийте або створіть базу данних";
    JFrame frame;
    public static JTabbedPane tabbedPane;
    public JMenuBar menuBar;
    public JMenuItem deleteTableMenuItem;
    public JMenuItem addRowMenuItem;
    public JMenuItem addColumnMenuItem;
    public JMenuItem deleteRowMenuItem;
    public JMenuItem deleteColumnMenuItem;
    public JMenuItem createTableMenuItem;
    public JMenuItem tablesMultiply;

    public JMenu tableMenu = new JMenu("Таблиця");
    public JMenu columnMenu = new JMenu("Колонка");
    public JMenu rowMenu = new JMenu("Рядок");

    public JLabel databaseLabel;
    public static RemoteDBGrpc.RemoteDBBlockingStub blockingStub;
    private static ManagedChannel channel;


    public static DBMS getInstance(){
        if (instance == null){
            instance = new DBMS();

            instance.frame = new JFrame("DBMS");
            instance.menuBar = new JMenuBar();
            instance.tabbedPane = new JTabbedPane();
            instance.deleteTableMenuItem = new JMenuItem("Видалити");
            instance.addRowMenuItem = new JMenuItem("Додати");
            instance.addColumnMenuItem = new JMenuItem("Додати");
            instance.deleteRowMenuItem = new JMenuItem("Видалити");
            instance.deleteColumnMenuItem = new JMenuItem("Видалити");
            instance.createTableMenuItem = new JMenuItem("Створити");
            instance.tablesMultiply = new JMenuItem("Перетин таблиць");

        }
        return instance;
    }

    public static void main(String[] args) throws RemoteException {
        DBMS dbms = DBMS.getInstance();
        String target = "localhost:50051";
        if (args.length > 1) {
            target = args[1];
        }
        channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
        blockingStub = RemoteDBGrpc.newBlockingStub(channel);

        dbms.tableMenu.add(dbms.createTableMenuItem);
        dbms.tableMenu.add(dbms.deleteTableMenuItem);
        dbms.tableMenu.add(dbms.tablesMultiply);

        dbms.columnMenu.add(dbms.addColumnMenuItem);
        dbms.columnMenu.add(dbms.deleteColumnMenuItem);

        dbms.rowMenu.add(dbms.addRowMenuItem);
        dbms.rowMenu.add(dbms.deleteRowMenuItem);

        dbms.menuBar.add(dbms.tableMenu);
        dbms.menuBar.add(dbms.columnMenu);
        dbms.menuBar.add(dbms.rowMenu);

        dbms.databaseLabel = new JLabel(databaseName, SwingConstants.CENTER);
        dbms.frame.setSize(800, 600);
        dbms.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dbms.frame.setJMenuBar(DBMS.instance.menuBar);
        dbms.frame.getContentPane().add(DBMS.getInstance().tabbedPane, BorderLayout.CENTER);
        dbms.frame.getContentPane().add(dbms.databaseLabel, BorderLayout.NORTH);
        dbms.frame.setLocationRelativeTo(null);
        dbms.frame.setVisible(true);


        dbms.createTableMenuItem.addActionListener(e -> {
            String tableName = JOptionPane.showInputDialog(dbms.frame, "Введіть назву нової таблиці:");
            boolean result = false;
            CreateTableResponse createTableResponse = blockingStub.createTable(CreateTableRequest.newBuilder().setName(tableName).build());
            result = createTableResponse.getSuccess();
            if (result) {
                addTable(tableName);
            } else {
                System.out.println("Table creation Error: " + tableName);
            }
        });

        dbms.deleteTableMenuItem.addActionListener(e -> {
            int selectedIndex = dbms.tabbedPane.getSelectedIndex();
            boolean result = false;
            DeleteTableResponse deleteTableResponse = blockingStub.deleteTable(DeleteTableRequest.newBuilder().setTableIndex(selectedIndex).build());
            result = deleteTableResponse.getSuccess();
            if (result) {
                tabbedPane.removeTabAt(selectedIndex);
            } else {
                System.out.println("Table delete Error: " + selectedIndex);
            }
        });

        dbms.addColumnMenuItem.addActionListener(e -> {
            boolean result = false;
            int selectedTab = dbms.tabbedPane.getSelectedIndex();
            if (selectedTab != -1) {
                String newColumnName = JOptionPane.showInputDialog(dbms.frame, "Введіть назву нової колонки:");

                if (newColumnName != null && !newColumnName.isEmpty()) {
                    ColumnType selectedDataType = (ColumnType) JOptionPane.showInputDialog(
                            dbms.frame,
                            "Оберіть тип нової колонки:",
                            "Додати Колонку",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            ColumnType.values(),
                            ColumnType.INT
                    );

                    if (selectedDataType != null) {
                        try {
                            if (selectedDataType == ColumnType.COLORINVL) {
                                DoubleInput dialog = new DoubleInput();
                                DoubleInput.InputResult doubleInputResult = dialog.showInputDialog();
                                if (doubleInputResult != null) {
                                    String min = doubleInputResult.getMin();
                                    String max = doubleInputResult.getMax();
                                    AddColumnResponse addColumnResponse = blockingStub.addColumn(AddColumnRequest.newBuilder()
                                            .setTableIndex(selectedTab)
                                            .setName(newColumnName)
                                            .setColumnType(org.example.ColumnType.valueOf(String.valueOf(selectedDataType)))
                                            .setMin(min)
                                            .setMax(max)
                                            .build());
                                    result = addColumnResponse.getSuccess();
                                }
                            } else {

                                AddColumnResponse addColumnResponse = blockingStub.addColumn(AddColumnRequest.newBuilder()
                                        .setTableIndex(selectedTab)
                                        .setName(newColumnName)
                                        .setColumnType(org.example.ColumnType.valueOf(String.valueOf(selectedDataType)))
                                        .build());
                                result = addColumnResponse.getSuccess();
                            }
                        }
                        catch (Exception ex){

                        }
                        if (result){
                            addColumn(selectedTab, newColumnName, selectedDataType.name());
                        } else {
                            System.out.println("Add Column Error: tab" + selectedTab);
                        }
                    }
                }
            }
        });


        dbms.deleteColumnMenuItem.addActionListener(e -> {
            int selectedTab = dbms.tabbedPane.getSelectedIndex();
            if (selectedTab != -1) {
                JPanel tablePanel = (JPanel) dbms.tabbedPane.getComponentAt(selectedTab);
                JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
                JTable table = (JTable) scrollPane.getViewport().getView();
                CustomTableModel tableModel = (CustomTableModel) table.getModel();

                int selectedColumn = table.getSelectedColumn();
                boolean result = false;
                DeleteColumnResponse deleteColumnResponse = blockingStub.deleteColumn(DeleteColumnRequest.newBuilder()
                        .setTableIndex(selectedTab)
                        .setColumnIndex(selectedColumn)
                        .build());
                result = deleteColumnResponse.getSuccess();
                if(result){
                    tableModel.removeColumn(selectedColumn);
                } else {
                    System.out.println("Delete Column Error: tab" + selectedTab);
                }
            }
        });

        dbms.addRowMenuItem.addActionListener(e -> {
            int selectedTab = dbms.tabbedPane.getSelectedIndex();
            boolean result = false;
            AddRowResponse addRowResponse = blockingStub.addRow(AddRowRequest.newBuilder().setTableIndex(selectedTab).build());
            result = addRowResponse.getSuccess();
            if(result) {
                JPanel tablePanel = (JPanel) tabbedPane.getComponentAt(selectedTab);
                JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
                JTable table = (JTable) scrollPane.getViewport().getView();
                CustomTableModel tableModel = (CustomTableModel) table.getModel();
                tableModel.addRow(new Object[tableModel.getColumnCount()]);
            } else {
                System.out.println("Add Row Error: tab" + selectedTab);
            }
        });

        dbms.deleteRowMenuItem.addActionListener(e -> {
            int selectedTab = dbms.tabbedPane.getSelectedIndex();
            if (selectedTab != -1) {
                JPanel tablePanel = (JPanel) dbms.tabbedPane.getComponentAt(selectedTab);
                JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
                JTable table = (JTable) scrollPane.getViewport().getView();
                CustomTableModel tableModel = (CustomTableModel) table.getModel();

                int selectedRow = table.getSelectedRow();
                boolean result = false;
                DeleteRowResponse deleteRowResponse = blockingStub.deleteRow(DeleteRowRequest.newBuilder()
                        .setTableIndex(selectedTab)
                        .setRowIndex(selectedRow)
                        .build());
                result = deleteRowResponse.getSuccess();
                if(result) {
                    tableModel.removeRow(selectedRow);
                } else {
                    System.out.println("Delete Row Error: tab" + selectedTab);
                }
            }
        });


        dbms.tablesMultiply.addActionListener(e -> {
            int selectedTab = instance.tabbedPane.getSelectedIndex();
            List<TableData> selectorTables = new ArrayList<>();
            List<TableData> tableData = null;
            tableData = getTableData();

            for (int i = 0; i < tableData.size(); i++){
                if (i != selectedTab) selectorTables.add(tableData.get(i));
            }
            List<String> tableNames = new ArrayList<>();
            for (TableData table : selectorTables) {
                tableNames.add(table.name);
            }
            String[] tableNamesArray = tableNames.toArray(new String[0]);
            String tableName = (String) JOptionPane.showInputDialog(
                    instance.frame,
                    "Choose second table for intersection:",
                    "Tables intersection",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    tableNamesArray,
                    tableNamesArray[0]
            );

            int intersectTable = -1;
            for (int i = 0; i < tableData.size(); i++){
                if (tableData.get(i).name.equals(tableName)) intersectTable = i;
            }

            if (selectedTab != -1 && intersectTable != -1) {
                boolean response = false;
                TablesIntersectionResponse tablesIntersectionResponse= blockingStub
                        .tablesIntersection(TablesIntersectionRequest.newBuilder().setTableIndex1(selectedTab).setTableIndex2(intersectTable).build());
                response = tablesIntersectionResponse.getSuccess();
                if (response) {
                    List<TableData> tableData1 = null;
                        tableData1 = getTableData();
                    addTable(tableData1.get(tableData1.size()-1).name);
                    List<Column> columns = null;
                        columns = getGetColumnsResponse(tableData1.size()-1);
                    for (int i = 0; i < columns.size(); i++) {
                        addColumn(tableData1.size()-1,columns.get(i).name,columns.get(i).getType());
                    }
                    JPanel tablePanel = (JPanel) dbms.tabbedPane.getComponentAt(tableData1.size()-1);
                    JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
                    JTable table = (JTable) scrollPane.getViewport().getView();
                    CustomTableModel tableModel = (CustomTableModel) table.getModel();
                    List<Row> rows = null;
                        rows = getRows(tableData1.size()-1);
                    for (int i = 0; i < rows.size(); i++) {
                            Object[] rowData = new Object[columns.size()];
                            for (int i1 = 0; i1 < rowData.length; i1++) {
                                rowData[i1] = rows.get(i).values.get(i1);
                            }
                            tableModel.addRow(rowData);
                    }
                    JOptionPane.showMessageDialog(DBMS.instance.frame, "Intersection found!");
                }
            }
        });

        updateDB();
    }
    private static void addColumn(int selectedTab, String newColumnName, String selectedDataType) {
        JPanel tablePanel = (JPanel) tabbedPane.getComponentAt(selectedTab);
        JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
        JTable table = (JTable) scrollPane.getViewport().getView();
        CustomTableModel tableModel = (CustomTableModel) table.getModel();

        tableModel.addColumn(newColumnName + " (" + selectedDataType + ")");
    }

    public static void updateDB() throws RemoteException {
        clearTables();
        List<TableData> tableData = getTableData();
        for (int tableIndex = 0; tableIndex < tableData.size(); tableIndex++) {
            addTable(tableData.get(tableIndex).name);

            List<Column> columns = getGetColumnsResponse(tableIndex);
            for (int i1 = 0; i1 < columns.size(); i1++) {
                Column column = columns.get(i1);
                addColumn(tableIndex, column.name, column.type);
            }
            JPanel tablePanel = (JPanel) tabbedPane.getComponentAt(tableIndex);
            JScrollPane scrollPane = (JScrollPane) tablePanel.getComponent(0);
            JTable table = (JTable) scrollPane.getViewport().getView();
            CustomTableModel tableModel = (CustomTableModel) table.getModel();
            List<Row> rows = getRows(tableIndex);
            for (int i = 0; i < rows.size(); i++) {
                Object[] rowData = new Object[columns.size()];
                for (int i1 = 0; i1 < rowData.length; i1++) {
                    rowData[i1] = rows.get(i).values.get(i1);
                }
                tableModel.addRow(rowData);
            }
        }
    }

    private static void clearTables() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.removeTabAt(0);
        }
    }


    private static List<com.example.component.TableData> getTableData() {
        GetTablesDataRequest getTablesDataRequest = GetTablesDataRequest.newBuilder().build();
        GetTablesDataResponse getTablesDataResponse = blockingStub.getTablesData(getTablesDataRequest);
        List<TableData> tableData = new ArrayList<>();
        for (int i = 0; i < getTablesDataResponse.getTablesDataCount(); i++) {
            tableData.add(new TableData(getTablesDataResponse.getTablesData(i).getName(),getTablesDataResponse.getTablesData(i).getIndex()));
        }
        return tableData;
    }

    private static List<com.example.component.Column> getGetColumnsResponse(int tableIndex) {
        GetColumnsRequest getColumnsRequest = GetColumnsRequest.newBuilder().setTableIndex(tableIndex).build();
        GetColumnsResponse getColumnsResponse = blockingStub.getColumns(getColumnsRequest);
        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < getColumnsResponse.getColumnsCount(); i++) {
            switch (ColumnType.valueOf(getColumnsResponse.getColumns(i).getType().name())) {
                case INT -> {
                    Column columnInt = new IntegerColumn(getColumnsResponse.getColumns(i).getName());
                    columns.add(columnInt);
                }
                case REAL -> {
                    Column columnReal = new RealColumn(getColumnsResponse.getColumns(i).getName());
                    columns.add(columnReal);
                }
                case STRING -> {
                    Column columnStr = new StringColumn(getColumnsResponse.getColumns(i).getName());
                    columns.add(columnStr);
                }
                case CHAR -> {
                    Column columnChar = new CharColumn(getColumnsResponse.getColumns(i).getName());
                    columns.add(columnChar);
                }
                case COLOR -> {
                    Column complexIntegerColumn = new ColorColumn(getColumnsResponse.getColumns(i).getName());
                    columns.add(complexIntegerColumn);
                }
                case COLORINVL -> {
                    Column complexRealColumn = new ColorInvlColumn(getColumnsResponse.getColumns(i).getName(),
                            getColumnsResponse.getColumns(i).getMin(),getColumnsResponse.getColumns(i).getMax());
                    columns.add(complexRealColumn);
                }
            }
        }
        return columns;
    }


    static List<com.example.component.Row> getRows(int tableIndex) {
        GetRowsRequest getRowsRequest = GetRowsRequest.newBuilder().setTableIndex(tableIndex).build();
        GetRowsResponse getRowsResponse = blockingStub.getRows(getRowsRequest);
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < getRowsResponse.getRowsList().size(); i++) {
            rows.add(new Row());
            rows.get(i).values = getRowsResponse.getRows(i).getValuesList();
        }
        return rows;
    }

    public static JPanel createTablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();

        CustomTable table = new CustomTable(model);

        DefaultCellEditor cellEditor = new DefaultCellEditor(new JTextField());

        cellEditor.addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = table.getSelectedColumn();
                Object updatedValue = table.getValueAt(selectedRow, selectedColumn);
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
            }
        });

        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            table.getColumnModel().getColumn(columnIndex).setCellEditor(cellEditor);
        }

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        CustomTableModel tableModel = new CustomTableModel ();

        table.setModel(tableModel);

        return panel;
    }

    public static void addTable(String name){
        if (name != null && !name.isEmpty()) {
            tabbedPane.addTab(name, createTablePanel());
        }
    }


}