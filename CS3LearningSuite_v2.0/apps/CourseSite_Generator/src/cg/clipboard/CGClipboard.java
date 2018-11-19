package cg.clipboard;

import djf.components.AppClipboardComponent;
import java.util.ArrayList;
import java.util.HashMap;
import cg.CGApp;
import cg.data.CGData;
import cg.data.TeachingAssistantPrototype;
import cg.data.TimeSlot;
import cg.data.TimeSlot.DayOfWeek;
import cg.transactions.CutTA_Transaction;
import cg.transactions.PasteTA_Transaction;


public class CGClipboard implements AppClipboardComponent {
    CGApp app;
    TeachingAssistantPrototype clipboardCutTA;
    TeachingAssistantPrototype clipboardCopiedTA;

    public CGClipboard(CGApp initApp) {
        app = initApp;
        clipboardCutTA = null;
        clipboardCopiedTA = null;
    }

    @Override
    public void cut() {
        CGData data = (CGData)app.getDataComponent();
        if (data.isTASelected()) {
            clipboardCutTA = data.getSelectedTA();
            clipboardCopiedTA = null;
            HashMap<TimeSlot, ArrayList<DayOfWeek>> officeHours = data.getTATimeSlots(clipboardCutTA);
            CutTA_Transaction transaction = new CutTA_Transaction((CGApp)app, clipboardCutTA, officeHours);
            app.processTransaction(transaction);
        }
    }

    @Override
    public void copy() {
        CGData data = (CGData)app.getDataComponent();
        if (data.isTASelected()) {
            TeachingAssistantPrototype tempTA = data.getSelectedTA();
            copyToCopiedClipboard(tempTA);
        }
    }
    
    private void copyToCopiedClipboard(TeachingAssistantPrototype taToCopy) {
        clipboardCutTA = null;
        clipboardCopiedTA = copyTA(taToCopy);
        app.getFoolproofModule().updateAll();        
    }
    
    private TeachingAssistantPrototype copyTA(TeachingAssistantPrototype taToCopy) {
        TeachingAssistantPrototype tempCopy = (TeachingAssistantPrototype)taToCopy.clone(); 
        tempCopy.setName(tempCopy.getName() + "_1");
        tempCopy.setEmail(tempCopy.getEmail() + "_1");
        return tempCopy;
    }

    @Override
    public void paste() {
        CGData data = (CGData)app.getDataComponent();
        if (clipboardCutTA != null) {
            PasteTA_Transaction transaction = new PasteTA_Transaction((CGApp)app, clipboardCutTA);
            app.processTransaction(transaction);
            
            this.clipboardCutTA = null;
            app.getFoolproofModule().updateAll();
        }
        else if (clipboardCopiedTA != null) {
            // FIGURE OUT THE PROPER NAME AND
            // EMAIL ADDRESS SO THAT THERE ISN'T A DUPLICATE             
            PasteTA_Transaction transaction = new PasteTA_Transaction((CGApp)app, clipboardCopiedTA);
            app.processTransaction(transaction);
            
            this.clipboardCopiedTA = null;
            app.getFoolproofModule().updateAll();
        }
    }    

    @Override
    public boolean hasSomethingToCut() {
        return ((CGData)app.getDataComponent()).isTASelected();
    }

    @Override
    public boolean hasSomethingToCopy() {
        return ((CGData)app.getDataComponent()).isTASelected();
    }

    @Override
    public boolean hasSomethingToPaste() {
        if ((clipboardCutTA != null) || (clipboardCopiedTA != null))
            return true;
        else
            return false;
    }
}