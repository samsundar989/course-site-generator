package cg.workspace.controllers;

import djf.modules.AppGUIModule;
import djf.ui.dialogs.AppDialogsFacade;
import javafx.collections.ObservableList;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import cg.CGApp;
import static cg.CGPropertyType.CG_EMAIL_TEXT_FIELD;
import static cg.CGPropertyType.CG_END_TIME_COMBOBOX;
import static cg.CGPropertyType.CG_FOOLPROOF_SETTINGS;
import static cg.CGPropertyType.CG_NAME_TEXT_FIELD;
import static cg.CGPropertyType.CG_NO_TA_SELECTED_CONTENT;
import static cg.CGPropertyType.CG_NO_TA_SELECTED_TITLE;
import static cg.CGPropertyType.CG_OFFICE_HOURS_TABLE_VIEW;
import static cg.CGPropertyType.CG_START_TIME_COMBOBOX;
import static cg.CGPropertyType.CG_TAS_TABLE_VIEW;
import static cg.CGPropertyType.CG_TA_EDIT_DIALOG;
import cg.data.CGData;
import cg.data.TAType;
import cg.data.TeachingAssistantPrototype;
import cg.data.TimeSlot;
import cg.data.TimeSlot.DayOfWeek;
import cg.transactions.AddTA_Transaction;
import cg.transactions.EditTA_Transaction;
import cg.transactions.ToggleOfficeHours_Transaction;
import cg.workspace.dialogs.TADialog;
import javafx.scene.control.ComboBox;

/**
 *
 * @author McKillaGorilla
 */
public class CGController {

    CGApp app;

    public CGController(CGApp initApp) {
        app = initApp;
    }

    public void processAddTA() {
        AppGUIModule gui = app.getGUIModule();
        TextField nameTF = (TextField) gui.getGUINode(CG_NAME_TEXT_FIELD);
        String name = nameTF.getText();
        TextField emailTF = (TextField) gui.getGUINode(CG_EMAIL_TEXT_FIELD);
        String email = emailTF.getText();
        CGData data = (CGData) app.getDataComponent();
        TAType type = data.getSelectedType();
        if (data.isLegalNewTA(name, email)) {
            TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name.trim(), email.trim(), type);
            AddTA_Transaction addTATransaction = new AddTA_Transaction(data, ta);
            app.processTransaction(addTATransaction);

            // NOW CLEAR THE TEXT FIELDS
            nameTF.setText("");
            emailTF.setText("");
            nameTF.requestFocus();
        }
        app.getFoolproofModule().updateControls(CG_FOOLPROOF_SETTINGS);
    }

    public void processVerifyTA() {

    }

    public void processToggleOfficeHours() {
        AppGUIModule gui = app.getGUIModule();
        TableView<TimeSlot> officeHoursTableView = (TableView) gui.getGUINode(CG_OFFICE_HOURS_TABLE_VIEW);
        ObservableList<TablePosition> selectedCells = officeHoursTableView.getSelectionModel().getSelectedCells();
        if (selectedCells.size() > 0) {
            TablePosition cell = selectedCells.get(0);
            int cellColumnNumber = cell.getColumn();
            CGData data = (CGData)app.getDataComponent();
            if (data.isDayOfWeekColumn(cellColumnNumber)) {
                DayOfWeek dow = data.getColumnDayOfWeek(cellColumnNumber);
                TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(CG_TAS_TABLE_VIEW);
                TeachingAssistantPrototype ta = taTableView.getSelectionModel().getSelectedItem();
                if (ta != null) {
                    TimeSlot timeSlot = officeHoursTableView.getSelectionModel().getSelectedItem();
                    ToggleOfficeHours_Transaction transaction = new ToggleOfficeHours_Transaction(data, timeSlot, dow, ta);
                    app.processTransaction(transaction);
                }
                else {
                    Stage window = app.getGUIModule().getWindow();
                    AppDialogsFacade.showMessageDialog(window, CG_NO_TA_SELECTED_TITLE, CG_NO_TA_SELECTED_CONTENT);
                }
            }
            int row = cell.getRow();
            cell.getTableView().refresh();
        }
    }

    public void processTypeTA() {
        app.getFoolproofModule().updateControls(CG_FOOLPROOF_SETTINGS);
    }

    public void processEditTA() {
        CGData data = (CGData)app.getDataComponent();
        if (data.isTASelected()) {
            TeachingAssistantPrototype taToEdit = data.getSelectedTA();
            TADialog taDialog = (TADialog)app.getGUIModule().getDialog(CG_TA_EDIT_DIALOG);
            taDialog.showEditDialog(taToEdit);
            TeachingAssistantPrototype editTA = taDialog.getEditTA();
            if (editTA != null) {
                EditTA_Transaction transaction = new EditTA_Transaction(taToEdit, editTA.getName(), editTA.getEmail(), editTA.getType());
                app.processTransaction(transaction);
            }
        }
    }

    public void processSelectAllTAs() {
        CGData data = (CGData)app.getDataComponent();
        data.selectTAs(TAType.All);
    }

    public void processSelectGradTAs() {
        CGData data = (CGData)app.getDataComponent();
        data.selectTAs(TAType.Graduate);
    }

    public void processSelectUndergradTAs() {
        CGData data = (CGData)app.getDataComponent();
        data.selectTAs(TAType.Undergraduate);
    }

    public void processSelectTA() {
        AppGUIModule gui = app.getGUIModule();
        TableView<TimeSlot> officeHoursTableView = (TableView) gui.getGUINode(CG_OFFICE_HOURS_TABLE_VIEW);
        officeHoursTableView.refresh();
    }
    public void processRemoveTA(){
        CGData data = (CGData)app.getDataComponent();
        AppGUIModule gui = app.getGUIModule();
        TableView tasTableView = (TableView) gui.getGUINode(CG_TAS_TABLE_VIEW);
        data.removeTA((TeachingAssistantPrototype)tasTableView.getSelectionModel().getSelectedItem());
    }
    
    public void processTimeRange(){
        CGData data = (CGData)app.getDataComponent();
        AppGUIModule gui = app.getGUIModule();
        ComboBox startTime = (ComboBox) gui.getGUINode(CG_START_TIME_COMBOBOX);
        ComboBox endTime = (ComboBox) gui.getGUINode(CG_END_TIME_COMBOBOX);
        String start = (String) startTime.getSelectionModel().getSelectedItem();
        String end = (String) endTime.getSelectionModel().getSelectedItem();
        String[] starthelp = start.split(":");
        String[] endhelp = end.split(":");
        String startZone = starthelp[1];
        String endZone = endhelp[1];
        start = starthelp[0];
        end = endhelp[0];
        
        if(startZone.contains("pm")){
            start=starthelp[0];
            int x = Integer.parseInt(start);
            x=x+12;
            start = Integer.toString(x);
        }
        if(endZone.contains("pm")){
            end=endhelp[0];
            int x = Integer.parseInt(end);
            x=x+12;
            end= Integer.toString(x);
        }
        
    }

}