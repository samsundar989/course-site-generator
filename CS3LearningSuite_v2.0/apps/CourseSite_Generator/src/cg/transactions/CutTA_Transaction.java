package cg.transactions;

import jtps.jTPS_Transaction;
import static djf.AppPropertyType.APP_CLIPBOARD_FOOLPROOF_SETTINGS;
import java.util.ArrayList;
import java.util.HashMap;
import cg.CGApp;
import cg.data.CGData;
import cg.data.TeachingAssistantPrototype;
import cg.data.TimeSlot;
import cg.data.TimeSlot.DayOfWeek;

public class CutTA_Transaction implements jTPS_Transaction {
    CGApp app;
    TeachingAssistantPrototype taToCut;
    HashMap<TimeSlot, ArrayList<DayOfWeek>> officeHours;

    public CutTA_Transaction(CGApp initApp, 
            TeachingAssistantPrototype initTAToCut, 
            HashMap<TimeSlot, ArrayList<DayOfWeek>> initOfficeHours) {
        app = initApp;
        taToCut = initTAToCut;
        officeHours = initOfficeHours;
    }

    @Override
    public void doTransaction() {
        CGData data = (CGData)app.getDataComponent();
        data.removeTA(taToCut, officeHours);
        app.getFoolproofModule().updateControls(APP_CLIPBOARD_FOOLPROOF_SETTINGS);
    }

    @Override
    public void undoTransaction() {
        CGData data = (CGData)app.getDataComponent();
        data.addTA(taToCut, officeHours);
        app.getFoolproofModule().updateControls(APP_CLIPBOARD_FOOLPROOF_SETTINGS);
    }   
}