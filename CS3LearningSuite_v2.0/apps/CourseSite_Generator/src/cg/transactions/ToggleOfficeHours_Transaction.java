package cg.transactions;

import jtps.jTPS_Transaction;
import cg.data.CGData;
import cg.data.TeachingAssistantPrototype;
import cg.data.TimeSlot;
import cg.data.TimeSlot.DayOfWeek;

/**
 *
 * @author McKillaGorilla
 */
public class ToggleOfficeHours_Transaction implements jTPS_Transaction {
    CGData data;
    TimeSlot timeSlot;
    DayOfWeek dow;
    TeachingAssistantPrototype ta;
    
    public ToggleOfficeHours_Transaction(   CGData initData, 
                                            TimeSlot initTimeSlot,
                                            DayOfWeek initDOW,
                                            TeachingAssistantPrototype initTA) {
        data = initData;
        timeSlot = initTimeSlot;
        dow = initDOW;
        ta = initTA;
    }

    @Override
    public void doTransaction() {
        timeSlot.toggleTA(dow, ta);
    }

    @Override
    public void undoTransaction() {
        timeSlot.toggleTA(dow, ta);
    }
}