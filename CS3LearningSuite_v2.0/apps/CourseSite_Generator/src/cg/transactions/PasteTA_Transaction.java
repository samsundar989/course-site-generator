package cg.transactions;

import jtps.jTPS_Transaction;
import cg.CGApp;
import cg.data.CGData;
import cg.data.TeachingAssistantPrototype;

public class PasteTA_Transaction implements jTPS_Transaction {
    CGApp app;
    TeachingAssistantPrototype taToPaste;

    public PasteTA_Transaction(  CGApp initApp, 
                                 TeachingAssistantPrototype initTAToPaste) {
        app = initApp;
        taToPaste = initTAToPaste;
    }

    @Override
    public void doTransaction() {
        CGData data = (CGData)app.getDataComponent();
        data.addTA(taToPaste);
    }

    @Override
    public void undoTransaction() {
        CGData data = (CGData)app.getDataComponent();
        data.removeTA(taToPaste);
    }   
}